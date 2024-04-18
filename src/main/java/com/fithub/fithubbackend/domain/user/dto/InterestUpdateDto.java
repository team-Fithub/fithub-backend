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
    @Schema(description = "관심사 수정 여부")
    private boolean interestsUpdated;

    @NotNull
    @Schema(description = "관심사 리스트")
    private List<Category> interests;

}
