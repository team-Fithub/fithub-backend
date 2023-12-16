package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostDocumentService {
    void createDocument(MultipartFile image, Post post) throws IOException;

    void updateDocument(List<MultipartFile> images, Post post);
}
