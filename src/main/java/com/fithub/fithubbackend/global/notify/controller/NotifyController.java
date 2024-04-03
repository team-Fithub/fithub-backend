package com.fithub.fithubbackend.global.notify.controller;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.notify.dto.NotifyDto;
import com.fithub.fithubbackend.global.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notify")
public class NotifyController {

    private final NotifyService notifyService;

    @GetMapping("/count")
    public ResponseEntity<Long> getUnReadNotiNum(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(notifyService.getUnReadNotiNum(user.getId()));
    }
    @GetMapping("/all")
    public ResponseEntity<Page<NotifyDto>> getAllNotifyForUsers(@AuthUser User user,
                                                                @PageableDefault(sort="id", size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(notifyService.getAllNotifyForUser(user.getId(), pageable));
    }

    @PutMapping("/select/status/read")
    public ResponseEntity<String> setStatusRead(@AuthUser User user, @RequestParam Long notifyId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.setStatusRead(notifyId);
        return ResponseEntity.ok().body("완료");
    }

    @DeleteMapping("/select/delete")
    public ResponseEntity<String> deleteNotify(@AuthUser User user, @RequestParam Long notifyId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.deleteNoti(notifyId);
        return ResponseEntity.ok().body("완료");
    }

    @DeleteMapping("/select/delete/list")
    public ResponseEntity<String> deleteNotifyAll(@AuthUser User user, @RequestParam List<Long> notiList) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.deleteNotiAll(notiList);
        return ResponseEntity.ok().body("완료");
    }
}
