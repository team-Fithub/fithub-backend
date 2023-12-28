package com.fithub.fithubbackend.domain.board.application;

import com.fithub.fithubbackend.domain.board.repository.HashtagRepository;
import com.fithub.fithubbackend.global.domain.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    @Transactional
    public Hashtag save(String hashTagContent) {

        Optional<Hashtag> hashtag = hashtagRepository.findByContent(hashTagContent);

        if (hashtag.isPresent())
            return hashtag.get();

        Hashtag newHashtag = Hashtag.builder().content(hashTagContent).build();
        hashtagRepository.save(newHashtag);

        return newHashtag;
    }
}
