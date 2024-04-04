package com.fithub.fithubbackend.global.notify.controller;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.notify.dto.NotifyDto;
import com.fithub.fithubbackend.global.notify.service.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notify 알림 관련 api", description = "알림 관련 api")
public class NotifyController {

    private final NotifyService notifyService;


    @Operation(summary = "읽지 않은 알림 개수 확인", responses = {
            @ApiResponse(responseCode = "200", description = "읽지 않은 알림 개수 반환"),
    })
    @GetMapping("/count")
    public ResponseEntity<Long> getUnReadNotiNum(@AuthUser User user) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(notifyService.getUnReadNotiNum(user.getId()));
    }

    @Operation(summary = "모든 알림 조회(page 기본 size = 5, 최신순)", responses = {
            @ApiResponse(responseCode = "200", description = "목록 조회"),
    })
    @GetMapping("/all")
    public ResponseEntity<Page<NotifyDto>> getAllNotifyForUsers(@AuthUser User user,
                                                                @PageableDefault(sort="id", size = 5, direction = Sort.Direction.DESC) Pageable pageable) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(notifyService.getAllNotifyForUser(user.getId(), pageable));
    }

    @Operation(summary = "선택한 알림 읽음으로 처리 / 알림 선택 시 읽음 처리", parameters = {
            @Parameter(name = "notifyId", description = "목록 조회시 dto에 담겨있는 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공")
    })
    @PutMapping("/select/status/read")
    public ResponseEntity<String> setStatusRead(@AuthUser User user, @RequestParam Long notifyId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.setStatusRead(notifyId);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "선택한 알림 삭제", parameters = {
            @Parameter(name = "notifyId", description = "목록 조회시 dto에 담겨있는 id")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "삭제 성공")
    })
    @DeleteMapping("/select/delete")
    public ResponseEntity<String> deleteNotify(@AuthUser User user, @RequestParam Long notifyId) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.deleteNoti(notifyId);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "선택한 알림들 삭제, 네이버 메일같은것", parameters = {
            @Parameter(name = "notiList", description = "목록 조회시 dto에 담겨있는 id 리스트")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "읽음 처리 성공")
    })
    @DeleteMapping("/select/delete/list")
    public ResponseEntity<String> deleteNotifyAll(@AuthUser User user, @RequestParam List<Long> notiList) {
        if (user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        notifyService.deleteNotiAll(notiList);
        return ResponseEntity.ok().body("완료");
    }
}
