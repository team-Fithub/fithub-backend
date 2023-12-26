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
import java.util.List;
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

        List<PostDocument> oldPostDocuments = findPostDocumentsByPost(post);

        IntStream.range(0, postDocumentUpdateDto.size())
                .forEach(index -> {
                    PostDocumentUpdateDto editedImage = postDocumentUpdateDto.get(index);

                    if (editedImage.isAdded()) {
                            try {
                                createDocument(editedImage.getImage(), post);
                            } catch (IOException e) {
                                throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
                            }
                        }

                    if (editedImage.isDeleted()) {
                        List<PostDocument> postDocument = postDocumentRepository.findBySizeAndInputNameAndPost(editedImage.getImage().getSize(), editedImage.getImage().getOriginalFilename(), post);

                        if (postDocument.size() > 1 && index < oldPostDocuments.size()) {
                            awsS3Uploader.deleteS3(oldPostDocuments.get(index).getPath());
                            postDocumentRepository.delete(postDocument.get(index));
                        } else {
                            awsS3Uploader.deleteS3(postDocument.get(0).getPath());
                            postDocumentRepository.delete(postDocument.get(0));
                        }
                    }
                });
    }

    @Override
    public void isValidDocument(List<MultipartFile> images) throws IOException {

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

    @Transactional
    public List<PostDocument> findPostDocumentsByPost(Post post) {
        return postDocumentRepository.findByPost(post);
    }
}
