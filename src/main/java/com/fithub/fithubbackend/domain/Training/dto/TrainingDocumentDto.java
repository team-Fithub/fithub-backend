package com.fithub.fithubbackend.domain.Training.dto;

import com.fithub.fithubbackend.domain.Training.domain.TrainingDocument;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class TrainingDocumentDto {
    private Long id;
    private String url;
    private String inputName;

    public static TrainingDocumentDto toDto(TrainingDocument document) {
        return TrainingDocumentDto.builder()
                .id(document.getId())
                .url(document.getDocument().getUrl())
                .inputName(document.getDocument().getInputName())
                .build();
    }
}
