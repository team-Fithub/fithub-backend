package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostDocumentRepository;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostDocumentServiceImpl implements PostDocumentService {

    private final PostDocumentRepository postDocumentRepository;
    private final AwsS3Uploader awsS3Uploader;

    @Value("${default.image.address}")
    private String profileImgUrl;

    @Override
    @Transactional
    public void createDocument(MultipartFile image, Post post) throws IOException {
        String path = awsS3Uploader.imgPath("postDocuments");
        String url = awsS3Uploader.putS3(image, path);

        PostDocument postDocument = PostDocument.builder()
                .url(url)
                .inputName(image.getOriginalFilename())
                .path(path)
                .post(post).build();

        postDocumentRepository.save(postDocument);
    }

    @Override
    @Transactional
    public void updateDocument(List<MultipartFile> images, Post post) {
        deletePostDocuments(post);

        images.forEach(image -> {
            try {
                createDocument(image, post);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.UPLODE_FAIL);
            }
        });

    }

    @Transactional
    public void deletePostDocuments(Post post) {
        postDocumentRepository.deleteByPost(post);
    }
}
