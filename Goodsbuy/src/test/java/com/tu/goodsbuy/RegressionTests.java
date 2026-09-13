package com.tu.goodsbuy;

import com.tu.goodsbuy.controller.param.*;
import com.tu.goodsbuy.controller.user.post.EmailPostController;
import com.tu.goodsbuy.global.config.WebSocketConfig;
import com.tu.goodsbuy.global.exception.file.NotImageFileException;
import com.tu.goodsbuy.global.util.filter.*;
import com.tu.goodsbuy.model.dto.*;
import com.tu.goodsbuy.repository.*;
import com.tu.goodsbuy.service.*;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.*;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegressionTests {
    @Test
    void alertTextCannotBreakOutOfScript() throws Exception {
        var response = new MockHttpServletResponse();
        com.tu.goodsbuy.global.util.ScriptWriterUtil.writeAndRedirect(response,
                "</script><script>alert('x')</script>", "/login");
        String html = response.getContentAsString();
        assertFalse(html.contains("<script>alert"));
        assertTrue(html.contains("\\u003c"));
    }

    @Test
    void legacyPasswordIsUpgradedOnlyAfterSuccessfulLogin() {
        var repository = mock(UserRepository.class);
        when(repository.getMemberUserById("user1234"))
                .thenReturn(Optional.of(new MemberUser(1L, "user1234", "Password1!")));
        when(repository.updatePassword(eq(1L), eq("Password1!"), anyString())).thenReturn(1);
        var service = new UserService(repository);
        assertThrows(com.tu.goodsbuy.global.exception.user.DuplicatedLoginIdException.class,
                () -> service.doLogin("user1234", "wrong"));
        verify(repository, never()).updatePassword(any(), any(), any());
        assertNull(service.doLogin("user1234", "Password1!").getUserPwd());
        var hash = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).updatePassword(eq(1L), eq("Password1!"), hash.capture());
        assertTrue(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .matches("Password1!", hash.getValue()));
    }

    @Test
    void passwordsAreHashedAndNeverKeptInLoginSession() {
        var repository = mock(UserRepository.class);
        when(repository.makeMemberUser(eq("user1234"), anyString())).thenReturn(1);
        var service = new UserService(repository);
        service.makeMemberUser("user1234", "Password1!");
        var hash = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).makeMemberUser(eq("user1234"), hash.capture());
        assertNotEquals("Password1!", hash.getValue());
        when(repository.getMemberUserById("user1234"))
                .thenReturn(Optional.of(new MemberUser(1L, "user1234", hash.getValue())));
        assertNull(service.doLogin("user1234", "Password1!").getUserPwd());
        assertThrows(com.tu.goodsbuy.global.exception.user.DuplicatedLoginIdException.class,
                () -> service.doLogin("user1234", "wrong"));
    }

    @Test
    void filtersHandleMissingSessionAndContinueOnlyOnce() throws Exception {
        var request = new MockHttpServletRequest("GET", "/profile");
        var response = new MockHttpServletResponse();
        var chain = mock(FilterChain.class);
        new LoginCheckFilter().doFilter(request, response, chain);
        verifyNoInteractions(chain);
        assertTrue(response.getContentAsString().contains("/login"));
        new LoginSessionFilter().doFilter(request, new MockHttpServletResponse(), chain);
        verify(chain).doFilter(eq(request), any());
        request.getSession().setAttribute("loginMember", new MemberUser(1L, "user", null));
        chain = mock(FilterChain.class);
        new LoginCheckFilter().doFilter(request, new MockHttpServletResponse(), chain);
        verify(chain, times(1)).doFilter(eq(request), any());
    }

    @Test
    void formFieldsBindFromHttpParameters() {
        var form = new LoginForm(null, null);
        var request = new MockHttpServletRequest();
        request.addParameter("username", "user1234");
        request.addParameter("password", "Password1!");
        new ServletRequestDataBinder(form).bind(request);
        assertEquals("user1234", form.getUsername());
        assertEquals("Password1!", form.getPassword());
    }

    @Test
    void invalidOrExpiredEmailCodeNeverVerifiesProfile() throws Exception {
        var profile = mock(ProfileService.class);
        var controller = new EmailPostController(profile);
        var request = new MockHttpServletRequest();
        controller.checkEmailCode("wrong", request, new MockHttpServletResponse());
        request.getSession().setAttribute("sendCode", "correct");
        request.getSession().setAttribute("sendCodeExpiresAt", System.currentTimeMillis() + 60_000);
        controller.checkEmailCode("wrong", request, new MockHttpServletResponse());
        request.getSession().setAttribute("sendCodeExpiresAt", 0L);
        controller.checkEmailCode("correct", request, new MockHttpServletResponse());
        verifyNoInteractions(profile);
    }

    @Test
    void validEmailCodeVerifiesOnce() throws Exception {
        var profile = mock(ProfileService.class);
        var controller = new EmailPostController(profile);
        var request = new MockHttpServletRequest();
        request.getSession().setAttribute("loginMember", new MemberUser(1000L, "user", null));
        request.getSession().setAttribute("sendCode", "correct");
        request.getSession().setAttribute("sendEmail", "user@example.com");
        request.getSession().setAttribute("sendCodeExpiresAt", System.currentTimeMillis() + 60_000);
        controller.checkEmailCode("correct", request, new MockHttpServletResponse());
        verify(profile).setEmailVerificationStatus(1000L);
        verify(profile).setEmailProfileByUserNo("user@example.com", 1000L);
        assertNull(request.getSession().getAttribute("sendCode"));
    }

    @Test
    void recipientUsesIdValueAndRejectsOutsiders() {
        var repository = mock(ChatRepository.class);
        var room = mock(ChatRoom.class);
        when(room.getUserNo()).thenReturn(Long.valueOf("1000"));
        when(room.getPurchaseNo()).thenReturn(2000L);
        when(repository.findRoomByRoomNo(1L)).thenReturn(Optional.of(room));
        var service = new ChatService(repository);
        assertEquals(2000L, service.getRecipientIdBySenderNo(1L, Long.valueOf("1000")));
        assertEquals(1000L, service.getRecipientIdBySenderNo(1L, 2000L));
        assertThrows(ResponseStatusException.class, () -> service.getRecipientIdBySenderNo(1L, 3000L));
    }

    @Test
    void productWithoutChatRoomsCanBeUpdated() {
        var service = new ChatService(mock(ChatRepository.class));
        assertDoesNotThrow(() -> service.updateProductInfoChatRoomByProductUpdateParam(
                new ProductUpdateParam("1", "name", "100", "description")));
    }

    @Test
    void onlyProductOwnerCanMutateProduct() {
        var repository = mock(ProductRepository.class);
        var product = mock(Product.class);
        when(product.getUserNo()).thenReturn(1000L);
        when(repository.getProductListByProductNo(1L)).thenReturn(Optional.of(product));
        var service = new ProductService(repository);
        assertDoesNotThrow(() -> service.requireOwner("1", 1000L));
        assertThrows(ResponseStatusException.class, () -> service.requireOwner("1", 2000L));
        assertThrows(ResponseStatusException.class, () -> service.requireOwner("1", null));
    }

    @Test
    void uploadChecksContentAndContainsPaths(@TempDir Path directory) throws Exception {
        var service = new ProfileService(mock(UserRepository.class), mock(ProfileRepository.class));
        var fake = new MockMultipartFile("file", "fake.png", "image/png", "not an image".getBytes());
        assertThrows(NotImageFileException.class, () -> service.uploadSaveImageAndGetIdentifier(directory.toString(), fake));
        assertThrows(NotImageFileException.class, () -> service.uploadSaveImageAndGetIdentifier(directory.toString(), null));
        var image = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        var bytes = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(image, "png", bytes);
        var file = new MockMultipartFile("file", "../../outside.png", null, bytes.toByteArray());
        String id = service.uploadSaveImageAndGetIdentifier(directory.toString(), file);
        assertTrue(Files.exists(directory.resolve(id)));
        assertFalse(id.contains(".."));
        assertThrows(IllegalArgumentException.class, () -> service.deleteImage(directory.toString(), "../outside.png"));
        service.deleteImage(directory.toString(), id);
        assertFalse(Files.exists(directory.resolve(id)));
    }

    @Test
    void websocketRejectsGlobalSubscriptionsAndChecksMembership() {
        var service = mock(ChatService.class);
        var registration = new ChannelRegistration() {
            public java.util.List<org.springframework.messaging.support.ChannelInterceptor> interceptors() {
                return getInterceptors();
            }
        };
        new WebSocketConfig(service).configureClientInboundChannel(registration);
        var interceptor = registration.interceptors().get(0);
        var headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSessionAttributes(Map.of("loginMember", new MemberUser(1000L, "user", null)));
        headers.setDestination("/sub/message");
        var global = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(global, null));
        headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSessionAttributes(Map.of("loginMember", new MemberUser(1000L, "user", null)));
        headers.setDestination("/sub/chat/1");
        var room = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        assertNotNull(interceptor.preSend(room, null));
        verify(service).getRecipientIdBySenderNo(1L, 1000L);
        doThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN))
                .when(service).getRecipientIdBySenderNo(1L, 1000L);
        assertThrows(ResponseStatusException.class, () -> interceptor.preSend(room, null));
    }
}
