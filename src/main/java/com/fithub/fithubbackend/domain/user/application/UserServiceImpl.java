package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.domain.user.dto.InterestUpdateDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import com.fithub.fithubbackend.domain.user.repository.DocumentRepository;
import com.fithub.fithubbackend.domain.user.repository.UserInterestRepository;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.common.Category;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.domain.Document;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final DocumentRepository documentRepository;
    private final UserInterestRepository userInterestRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Value("${default.image.address}")
    private String defaultProfileImg;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto myProfile(User user) {
        List<Category> interests = userInterestRepository.findInterestsByUser(user);

        return ProfileDto.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImg(user.getProfileImg().getUrl())
                .bio(user.getBio())
                .gender(user.getGender())
                .grade(user.getGrade())
                .interests(interests)
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
    public void updateInterest(InterestUpdateDto interestUpdateDto, User user) {
        if (interestUpdateDto.isInterestsDeleted()) {
            interestUpdateDto.getDeletedInterests().forEach(interest -> {
                UserInterest userInterest = userInterestRepository.findByInterestAndUser(interest, user);
                userInterestRepository.delete(userInterest);
            });
        }

        if (interestUpdateDto.isInterestsAdded()) {
            interestUpdateDto.getAddedInterests().forEach(interest -> {
                UserInterest userInterest = UserInterest.builder()
                        .interest(interest)
                        .user(user).build();
                userInterestRepository.save(userInterest);
            });
        }
    }

}