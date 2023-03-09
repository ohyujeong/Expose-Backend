package com.sm.expose.global.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sm.expose.global.security.domain.Role;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.domain.UserPrincipal;
import com.sm.expose.global.security.dto.AuthResponse;
import com.sm.expose.global.security.dto.AuthorizationKakao;
import com.sm.expose.global.security.dto.KakaoOAuth2User;
import com.sm.expose.global.security.dto.UserUpdateDto;
import com.sm.expose.global.security.oauth.ProviderType;
import com.sm.expose.global.security.provider.TokenProvider;
import com.sm.expose.global.security.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final OAuth2Kakao oAuth2Kakao;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    public UserDetailsServiceImpl(UserRepository userRepository, OAuth2Kakao oAuth2Kakao, @Lazy AuthenticationManager authManager, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.oAuth2Kakao = oAuth2Kakao;
        this.authManager = authManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public User findUser(Principal principal){
        User user = userRepository.findByEmail(principal.getName());
        return user;
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email); //없으면 null, 있으면 user 객체 return
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User updateUser(User user, KakaoOAuth2User kakaoOAuth2User){
        return userRepository.save(user.update(kakaoOAuth2User.getName(), kakaoOAuth2User.getProfileImage()));
    }

    public void updateUserTaste(User user, UserUpdateDto userUpdateDto){
        if(userUpdateDto.getHalf() == null) {
            userUpdateDto.setHalf(user.getHalf());
        }
        if(userUpdateDto.getSelfie() == null) {
            userUpdateDto.setSelfie(user.getSelfie());
        }
        if(userUpdateDto.getSit() == null) {
            userUpdateDto.setSit(user.getSit());
        }
        if(userUpdateDto.getWhole() == null) {
            userUpdateDto.setWhole(user.getWhole());
        }
        if(userUpdateDto.getTwo() == null) {
            userUpdateDto.setTwo(user.getTwo());
        }
        if(userUpdateDto.getMany() == null) {
            userUpdateDto.setMany(user.getMany());
        }
        user.updateTaste(userUpdateDto);
        userRepository.save(user);
    }

    // loadUserByUsername 은 DB에 접근해서 사용자 정보를 가져오는 역할을 함
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Can not find email.");
        }
        return UserPrincipal.create(user);
    }

    public AuthResponse oauth2AuthorizationGoogle(String code) throws JsonProcessingException {
        AuthorizationKakao authorization = oAuth2Kakao.callTokenApi(code);
        KakaoOAuth2User googleUserInfo = oAuth2Kakao.callGetUserByAccessToken(authorization.getAccess_token());

        String email = googleUserInfo.getEmail();
        String profileImage = googleUserInfo.getProfileImage();
        String password = googleUserInfo.getProviderId() + email;
        String nickname = googleUserInfo.getName();
        User user = findByEmail(email);

        if (user == null) {
            String encodedPassword = bCryptPasswordEncoder.encode(password);
            user = new User(email, nickname, encodedPassword, profileImage, Role.USER);
            user.setProviderType(ProviderType.kakao);
            userRepository.save(user);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwtToken = tokenProvider.createToken(auth);
        AuthResponse authResponse = new AuthResponse(jwtToken, user.getUserId(), user.getNickname());
        return authResponse;
    }
}
