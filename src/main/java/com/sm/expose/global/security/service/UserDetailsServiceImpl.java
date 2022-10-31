package com.sm.expose.global.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sm.expose.global.security.domain.Role;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.domain.UserPrincipal;
import com.sm.expose.global.security.dto.AuthResponse;
import com.sm.expose.global.security.dto.AuthorizationGoogle;
import com.sm.expose.global.security.dto.GoogleOAuth2User;
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

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final OAuth2Google oAuth2Google;
    private final AuthenticationManager authManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;

    public UserDetailsServiceImpl(UserRepository userRepository, OAuth2Google oAuth2Google, @Lazy AuthenticationManager authManager, @Lazy BCryptPasswordEncoder bCryptPasswordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.oAuth2Google = oAuth2Google;
        this.authManager = authManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
    }


    public User findByEmail(String email){
        return userRepository.findByEmail(email); //없으면 null, 있으면 user 객체 return
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public User updateUser(User user, GoogleOAuth2User googleOAuth2User){
        return userRepository.save(user.update(googleOAuth2User.getName(), googleOAuth2User.getProfileImage()));
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
        AuthorizationGoogle authorization = oAuth2Google.callTokenApi(code);
        System.out.println("authorization = " + authorization.getAccess_token());
        GoogleOAuth2User googleUserInfo = oAuth2Google.callGetUserByAccessToken(authorization.getAccess_token());
        System.out.println("googleUserInfo = " + googleUserInfo.getAttributes());

        String email = googleUserInfo.getEmail();
        String profileImage = googleUserInfo.getProfileImage();
        String password = googleUserInfo.getProviderId() + email;
        String name = googleUserInfo.getName();
        User user = findByEmail(email);

        if (user == null) {
            String encodedPassword = bCryptPasswordEncoder.encode(password);
            user = new User(email, name, encodedPassword, profileImage, Role.USER);
            user.setProviderType(ProviderType.google);
            userRepository.save(user);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwtToken = tokenProvider.createToken(auth);
        System.out.println("jwtToken = " + jwtToken);
        AuthResponse authResponse = new AuthResponse(jwtToken, user.getUserId());
        return authResponse;
    }
}
