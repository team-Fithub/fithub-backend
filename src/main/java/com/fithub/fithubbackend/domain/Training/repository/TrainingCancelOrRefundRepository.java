package com.fithub.fithubbackend.domain.Training.repository;

import com.fithub.fithubbackend.domain.Training.domain.TrainingCancelOrRefund;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingCancelOrRefundRepository extends JpaRepository<TrainingCancelOrRefund,Long> {
    List<TrainingCancelOrRefund> findByUser(User user);
}
