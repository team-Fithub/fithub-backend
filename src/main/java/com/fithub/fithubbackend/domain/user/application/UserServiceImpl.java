package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.Training.enums.ReserveStatus;
import com.fithub.fithubbackend.domain.Training.repository.ReserveInfoRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingLikesRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.board.repository.BookmarkRepository;
import com.fithub.fithubbackend.domain.board.repository.LikesRepository;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.ClosureReason;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.domain.user.dto.CloseAccountReasonDto;
import com.fithub.fithubbackend.domain.user.dto.InterestUpdateDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import com.fithub.fithubbackend.domain.user.repository.ClosureReasonRepository;
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

import java.time.LocalDate;
import java.util.Optional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final DocumentRepository documentRepository;
    private final UserInterestRepository userInterestRepository;
    private final ReserveInfoRepository reserveInfoRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TrainingLikesRepository trainingLikesRepository;
    private final ClosureReasonRepository closureReasonRepository;
    private final LikesRepository likesRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Value("${default.image.address}")
    private String defaultProfileImg;

    @Override
    @Transactional(readOnly = true)
    public ProfileDto myProfile(User user) {
        List<Category> interests = userInterestRepository.findByUser(user).stream().map(UserInterest::getInterest).toList();

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
            List<UserInterest> originalInterests = userInterestRepository.findByUser(user);
            originalInterests.forEach(originalInterest -> {
                if (!interestUpdateDto.getUnModifiedInterests().contains(originalInterest.getInterest())) {
                    userInterestRepository.delete(originalInterest);
                }
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

    @Override
    @Transactional(readOnly = true)
    public List<UserInterest> getUserInterests(Long userId) {
        return userInterestRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void closeAccount(User user, CloseAccountReasonDto reason) {

        if (user.getRoles().contains("ROLE_TRAINER")) {
            Trainer trainer = trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

            if (trainingRepository.existsByTrainerIdAndEndDateAfter(trainer.getId(), LocalDate.now()))
                throw new CustomException(ErrorCode.CONFLICT_TRAINING);

            if (!reserveInfoRepository.existsByTrainerAndStatusNotIn(trainer,
                    List.of(ReserveStatus.BEFORE, ReserveStatus.START)))
                throw new CustomException(ErrorCode.BAD_REQUEST, "예약 또는 진행 중인 트레이닝이 존재해 트레이너 탈퇴 작업이 불가능합니다.");

            trainer.clearUpTrainer(user.getProfileImg().getUrl());
        } else
            if (!reserveInfoRepository.existsByUserAndStatusNotIn(user,
                    List.of(ReserveStatus.BEFORE, ReserveStatus.START)))
                throw new CustomException(ErrorCode.BAD_REQUEST, "예약 또는 진행 중인 트레이닝이 존재해 회원 탈퇴 작업이 불가능합니다.");

        addClosureReason(reason);
        cleanUpUser(user);
        userRepository.save(user);
    }

    private void addClosureReason(CloseAccountReasonDto reason) {
        closureReasonRepository.save(ClosureReason.builder()
                .reasonType(reason.getClosureReasonType()).customReason(reason.getCustomReason()).build());
    }

    private void cleanUpUser(User user) {
        
        if (!user.getProfileImg().getUrl().equals(defaultProfileImg)) {
            awsS3Uploader.deleteS3(user.getProfileImg().getPath());
            documentRepository.deleteById(user.getProfileImg().getId());

            Optional<Document> defaultDocument = documentRepository.findById(1L);
            user.updateProfileImg(defaultDocument.get());
        }

        user.deleteUser();
        bookmarkRepository.deleteByUser(user);
        likesRepository.deleteByUser(user);
        trainingLikesRepository.deleteByUser(user);
        userInterestRepository.deleteByUser(user);

    }

}