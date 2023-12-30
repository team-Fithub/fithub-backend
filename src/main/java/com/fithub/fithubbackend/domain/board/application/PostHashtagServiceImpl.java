package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import com.fithub.fithubbackend.domain.board.repository.PostHashtagRepository;
import com.fithub.fithubbackend.global.domain.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHashtagServiceImpl implements PostHashtagService {

    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;

    @Override
    @Transactional
    public void createPostHashtag(String hashTagContentStr, Post post) {
        List<String> hashTagContents = extractHashTags(hashTagContentStr);
        hashTagContents.forEach(hashTagContent -> savePostHashtag(hashTagContent, post));
    }

    @Transactional
    public void savePostHashtag(String hashTagContent, Post post){
        Hashtag hashtag = hashtagService.save(hashTagContent);
        post.addPostHashtag(new PostHashtag(post, hashtag));
    }

    @Override
    @Transactional
    public void updateHashtag(String hashTagContentStr, Post post) {

        List<String> oldHashTags = postHashtagRepository.findHashtagByPostId(post.getId());

        List<String> newHashTags = extractHashTags(hashTagContentStr);

        if (oldHashTags.equals(newHashTags))
            return;

        post.getPostHashtags().clear();
        newHashTags.forEach(hashTagContent -> savePostHashtag(hashTagContent, post));
    }

    public List<String> extractHashTags(String hashTagContentStr) {
        return Arrays.stream(hashTagContentStr.split("#"))
                .map(String::trim)
                .filter(hashTag -> hashTag.length() > 0)
                .collect(Collectors.toList());
    }

}
