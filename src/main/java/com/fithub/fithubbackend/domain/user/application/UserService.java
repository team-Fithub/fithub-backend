package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import com.fithub.fithubbackend.domain.user.dto.ProfileUpdateDto;
import com.fithub.fithubbackend.domain.user.enums.Gender;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    ProfileDto myProfile(User user);
    ProfileDto updateProfile(ProfileUpdateDto profileUpdateDto, User user);
    ProfileDto updateImage(MultipartFile profileImg, User user);
}
