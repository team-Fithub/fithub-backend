package com.fithub.fithubbackend.domain.trainer.repository;

import com.fithub.fithubbackend.domain.trainer.domain.TrainerLicenseImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainerLicenseImgRepository extends JpaRepository<TrainerLicenseImg, Long> {

    List<TrainerLicenseImg> findByTrainerId(long trainerId);
}
