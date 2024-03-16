package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.domain.UserInterest;
import com.fithub.fithubbackend.domain.user.dto.InterestUpdateDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    ProfileDto myProfile(User user);
    void updateProfile(ProfileUpdateDto profileUpdateDto, User user);
    void updateImage(MultipartFile profileImg, User user);
    void updateInterest(InterestUpdateDto interestUpdateDto, User user);
    List<UserInterest> getUserInterests(Long userId);
}
