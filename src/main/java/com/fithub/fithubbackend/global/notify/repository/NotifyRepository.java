package com.fithub.fithubbackend.global.notify.repository;

import com.fithub.fithubbackend.global.domain.Notify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
    Long countByIsReadFalseAndReceiverId(Long receiverId);
    Page<Notify> findByReceiverId(Long receiverId, Pageable pageable);
    List<Notify> findByIdIn(List<Long> idList);
}
