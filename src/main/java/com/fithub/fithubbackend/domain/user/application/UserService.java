package com.fithub.fithubbackend.domain.user.application;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.dto.ProfileDto;
import org.springframework.web.multipart.MultipartFile;


public interface UserService {
    ProfileDto myProfile(User user);
    User updateProfile(MultipartFile multipartFile, ProfileDto profileDto, User user);
}
