package com.rabbit.gut.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Service to authenticate all users without password.
 */
@Service(value = "secdetails")
public class AuthEveryoneUserAuthServiceImpl implements UserDetailsService {
    /**
     * simple user authentication .
     *
     * @param username
     * @return user details
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return new User(username, "", Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
