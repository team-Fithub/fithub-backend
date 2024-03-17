package com.fithub.fithubbackend.domain.chat.Handler;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {


    // 메세지 전송 사전 작업 필요 시
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        return message;
    }
}

