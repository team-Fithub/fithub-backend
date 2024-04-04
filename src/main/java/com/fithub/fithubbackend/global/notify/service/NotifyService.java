package com.fithub.fithubbackend.global.notify.service;

import com.fithub.fithubbackend.global.notify.dto.NotifyDto;
import com.fithub.fithubbackend.global.notify.dto.NotifyRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotifyService {
    void notifyByRequest(NotifyRequestDto dto);

    Long getUnReadNotiNum(Long userId);
    Page<NotifyDto> getAllNotifyForUser(Long userId, Pageable pageable);

    void setStatusRead(Long id);
    void deleteNoti(Long id);
    void deleteNotiAll(List<Long> notiList);
}
