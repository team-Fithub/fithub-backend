package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.enums.Gender;
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

//    @Override
//    @Transactional(rollbackFor = {Exception.class})
//    public User updateProfile(MultipartFile profileImg, ProfileDto profileDto, User user) {
//        System.out.println("UserServiceImpl.updateProfile 실행");
//
//        try {
//            if (profileDto != null) {
//                System.out.println("UserServiceImpl.updateProfile: profileDto 수정");
//                user.updateProfile(profileDto);
//            }
//
//            if (profileImg != null) {
//                System.out.println("UserServiceImpl.updateProfile: profileImg 수정");
//                awsS3Uploader.deleteS3(user.getProfileImg().getPath());
//                documentRepository.deleteById(user.getProfileImg().getId());
//                String profileImgPath = awsS3Uploader.imgPath("profiles");
//                Document document = Document.builder()
//                        .url(awsS3Uploader.putS3(profileImg, profileImgPath))
//                        .inputName(profileImg.getOriginalFilename())
//                        .path(profileImgPath)
//                        .build();
//                documentRepository.save(document);
//                user.updateProfileImg(document);
//            }
//
//            userRepository.save(user);
//            return user;
//        } catch (Exception e) {
//            // 롤백이 필요한 예외가 여기에 포함되어야 합니다.
//            throw new RuntimeException("프로필 업데이트 중 오류 발생", e);
//        }
//    }

    @Transactional(rollbackFor = {Exception.class})
    public User updateProfile(String nickname, Gender gender, String phone, MultipartFile profileImg, User user) {

        try {
            if (nickname != null) {
                duplicateEmailOrNickname(nickname);
                user.setNickname(nickname);
            }

            if (gender != null) {
                user.setGender(gender);
            }

            if (phone != null) {
                user.setPhone(phone);
            }

            if (profileImg != null) {
                handleProfileImageUpdate(profileImg, user);
            }

            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, ErrorCode.UPLOAD_PROFILE_ERROR.getMessage());
        }
    }

    private void handleProfileImageUpdate(MultipartFile profileImg, User user) {
        awsS3Uploader.deleteS3(user.getProfileImg().getPath());
        documentRepository.deleteById(user.getProfileImg().getId());

        String profileImgPath = awsS3Uploader.imgPath("profiles");
        Document document = Document.builder()
                .url(awsS3Uploader.putS3(profileImg, profileImgPath))
                .inputName(profileImg.getOriginalFilename())
                .path(profileImgPath)
                .build();

        documentRepository.save(document);
        user.updateProfileImg(document);
    }

    private void duplicateEmailOrNickname(String nickname) {
        if(userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,ErrorCode.DUPLICATE.getMessage());
    }

}