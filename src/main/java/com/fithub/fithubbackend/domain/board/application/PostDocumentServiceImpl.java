package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.dto.PostDocumentUpdateDto;
import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.repository.PostDocumentRepository;
import com.fithub.fithubbackend.domain.board.post.domain.PostDocument;
import com.fithub.fithubbackend.global.config.s3.AwsS3Uploader;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import com.fithub.fithubbackend.global.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        postDocumentRepository.save(postDocument);
    }

    @Override
    @Transactional
    public void updateDocument(List<PostDocumentUpdateDto> postDocumentUpdateDto, Post post) {

        List<String> awsS3Urls = postDocumentRepository.findUrlsByPost(post);

        List<String> newAws3Urls = postDocumentUpdateDto.stream()
                .filter(image -> image.getAwsS3Url() != null)
                .map(image -> image.getAwsS3Url())
                .collect(Collectors.toList());

        List<String> needToDelete = awsS3Urls.stream().filter(url -> !newAws3Urls.contains(url)).collect(Collectors.toList());

        if (needToDelete != null && !needToDelete.isEmpty()) {
            needToDelete.forEach(imageUrl -> {
                awsS3Uploader.deleteS3(postDocumentRepository.findByUrl(imageUrl).getPath());
                postDocumentRepository.deleteByUrl(imageUrl);
            });
        }

        List<MultipartFile> extractMultipartFile = postDocumentUpdateDto.stream()
                .filter(image -> image.getImage() != null)
                .map(PostDocumentUpdateDto::getImage)
                .collect(Collectors.toList());

        isValidDocument(extractMultipartFile);

        extractMultipartFile.forEach(
                image -> {
                    try {
                        System.out.println(image.getOriginalFilename());
                        createDocument(image, post);
                    } catch (IOException e) {
                        throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
                    }
                }
        );

    }

    @Override
    @Transactional
    public void isValidDocument(List<MultipartFile> images) {

        for (MultipartFile image : images) {
            try (InputStream inputStream = image.getInputStream()) {
                boolean isValid = FileUtils.validImageFile(inputStream);

                if (!isValid) {
                    throw new CustomException(ErrorCode.INVALID_IMAGE, "이미지 파일이 아닌 파일");
                }
            } catch (IOException e) {
                throw new CustomException(ErrorCode.INVALID_IMAGE, "이미지 확장자 검사 실패");
            }
        }
    }

}
