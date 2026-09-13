package com.tu.goodsbuy.service;


import com.tu.goodsbuy.controller.param.ProductUpdateParam;
import com.tu.goodsbuy.model.dto.ChatMessage;
import com.tu.goodsbuy.model.dto.ChatRoom;
import com.tu.goodsbuy.repository.param.InsertChatMessageDto;
import com.tu.goodsbuy.repository.ChatRepository;
import com.tu.goodsbuy.repository.param.ChatRoomBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;


    @Transactional(readOnly = true)
    public List<ChatRoom> findAllRoomByUserNo(Long userNo) {
        List<ChatRoom> rooms = chatRepository.findAllRoomsByUserNo(userNo);
        Collections.reverse(rooms);

        return rooms;
    }

    @Transactional(readOnly = true)
    public ChatRoom findRoomByRoomNo(Long roomNo) {
        return chatRepository.findRoomByRoomNo(roomNo).orElseThrow();
    }


    @Transactional
    public boolean isExistChatRoom(Long userNo, String productNo) {
        return chatRepository.isExistChatRoom(userNo, productNo) == 1;
    }

    @Transactional
    public void createChatRoom(ChatRoomBuilder chatRoomBuilder) {
        if (chatRepository.createChatRoom(chatRoomBuilder) == 0) {
            throw new IllegalStateException("Chat room was not created");
        }
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> findAllMessageByChatRoomNo(String roomId) {
        return chatRepository.findAllMessageByChatRoomNo(roomId);
    }


    @Transactional
    public void updateProductInfoChatRoomByProductUpdateParam(ProductUpdateParam productUpdateParam) {

        String productNo = productUpdateParam.getProductNo();
        String productName = productUpdateParam.getProductName();
        String productPrice = productUpdateParam.getProductPrice();

        chatRepository.updateProductInfoChatRoomByProductUpdateParam(productNo, productName, productPrice);
    }


    @Transactional
    public void updateProductImgUrlByProductNo(String imgURL, String productNo) {
        chatRepository.updateProductImgUrlChatRoomByProductNo(imgURL, productNo);
    }

    @Transactional
    public ChatMessage MakeSendMessage(String roomNo, String content, Long senderId, String senderNickname, Long recipientId) {

        InsertChatMessageDto insertChatMessageDto = InsertChatMessageDto.builder().messageNo(null)
                .chatRoomNo(roomNo).content(content).senderId(senderId).senderNickname(senderNickname)
                .recipientId(recipientId).build();

        /*chatRepository.createChatMessage(roomNo, content, senderId, senderNickname, recipientId);*/
        chatRepository.createChatMessage(insertChatMessageDto);


        return chatRepository.getChatMessageByMessageNo(insertChatMessageDto.getMessageNo()).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Long getRecipientIdBySenderNo(Long roomNo, Long senderId) {

        ChatRoom chatRoom = chatRepository.findRoomByRoomNo(roomNo).orElseThrow();

        if (java.util.Objects.equals(senderId, chatRoom.getUserNo())) {
            return chatRoom.getPurchaseNo();
        }

        if (!java.util.Objects.equals(senderId, chatRoom.getPurchaseNo())) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN);
        }
        return chatRoom.getUserNo();
    }


}
