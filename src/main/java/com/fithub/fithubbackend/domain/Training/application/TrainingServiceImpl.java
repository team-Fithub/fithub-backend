package com.fithub.fithubbackend.domain.Training.application;

import com.fithub.fithubbackend.domain.Training.domain.Training;
import com.fithub.fithubbackend.domain.Training.domain.TrainingReview;
import com.fithub.fithubbackend.domain.Training.dto.*;
import com.fithub.fithubbackend.domain.Training.dto.review.TrainingReviewDto;
import com.fithub.fithubbackend.domain.Training.enums.Direction;
import com.fithub.fithubbackend.domain.Training.repository.CustomTrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingRepository;
import com.fithub.fithubbackend.domain.Training.repository.TrainingReviewRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.GeometryUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingReviewRepository trainingReviewRepository;

    private final CustomTrainingRepository customTrainingRepository;

    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingOutlineDto> searchAll(Pageable pageable) {
        Page<Training> trainingPage = trainingRepository.findAllByDeletedFalseAndClosedFalse(pageable);
        return trainingPage.map(TrainingOutlineDto::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingInfoDto searchById(Long id) {
        Training training = trainingRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "해당하는 트레이닝이 존재하지 않습니다."));
        if (training.isDeleted()) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "삭제된 트레이닝입니다.");
        }

        TrainingInfoDto dto = TrainingInfoDto.toDto(training);
        if (training.getImages() != null && !training.getImages().isEmpty()) {
            List<TrainingDocumentDto> images = training.getImages().stream().map(TrainingDocumentDto::toDto).toList();
            dto.updateImages(images);
        }
        return dto;
    }

    @Override
    public List<TrainingReviewDto> getTrainingReviews(Long id) {
        List<TrainingReview> trainingReviewList = trainingReviewRepository.findByLockedFalseAndTrainingId(id);
        return trainingReviewList.stream().map(TrainingReviewDto::toDto).toList();
    }

    @Override
    public Page<TrainingOutlineDto> searchTrainingByConditions(TrainingSearchConditionDto conditions, Pageable pageable) {
        Page<Training> trainingList = customTrainingRepository.searchByConditions(conditions, pageable);
        return trainingList.map(TrainingOutlineDto::toDto);
    }

    public List<TrainingOutlineDto> searchTrainingByLocation(Double latitude, Double longitude) {
        Location northEast = GeometryUtil.calculate(latitude, longitude, 2.0, Direction.NORTHEAST.getBearing());
        Location southWest = GeometryUtil.calculate(latitude, longitude, 2.0, Direction.SOUTHWEST.getBearing());

        String pointFormat = formatPoint(northEast, southWest);

        List<Training> trainingList = findTrainingByLocation(pointFormat);

        return trainingList.stream().map(t -> {
            TrainingOutlineDto dto = TrainingOutlineDto.toDto(t);
            dto.updateDist(calcDist(latitude, longitude, t.getId()));
            return dto;
        }).toList();
    }

    private String formatPoint(Location northEast, Location southWest) {
        return String.format(
                "'LINESTRING(%f %f, %f %f)'",
                northEast.getLongitude(), northEast.getLatitude(), southWest.getLongitude(), southWest.getLatitude()
        );
    }

    private List<Training> findTrainingByLocation(String pointFormat) {
        Query query = entityManager.createNativeQuery(
                "" +
                        "SELECT * \n" +
                        "FROM Training AS t \n" +
                        "WHERE t.deleted = false AND t.closed = false " +
                        "AND " +
                        "MBRContains(ST_LINESTRINGFROMTEXT(" + pointFormat + "), t.point) \n" +
                        "ORDER BY t.id"
                , Training.class
        );
        return query.getResultList();
    }

    private Double calcDist(Double latitude, Double longitude, Long trainingId) {
        Point point;
        try {
            point = (Point) new WKTReader().read(String.format("POINT(%s %s)", longitude, latitude));
        } catch (ParseException e) {
            throw new CustomException(ErrorCode.POINT_PARSING_ERROR);
        }

        if (point != null) {
            return trainingRepository.findDistByPoint(point.getX(), point.getY(), trainingId);
        }
        return null;
    }
}
