package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import com.fithub.fithubbackend.domain.board.repository.PostDocumentRepository;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public void createDocument(MultipartFile image, Post post) throws IOException {
        String path = awsS3Uploader.imgPath("postDocuments");
        String url = awsS3Uploader.putS3(image, path);

        PostDocument postDocument = PostDocument.builder()
                .url(url)
                .inputName(image.getOriginalFilename())
                .path(path)
                .post(post)
                .size(image.getSize()).build();

        post.addPostDocument(postDocument);
    }

    @Override
    @Transactional
    public void updateDocument(PostUpdateDto postUpdateDto, Post post) {

        if (postUpdateDto.isImageDeleted()) {
            List<PostDocument> postDocuments = postDocumentRepository.findByPost(post);
            List<String> awsS3Urls = postUpdateDto.getExistingImages();

            postDocuments.forEach(postDocument -> {
                if (!awsS3Urls.contains(postDocument.getUrl())) {
                    awsS3Uploader.deleteS3(postDocument.getPath());
                    post.getPostDocuments().remove(postDocument);
                }
            });
        }

        if (postUpdateDto.isImageAdded()) {
            FileUtils.isValidDocument(postUpdateDto.getNewImages());
            postUpdateDto.getNewImages().forEach(
                    newImage -> {
                        try {
                            createDocument(newImage, post);
                        } catch (IOException e) {
                            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
                        }
                    }
            );
        }

    }
}
