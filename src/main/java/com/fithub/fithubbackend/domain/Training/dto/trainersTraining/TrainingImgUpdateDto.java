package com.fithub.fithubbackend.domain.Training.dto.trainersTraining;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class TrainingImgUpdateDto {
    private boolean imgDeleted;
    private List<String> unModifiedImgList;

    private boolean imgAdded;
    private List<MultipartFile> newImgList;
}
