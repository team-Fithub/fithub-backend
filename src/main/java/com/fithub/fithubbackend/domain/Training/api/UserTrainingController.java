package com.fithub.fithubbackend.domain.Training.api;

import com.fithub.fithubbackend.domain.Training.application.UserTrainingService;
import com.fithub.fithubbackend.domain.Training.dto.TrainingInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingLikesInfoDto;
import com.fithub.fithubbackend.domain.Training.dto.TrainingOutlineDto;
import com.fithub.fithubbackend.domain.Training.dto.UsersReserveInfoDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.exception.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/training")
@RequiredArgsConstructor
public class UserTrainingController {
    private final UserTrainingService userTrainingService;

    @Operation(summary = "트레이닝 전체 조회, page 사용 (size = 9, sort=\"id\" desc 적용되어있음. swagger에서 보낼 때 따로 지정하는 거 없으면 parameter에 pageable은 다 지우고 보내도 됨)")
    @GetMapping("/all")
    public ResponseEntity<Page<TrainingOutlineDto>> searchAll(@PageableDefault(size = 9, sort = "id",  direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userTrainingService.searchAll(pageable));
    }

    @Operation(summary = "트레이닝 하나 상세 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당하는 트레이닝 존재 X", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<TrainingInfoDto> searchById(@RequestParam Long trainingId) {
        return ResponseEntity.ok(userTrainingService.searchById(trainingId));
    }

    @Operation(summary = "트레이닝 찜 리스트 조회", responses = {
            @ApiResponse(responseCode = "200", description = "리스트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/likes")
    public ResponseEntity<List<TrainingLikesInfoDto>> getUserTrainingLikesList(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getTrainingLikesList(user));
    }

    @Operation(summary = "트레이닝 찜 여부 조회", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "찜 여부를 조회할 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/check/likes")
    public ResponseEntity<Boolean> isLikesTraining(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.isLikesTraining(trainingId, user));
    }

    @Operation(summary = "트레이닝 찜 설정", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "400", description = "트레이너는 자신의 트레이닝 찜 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/likes")
    public ResponseEntity<String> likes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.likesTraining(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "트레이닝 찜 취소", parameters = {
            @Parameter(name = "trainingId", description = "조회할 트레이닝의 primary key(id)")
    }, responses = {
            @ApiResponse(responseCode = "404", description = "해당 트레이닝이 존재하지 않음"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "해당 트레이닝을 찜하지 않아 찜 취소 불가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/likes")
    public ResponseEntity<String> cancelTrainingLikes(@RequestParam Long trainingId, @AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        userTrainingService.cancelTrainingLikes(trainingId, user);
        return ResponseEntity.ok().body("완료");
    }

    @Operation(summary = "회원의 트레이닝 예약 리스트", parameters = {
            @Parameter(name = "pageable", description = "조회할 목록의 page, size, sort(기본은 id desc(생성 순), 예약된 트레이닝 날짜 순은 reserveDateTime으로 주면 됨)")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인한 사용자만 가능", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/reservations")
    public ResponseEntity<Page<UsersReserveInfoDto>> getUsersTrainingReservationList(@AuthUser User user,
                                                                                     @PageableDefault(sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(userTrainingService.getTrainingReservationList(user, pageable));
    }
}
