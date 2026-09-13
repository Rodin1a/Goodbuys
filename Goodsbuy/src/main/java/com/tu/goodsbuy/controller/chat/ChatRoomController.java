package com.tu.goodsbuy.controller.chat;

import com.tu.goodsbuy.model.dto.ChatMessage;
import com.tu.goodsbuy.model.dto.ChatRoom;
import com.tu.goodsbuy.model.dto.MemberUser;
import com.tu.goodsbuy.service.ChatService;
import com.tu.goodsbuy.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;
    private final ProfileService profileService;


    @GetMapping("/chat")
    public String getChat(@SessionAttribute(value = "loginMember") MemberUser loginMember, Model model) {

        List<ChatRoom> rooms = chatService.findAllRoomByUserNo(loginMember.getUserNo());
        model.addAttribute("chatroom", rooms);

        return "chat/chatRoom";
    }


    // 채팅방 입장
    // /pub/enter/{roomId} request 요청
    // /sub/chat/{roomId} response
    @MessageMapping("/enter/{roomId}")
    @SendTo("/sub/messages/{roomId}")
    public List<ChatMessage> enter(@DestinationVariable("roomId") String roomId, Map<String, String> memberId) {

        log.info("enter room : " + roomId + ", member : " + memberId.get("memberId"));

        return chatService.findAllMessageByChatRoomNo(roomId);
    }


    //채팅 주고받기
    // /pub/enter/{roomId} request 요청
    // /sub/chat/{roomId} response
    @MessageMapping("/chat/{roomId}")
    @SendTo("/sub/chat/{roomId}")
    public ChatMessage sendChat(@DestinationVariable("roomId") String roomId, Map<String, String> data,
                                SimpMessageHeaderAccessor headers) {
        String content = data.get("message");
        MemberUser member = (MemberUser) headers.getSessionAttributes().get("loginMember");
        String userNo = member.getUserNo().toString();
        if (content == null || content.isBlank() || content.length() > 4000) {
            throw new IllegalArgumentException("Message must contain 1 to 4000 characters");
        }


        //return chatMessage
        return chatService.MakeSendMessage(roomId, content, Long.valueOf(userNo),
                profileService.getNicknameByUserNo(Long.valueOf(userNo)),
                chatService.getRecipientIdBySenderNo(Long.valueOf(roomId), Long.valueOf(userNo)));
    }


    // 채팅방 render
    @ResponseBody
    @PostMapping("/chat/render")
    public ResponseEntity<ChatRoom> renderChatRoom(@RequestBody Map<String, String> roomId,
                                                 @SessionAttribute("loginMember") MemberUser member) {
        chatService.getRecipientIdBySenderNo(Long.valueOf(roomId.get("roomId")), member.getUserNo());
        ChatRoom chatRoom = chatService.findRoomByRoomNo(Long.valueOf(roomId.get("roomId")));
        return ResponseEntity.ok(chatRoom);
    }


    @GetMapping("/alertChat")
    public String getAlertChat() {
        return "chat/alertChat";
    }

    @GetMapping("/alertChat2")
    public String getAlertChat2() {
        return "chat/alertChat2";
    }
}
