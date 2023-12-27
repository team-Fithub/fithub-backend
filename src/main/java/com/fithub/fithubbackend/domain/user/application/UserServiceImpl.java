package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.Training.repository.TrainingCancelOrRefundRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AwsS3Uploader awsS3Uploader;
    private final TrainingCancelOrRefundRepository trainingCancelOrRefundRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto myProfile(User user) {
        return ProfileDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImg(user.getProfileImg().getUrl())
                .bio(user.getBio())
                .gender(user.getGender())
                .grade(user.getGrade())
                .build();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public User updateProfile(MultipartFile profileImg, ProfileDto profileDto, User user) {
        if(!user.getEmail().equals(profileDto.getEmail()) || !user.getNickname().equals(profileDto.getNickname())) {
            duplicateEmailOrNickname(profileDto.getEmail(), profileDto.getNickname());
        }
        user.updateProfile(profileDto);
        if(profileImg != null) {
            awsS3Uploader.deleteFile("profiles",user.getProfileImg().getPath());
            documentRepository.deleteById(user.getProfileImg().getId());
            String profileImgPath = awsS3Uploader.imgPath("profiles");
            Document document = Document.builder()
                    .url(awsS3Uploader.putS3(profileImg,profileImgPath))
                    .inputName(profileImg.getOriginalFilename())
                    .path(profileImgPath)
                    .build();
            documentRepository.save(document);
            user.updateProfileImg(document);
        }
        // TODO : 트랜잭션이 동작을 안함.. 수정예정
        userRepository.save(user);
        return user;
    }

    private void duplicateEmailOrNickname(String email, String nickname) {
        if(userRepository.findByEmail(email).isPresent()
            || userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,ErrorCode.DUPLICATE.getMessage());
    }
}