package com.fithub.fithubbackend.domain.user.dto;

import com.fithub.fithubbackend.global.common.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Schema(description = "회원의 관심사 수정 dto")
public class InterestUpdateDto {

    @NotNull
    @Schema(description = "관심사 삭제 여부")
    private boolean interestsDeleted;

    @Schema(description = "interestsDeleted = true 일 때만 사용. 삭제되지 않는 관심사. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> unModifiedInterests;

    @NotNull
    @Schema(description = "관심사 추가 여부")
    private boolean interestsAdded;

    @Schema(description = "추가된 관심사. ex) PILATES, HEALTH, PT, CROSSFIT, YOGA")
    private List<Category> addedInterests;

}
