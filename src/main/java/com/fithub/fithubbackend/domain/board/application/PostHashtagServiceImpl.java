package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import com.fithub.fithubbackend.domain.board.repository.PostHashtagRepository;
import com.fithub.fithubbackend.global.domain.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHashtagServiceImpl implements PostHashtagService {

    private final PostHashtagRepository postHashtagRepository;
    private final HashtagService hashtagService;

    @Override
    @Transactional
    public void createOrUpdateHashtag(String hashTagContentStr, Post post){

        List<PostHashtag> oldPostHashtags = getPostHashTags(post);
        List<PostHashtag> needToDelete = new ArrayList<>();

        List<String> hashTagContents = Arrays.stream(hashTagContentStr.split("#"))
                .map(String::trim)
                .filter(hashTag -> hashTag.length() > 0)
                .collect(Collectors.toList());

        for (PostHashtag oldPostHashtag : oldPostHashtags) {
            boolean contains = hashTagContents.stream().anyMatch(hashTagContent -> hashTagContent.equals(oldPostHashtag.getHashtag().getContent()));

            if (!contains)
                needToDelete.add(oldPostHashtag);

        }

        hashTagContents.forEach(hashTagContent -> saveHashtag(hashTagContent, post));
        needToDelete.forEach(hashTag -> {
            postHashtagRepository.delete(hashTag);
        });
    }

    @Override
    public void updateHashTag(String hashTags, Post post) {

    }

    @Transactional
    public void saveHashtag(String hashTagContent, Post post){

        Hashtag hashtag = hashtagService.save(hashTagContent);

        Optional<PostHashtag> postHashtag = postHashtagRepository.findByPostAndHashtag(post, hashtag);

        if (postHashtag.isPresent())
            return;

        postHashtagRepository.save(new PostHashtag(post, hashtag));
    }

    public List<PostHashtag> getPostHashTags(Post post){
        return postHashtagRepository.findByPost(post);
    }

}
