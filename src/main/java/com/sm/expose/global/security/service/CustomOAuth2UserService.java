package com.sm.expose.global.security.service;

import com.sm.expose.global.security.domain.Role;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.domain.UserPrincipal;
import com.sm.expose.global.security.dto.GoogleOAuth2User;
import com.sm.expose.global.security.oauth.ProviderType;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * UserPrincipleDetailsService 클래스의 역할, User 정보를 가져온다.
 * 가져온 User의 정보를 UserPrinciple 클래스로 변경해 Spring Security로 전달.
 */
/**
 * 인증 성공 시 Spring OAuth2에서 호출할 loadUser() 메소드를 재정의하고 새 CustomOAuth2User 객체를 반환
 * JWT 방식에서는 반환 객체가 저장되지 X
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserDetailsServiceImpl userDetailsService;

    public CustomOAuth2UserService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @SneakyThrows
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /** 유저의 정보를 가져옴 */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        return processOAuthUser(userRequest, oAuth2User);

    }


    /**
     * 사용자 정보 추출
     * @param userRequest
     * @param oAuth2User
     * @return
     */
    private OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        GoogleOAuth2User googleOAuth2User = new GoogleOAuth2User(oAuth2User.getAttributes());
        String email = googleOAuth2User.getEmail();

        User user = userDetailsService.findByEmail(email);

        //DB에 없는 사용자라면 회원가입처리
        if (user == null) {
            user = registerNewUser(googleOAuth2User);
        }
        else{
            user = updateExistingUser(user, googleOAuth2User);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User updateExistingUser(User user, GoogleOAuth2User googleOAuth2User) {
        return userDetailsService.updateUser(user,googleOAuth2User);
    }

    private User registerNewUser(GoogleOAuth2User googleOAuth2User) {
        String email = googleOAuth2User.getEmail();
        String name = googleOAuth2User.getName();
        String profileImage = googleOAuth2User.getProfileImage();

        User registerUser = User.builder()
                    .email(email)
                    .name(name)
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .build();
        registerUser.setProviderType(ProviderType.google);
        userDetailsService.saveUser(registerUser);
        registerUser = userDetailsService.findByEmail(registerUser.getEmail());
        return registerUser;
    }
}
