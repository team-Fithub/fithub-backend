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

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public User updateProfile(ProfileDto profileDto, User user) {
        try {
            // 중복 검사
            // duplicateNickname(profileDto.getNickname());

            if (profileDto == null) { throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "프로필 데이터가 비어있습니다."); }

            user.updateProfile(profileDto);
            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "프로필 업데이트 중 오류가 발생했습니다");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public User updateImage(MultipartFile profileImg, User user) {
        try {
            if (profileImg == null) { throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "이미지 데이터가 비어있습니다."); }

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

            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "이미지 업데이트 중 오류가 발생했습니다");
        }
    }

//    @Transactional(rollbackFor = {Exception.class})
//    public User updateProfile(String nickname, Gender gender, String phone, MultipartFile profileImg, User user) {
//
//        try {
//            if (nickname != null) {
//                duplicateEmailOrNickname(nickname);
//                user.setNickname(nickname);
//            }
//
//            if (gender != null) {
//                user.setGender(gender);
//            }
//
//            if (phone != null) {
//                user.setPhone(phone);
//            }
//
//            if (profileImg != null) {
//                handleProfileImageUpdate(profileImg, user);
//            }
//
//            userRepository.save(user);
//            return user;
//        } catch (Exception e) {
//            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, ErrorCode.UPLOAD_PROFILE_ERROR.getMessage());
//        }
//    }


    private void duplicateNickname(String nickname) {
        if(userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE,ErrorCode.DUPLICATE.getMessage());
    }

}