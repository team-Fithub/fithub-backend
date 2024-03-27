package com.fithub.fithubbackend.domain.trainer.application;

import com.fithub.fithubbackend.domain.Training.dto.Location;
import com.fithub.fithubbackend.domain.Training.enums.Direction;
import com.fithub.fithubbackend.domain.trainer.domain.Trainer;
import com.fithub.fithubbackend.domain.trainer.dto.RatingTotalReviewsDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerRecommendationDto;
import com.fithub.fithubbackend.domain.trainer.dto.TrainerRecommendationOutlineDto;
import com.fithub.fithubbackend.domain.trainer.repository.TrainerRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.domain.user.repository.UserInterestRepository;
import com.fithub.fithubbackend.global.common.Category;
import com.fithub.fithubbackend.global.util.GeometryUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserTrainerServiceImpl implements UserTrainerService {

    private final TrainerRepository trainerRepository;
    private final UserInterestRepository userInterestRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public TrainerRecommendationOutlineDto recommendTrainers(User user, Location location, int size) {

        String pointFormat = setFormatPoint(location);
        Category interest = randomCategory(userInterestRepository.findByUser(user));

        List<Trainer> trainerList = findTrainersByLocationAndInterestAndRating(pointFormat, interest, size);
        List<TrainerRecommendationDto> trainerRecommendationList = trainerList.stream().map(TrainerRecommendationDto::toDto).toList();

        updateTrainerRecommendations(trainerRecommendationList);

        return TrainerRecommendationOutlineDto.builder()
                .interest(interest)
                .trainerRecommendationList(trainerRecommendationList).build();
    }

    private void updateTrainerRecommendations(List<TrainerRecommendationDto> trainerRecommendationList) {
        for (TrainerRecommendationDto dto : trainerRecommendationList) {
            RatingTotalReviewsDto result = trainerRepository.findRatingAndReviewCountByTrainerId(dto.getId());
            dto.updateTotalReviews(result.getTotalReviews());
            dto.updateRating(result.getRating());
        }
    }

    private String setFormatPoint(Location location) {
        Location northEast = GeometryUtil.calculate(location.getLatitude(), location.getLongitude(), 1.5, Direction.NORTHEAST.getBearing());
        Location southWest = GeometryUtil.calculate(location.getLatitude(), location.getLongitude(), 1.5, Direction.SOUTHWEST.getBearing());

        return String.format(
                "'LINESTRING(%f %f, %f %f)'",
                northEast.getLongitude(), northEast.getLatitude(), southWest.getLongitude(), southWest.getLatitude()
        );
    }

    private Category randomCategory(List<UserInterest> interestList) {
        if (interestList.size() > 1 ) {
            Random random = new Random();
            return interestList.get(random.nextInt(interestList.size())).getInterest();
        }
        else
            return interestList.get(0).getInterest();
    }

    private List<Trainer> findTrainersByLocationAndInterestAndRating(String pointFormat, Category interest, int size) {
        Query query = entityManager.createNativeQuery(
                "SELECT t.* " +
                        "FROM trainer AS t INNER JOIN user AS u ON t.user_id = u.id " +
                        "LEFT JOIN  " +
                        "(SELECT trainer.id, COALESCE(AVG(tr.star), 0) AS rating " +
                        "FROM training_review AS tr " +
                        "INNER JOIN training ON tr.training_id = training.id " +
                        "INNER JOIN trainer ON training.trainer_id = trainer.id " +
                        "GROUP by trainer.id " +
                        ") AS avg_tr ON t.id = avg_tr.id " +
                        "WHERE u.id IN ( SELECT ui.user_id FROM user_interest ui WHERE ui.interest = '"+ interest + "') " +
                        "AND MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + "), t.point)" +
                        "AND avg_tr.rating >= 3.5 " +
                        "ORDER BY rating DESC LIMIT " + size
                , Trainer.class
        );
        return query.getResultList();
    }
}