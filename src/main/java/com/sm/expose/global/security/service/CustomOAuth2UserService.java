package com.sm.expose.global.security.service;

import com.sm.expose.global.security.domain.Role;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.domain.UserPrincipal;
import com.sm.expose.global.security.dto.CustomOAuth2User;
import com.sm.expose.global.security.oauth.ProviderType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

///**
// * UserPrincipleDetailsService 클래스의 역할, User 정보를 가져온다.
// * 가져온 User의 정보를 UserPrinciple 클래스로 변경해 Spring Security로 전달.
// */
//@RequiredArgsConstructor
//@Service
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {

//    private final UserRepository userRepository;
//
//    /**
//     * OAuth2 공급자로부터 Access Token을 받은 이후 호출
//     * If, 동일한 이메일이 DB에 존재하지 않을 경우 사용자 정보를 등록하고
//     * 존재하면 사용자 정보 업데이트
//     */
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        try {
//            return processOAuthUser(userRequest, oAuth2User);
//        } catch (AuthenticationException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
//        }
//    }
//
//    /**
//     * 사용자 정보 추출
//     */
//    private OAuth2User processOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
//
//        OAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
//
//        System.out.println(oAuth2User.getName());
//        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
//        User user;
//
//        if (userOptional.isPresent()) {
//            user = userOptional.get();
//            user = updateExistingUser(user, oAuth2UserInfo);
//        } else {
//            user = registerNewUser(userRequest, oAuth2UserInfo);
//        }
//        return UserPrincipal.create(user);
//    }
//
//    //DB에 없을 때, 등록
//    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
//        return userRepository.save(
//                User.builder()
//                        .name(oAuth2UserInfo.getName())
//                        .email(oAuth2UserInfo.getEmail())
//                        .profileImage(oAuth2UserInfo.getProfileImage())
//                        .providerType(ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId()))
//                        .build()
//        );
//    }
//
//    //DB에 없을 때, 수정
//    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
//        return userRepository.save(
//                existingUser.update(oAuth2UserInfo.getName(), oAuth2UserInfo.getProfileImage()));
//    }
//}
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

    /**
     * 서드파티 접근을 위한 accessToken까지 얻은다음 실행된다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /** 유저의 정보를 가져옴 */
        OAuth2User oAuth2User = super.loadUser(userRequest);

//        /** 인증 서버 (구글) 의 정보를 가져옴 */
//        String provider = userRequest.getClientRegistration().getRegistrationId();

        /**
         * 새 유저 객체
         */
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(oAuth2User.getAttributes());

        String email = customOAuth2User.getEmail();
        String name = customOAuth2User.getName();
        String profileImage = customOAuth2User.getProfileImage();

        User existUser = userDetailsService.findByEmail(email);

        //DB에 없는 사용자라면 회원가입처리
        if (existUser == null) {
            existUser = User.builder()
                    .email(email)
                    .name(name)
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .build();
            existUser.setProviderType(ProviderType.google);
            userDetailsService.saveUser(existUser);
        }

        return UserPrincipal.create(existUser, oAuth2User.getAttributes());
    }
}
