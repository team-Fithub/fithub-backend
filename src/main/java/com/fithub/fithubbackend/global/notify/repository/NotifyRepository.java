package com.fithub.fithubbackend.global.notify.repository;

import com.fithub.fithubbackend.global.domain.Notify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
}
