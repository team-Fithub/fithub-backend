package com.fithub.fithubbackend.global.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserAdapter extends User {
    private com.fithub.fithubbackend.domain.user.domain.User user;

    public UserAdapter(com.fithub.fithubbackend.domain.user.domain.User user){
        super(user.getEmail(),user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_"+ user.getRoles())));
        this.user = user;
    }
}
