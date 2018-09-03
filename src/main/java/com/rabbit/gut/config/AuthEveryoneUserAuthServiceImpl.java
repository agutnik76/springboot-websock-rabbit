package com.rabbit.gut.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Сервис для авторизации любых пользователей без пароля.
 */
@Service
public class AuthEveryoneUserAuthServiceImpl implements UserDetailsService {
    /**
     * Поииск пользователя.
     *
     * @param username - Имя пользователя.
     * @return Информация о пользователе.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        return new User(username, "", Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
