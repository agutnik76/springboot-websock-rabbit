package com.rabbit.gut.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Конфигуратор базовой авторизации.
 * Авторизация нужна для возможности отправки сообщений по имени пользователя.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Конфигурирует глобальные настройки сервиса управления пользователяим и энкодер.
     *
     * @param auth менеджер аутентификации.
     * @throws Exception Исключения.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(new AuthEveryoneUserAuthServiceImpl())
                .passwordEncoder(getPasswordEncoder());
    }

    /**
     * Конфигурирует доступ к http.
     *
     * @param http - HTTP запрос.
     * @throws Exception Исключения.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .configure(http);//disable()
                http.cors().and()
                .authorizeRequests()
                // Точка для подключения клиентов должна быть доступна всем
                .antMatchers("/socket/sockJs/info*").permitAll()
                .anyRequest().authenticated()
                .and().httpBasic()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    /**
     * Возвращает энкодер пароля.
     *
     * @return шифровшик пароля
     */
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Отключение Cors для локальной отладки.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Upgrade"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}