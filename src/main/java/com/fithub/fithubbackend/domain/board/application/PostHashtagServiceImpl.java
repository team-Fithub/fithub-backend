package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import com.fithub.fithubbackend.domain.board.repository.HashtagRepository;
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
    private final HashtagRepository hashtagRepository;

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

        /* 해시태그 업데이트
        * 1. DB에 저장된 postHashtag 삭제 후 업데이트된 해시태그 저장
        * */
//        List<String> oldHashTags = postHashtagRepository.findHashtagByPostId(post.getId());
//
//        List<String> newHashTags = extractHashTags(hashTagContentStr);
//
//        if (oldHashTags.equals(newHashTags))
//            return;
//
//        post.getPostHashtags().clear();
//        newHashTags.forEach(hashTagContent -> savePostHashtag(hashTagContent, post));

        /* 해시태그 업데이트
         * 2. 기존 해시태그 리스트의 hashtag fk 업데이트
         * */

        // DB에 저장된 PostHashtags 리스트
        List<PostHashtag> dbPostHashtags = postHashtagRepository.findByPostFetch(post.getId());

        // 문자열 형태의 기존 DB에 저장된 해시태그 리스트
        List<String> oldHashTags = postHashtagRepository.findHashtagByPostId(post.getId());

        // 문자열 형태의 새로운 해시태그 리스트
        List<String> newHashtags = extractHashTags(hashTagContentStr);

        if (oldHashTags.equals(newHashtags))
            return;

        int i, j;
        for (i = 0; i < newHashtags.size(); i++ ) {

            String newHashtagContent = newHashtags.get(i);
            Optional<Hashtag> hashtag = hashtagRepository.findByContent(newHashtagContent);

            if (i < oldHashTags.size()) {
                if (!newHashtagContent.equals(oldHashTags.get(i))) {
                    PostHashtag dbPostHashtag = dbPostHashtags.get(i);

                    hashtag.ifPresentOrElse(
                            h -> dbPostHashtag.setHashtag(h),
                            () -> {
                                Hashtag newHashtag = new Hashtag(newHashtagContent);
                                hashtagRepository.save(newHashtag);
                                dbPostHashtag.setHashtag(newHashtag);
                            }
                    );
                }
            }
            else {
                String addHashtagContent = newHashtags.get(i);

                hashtag.ifPresentOrElse(
                        h -> post.addPostHashtag(new PostHashtag(post, hashtag.get())),
                        () -> {
                            Hashtag addHashtag = new Hashtag(addHashtagContent);
                            hashtagRepository.save(addHashtag);
                            post.addPostHashtag(new PostHashtag(post, addHashtag));
                        }
                );
            }
        }

        if (newHashtags.size() < oldHashTags.size()) {
            for (j = i; j < oldHashTags.size(); j++) {
                post.getPostHashtags().remove(dbPostHashtags.get(j));
            }
        }

    }

    public List<String> extractHashTags(String hashTagContentStr) {
        return Arrays.stream(hashTagContentStr.split("#"))
                .map(String::trim)
                .filter(hashTag -> hashTag.length() > 0)
                .collect(Collectors.toList());
    }

}
