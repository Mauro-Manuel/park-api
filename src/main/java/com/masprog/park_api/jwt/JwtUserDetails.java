package com.masprog.park_api.jwt;

import com.masprog.park_api.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtUserDetails
        extends org.springframework.security.core.userdetails.User {

    private final User user;

    public JwtUserDetails(User user) {
        super(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole().name())
        );

        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getRole() {
        return user.getRole().name();
    }
}