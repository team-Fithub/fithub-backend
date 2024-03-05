package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.board.application.UserPostBookmarkService;
import com.fithub.fithubbackend.domain.board.application.UserPostLikesService;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final DocumentRepository documentRepository;
    private final AwsS3Uploader awsS3Uploader;

    private final UserPostBookmarkService bookmarkService;
    private final UserPostLikesService likesService;

    private final RedisUtil redisUtil;

    @Value("${default.image.address}")
    private String defaultProfileImg;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto myProfile(User user) {
        return ProfileDto.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImg(user.getProfileImg().getUrl())
                .bio(user.getBio())
                .gender(user.getGender())
                .grade(user.getGrade())
                .trainer(user.isTrainer())
                .build();
    }

    @Override
    @Transactional
    public void updateProfile(ProfileUpdateDto profileUpdateDto, User user) {
        try {
            user.updateProfile(profileUpdateDto);
            userRepository.save(user);
            Optional<Trainer> optionalTrainer = trainerRepository.findByUserId(user.getId());
            optionalTrainer.ifPresent(t -> t.linkedToUserName(user.getName()));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "프로필 업데이트 중 오류가 발생했습니다");
        }
    }

    @Override
    @Transactional
    public void updateImage(MultipartFile profileImg, User user) {
        try {
            if (!user.getProfileImg().getUrl().equals(defaultProfileImg)) {
                awsS3Uploader.deleteS3(user.getProfileImg().getPath());
                documentRepository.deleteById(user.getProfileImg().getId());
            }
            String profileImgPath = awsS3Uploader.imgPath("profiles");
            Document document = Document.builder()
                    .url(awsS3Uploader.putS3(profileImg, profileImgPath))
                    .inputName(profileImg.getOriginalFilename())
                    .path(profileImgPath)
                    .build();
            documentRepository.save(document);
            user.updateProfileImg(document);
            userRepository.save(user);
            Optional<Trainer> optionalTrainer = trainerRepository.findByUserId(user.getId());
            optionalTrainer.ifPresent(t -> t.linkedToUserProfileImg(document));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.UPLOAD_PROFILE_ERROR, "이미지 업데이트 중 오류가 발생했습니다");
        }
    }

    @Override
    @Transactional
    public void deleteUserAccount(User user, String cookieRefreshToken) {

        // TODO 예약되거나 진행 중인 트레이닝이 있는 경우 탈퇴 보류

        redisUtil.deleteData(user.getEmail());

        user.deleteUser();

        if (user.getProfileImg().getId() != 1L) {
            awsS3Uploader.deleteS3(user.getProfileImg().getPath());
            Document document = documentRepository.findById(1L).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "존재하지 않는 기본 이미지"));
            user.updateProfileImg(document);
        }

        bookmarkService.deleteAllBookmarksByUser(user);
        likesService.deleteAllLikesByUser(user);

        userRepository.save(user);
    }
}