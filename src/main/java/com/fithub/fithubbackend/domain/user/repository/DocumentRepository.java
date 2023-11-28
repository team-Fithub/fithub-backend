package com.fithub.fithubbackend.domain.user.repository;

import com.fithub.fithubbackend.global.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document,Long> {
}
