package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    @Override
    @Transactional(readOnly = true)
    public ProfileDto myProfile(User user) {
        return ProfileDto.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImg(user.getProfileImg().getUrl())
                .bio(user.getBio())
                .gender(user.getGender())
                .grade(user.getGrade())
                .build();
    }
}