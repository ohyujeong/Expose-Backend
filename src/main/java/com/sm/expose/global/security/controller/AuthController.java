package com.sm.expose.global.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sm.expose.global.common.ResponseMessage;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.dto.AuthResponse;
import com.sm.expose.global.security.repository.UserRepository;
import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        try{
            User user = userRepository.findByEmail(principal.getName());
            return user;
        }catch (NullPointerException e){
            throw e;
        }
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<ResponseMessage> oauth2AuthorizationGoogle(@RequestParam("code") String code) throws JsonProcessingException {
        AuthResponse authResponse = userDetailsService.oauth2AuthorizationGoogle(code);
        return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse), HttpStatus.OK);
    }
}