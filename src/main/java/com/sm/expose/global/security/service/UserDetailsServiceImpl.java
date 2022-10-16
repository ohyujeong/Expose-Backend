package com.sm.expose.global.security.service;

import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.domain.UserPrincipal;
import com.sm.expose.global.security.dto.GoogleOAuth2User;
import com.sm.expose.global.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

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
}
