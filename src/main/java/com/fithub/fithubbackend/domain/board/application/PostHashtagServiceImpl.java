package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.post.domain.Post;
import com.fithub.fithubbackend.domain.board.post.domain.PostHashtag;
import com.fithub.fithubbackend.domain.board.repository.PostHashtagRepository;
import com.fithub.fithubbackend.global.domain.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
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
        postHashtagRepository.save(new PostHashtag(post, hashtag));
    }

    @Override
    @Transactional
    public void updateHashtag(String hashTagContentStr, Post post) {

        // 1. post 해시태그 내용 리스트 가져오기
        List<String> oldHashTags = postHashtagRepository.findHashtagByPostId(post.getId());

        // 2. 업데이트된 해시태그 추출
        List<String> newHashTags = extractHashTags(hashTagContentStr);

        // 3. 해시태그 내용 리스트와 업데이트 해시태크 sort
        Collections.sort(oldHashTags);
        Collections.sort(newHashTags);

        // 4. 변경 사항이 없다면 return
        if (oldHashTags.equals(newHashTags))
            return;

        List<PostHashtag> oldPostHashtags = getPostHashTags(post);
        List<PostHashtag> needToDelete = new ArrayList<>();

        for (PostHashtag oldPostHashtag : oldPostHashtags) {
            boolean contains = newHashTags.stream().anyMatch(hashTagContent -> hashTagContent.equals(oldPostHashtag.getHashtag().getContent()));
            if (!contains)
                needToDelete.add(oldPostHashtag);
        }

        List<String> needToAdd = newHashTags.stream().filter(newHT -> oldHashTags.stream().noneMatch(Predicate.isEqual(newHT)))
                .collect(Collectors.toList());

        needToAdd.forEach(hashTagContent -> saveHashtag(hashTagContent, post));
        needToDelete.forEach(hashTag -> {
            postHashtagRepository.delete(hashTag);
        });
    }


    @Transactional
    public void saveHashtag(String hashTagContent, Post post){

        Hashtag hashtag = hashtagService.save(hashTagContent);

        Optional<PostHashtag> postHashtag = postHashtagRepository.findByPostAndHashtag(post, hashtag);

        if (postHashtag.isPresent())
            return;

        postHashtagRepository.save(new PostHashtag(post, hashtag));
    }

    @Transactional
    public List<PostHashtag> getPostHashTags(Post post){
        return postHashtagRepository.findByPost(post);
    }

    public List<String> extractHashTags(String hashTagContentStr) {
        return Arrays.stream(hashTagContentStr.split("#"))
                .map(String::trim)
                .filter(hashTag -> hashTag.length() > 0)
                .collect(Collectors.toList());
    }

}
