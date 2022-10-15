package com.sm.expose.global.config;

import com.sm.expose.global.security.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity // spring security 설정을 활성화시켜주는 어노테이션
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService oAuthService;

    public SecurityConfig(CustomOAuth2UserService oAuthService) {
        this.oAuthService = oAuthService;
    }

        //AuthenticationManagerBean 등록 -> 하단의 configure에서 LoginForm을 이용한 자동 처리 사용 X,
    // 수동으로 Authentication을 만들어서 SecurityContext에 저장
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()  throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login() // OAuth2 로그인 설정 시작점
                .userInfoEndpoint() // OAuth2 로그인 성공 이후 사용자 정보를 가져올 때 설정 담당
                .userService(oAuthService); // OAuth2 로그인 성공 시, 후작업을 진행할 UserService 인터페이스 구현체 등록
    }
}