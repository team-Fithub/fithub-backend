package com.fithub.fithubbackend.global.notify.service;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.Notify;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.notify.NotificationType;
import com.fithub.fithubbackend.global.notify.dto.NotifyDto;
import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import com.fithub.fithubbackend.global.notify.repository.NotifyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final NotifyRepository notifyRepository;

    @Override
    public void notifyByRequest(NotifyRequestDto dto) {
        notifyRepository.save(createNotify(dto.getReceiver(), dto.getContent(), dto.getUrl(), dto.getType()));
        messagingTemplate.convertAndSend("/topic/alarm/" + dto.getReceiver().getId(), dto.getContent());
    }

    private Notify createNotify(User receiver, String content, String url, NotificationType type) {
        return Notify.builder()
                .receiver(receiver)
                .content(content)
                .url(url)
                .type(type)
                .build();
    }

    @Override
    public Long getUnReadNotiNum(Long userId) {
        return notifyRepository.countByIsReadFalseAndReceiverId(userId);
    }

    @Override
    public Page<NotifyDto> getAllNotifyForUser(Long userId, Pageable pageable) {
        Page<Notify> notifyPage = notifyRepository.findByReceiverId(userId, pageable);
        return notifyPage.map(NotifyDto::new);
    }

    @Override
    @Transactional
    public void setStatusRead(Long id) {
        Notify notify = findNotify(id);
        notify.setStatusToRead();
    }

    @Override
    @Transactional
    public void deleteNoti(Long id) {
        notifyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteNotiAll(List<Long> notiList) {
        List<Notify> notifyList = notifyRepository.findByIdIn(notiList);
        notifyRepository.deleteAll(notifyList);
    }

    private Notify findNotify(Long id) {
        return notifyRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 알림을 찾을 수 없습니다."));
    }
}
