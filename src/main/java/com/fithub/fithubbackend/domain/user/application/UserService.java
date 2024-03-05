package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ProfileDto myProfile(User user);
    void updateProfile(ProfileUpdateDto profileUpdateDto, User user);
    void updateImage(MultipartFile profileImg, User user);
}
