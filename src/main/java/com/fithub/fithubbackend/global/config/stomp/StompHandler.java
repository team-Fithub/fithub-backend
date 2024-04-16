package com.fithub.fithubbackend.global.config.stomp;

import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        String token = accessor.getFirstNativeHeader("Authorization");
        log.info("[StompHandler] - accessor command: {}", accessor.getCommand());
//        if (accessor.getCommand() == StompCommand.CONNECT) {
//            if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
//                token = token.substring(7);
//            }
//            if (!jwtTokenProvider.validateToken(token)) {
//                throw new CustomException(ErrorCode.AUTHENTICATION_ERROR);
//            }
//        }
        return message;
    }
}