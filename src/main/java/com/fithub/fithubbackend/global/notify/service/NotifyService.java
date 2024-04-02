package com.fithub.fithubbackend.global.notify.service;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.Notify;
import com.fithub.fithubbackend.global.notify.NotificationType;
import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import com.fithub.fithubbackend.global.notify.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotifyService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotifyRepository notifyRepository;

    public void notifyByRequest(NotifyRequestDto dto) {
        notifyRepository.save(createNotify(dto.getReceiver(), dto.getContent(), dto.getUrl(), dto.getType()));
        messagingTemplate.convertAndSend("/topic/alarm" + dto.getReceiver().getId(), dto.getContent());
    }

    private Notify createNotify(User receiver, String content, String url, NotificationType type) {
        return Notify.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .type(type)
                .build();
    }
}
