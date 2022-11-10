package com.sm.expose.global.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sm.expose.global.common.ResponseMessage;
import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.dto.AuthResponse;
import com.sm.expose.global.security.dto.UserUpdateDto;
import com.sm.expose.global.security.repository.UserRepository;
import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Api(tags={"사용자 API"})
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/user/me")
    public User getCurrentUser(Principal principal) {
        try{
            User user = userRepository.findByEmail(principal.getName());
            return user;
        }catch (NullPointerException e){
            throw e;
        }
    }

    @ApiOperation(value = "사용자 취향 업데이트", notes = "사용자 취향 업데이트 엔드포인트")
    @PatchMapping("/user/update")
    public ResponseEntity<ResponseMessage> updateUserInfo(@RequestBody UserUpdateDto userUpdateDto, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        userDetailsService.updateUserTaste(user, userUpdateDto);
        return new ResponseEntity<>(ResponseMessage.withData(201, "취향 업데이트 성공", user), HttpStatus.CREATED);
    }


    @ApiOperation(value = "카카오 소셜 로그인 redirect")
    @GetMapping("/oauth/callback/kakao")
    public ResponseEntity<ResponseMessage> oauth2AuthorizationKaKao(@RequestParam("code") String code) throws JsonProcessingException {
        AuthResponse authResponse = userDetailsService.oauth2AuthorizationGoogle(code);
        return new ResponseEntity<>(ResponseMessage.withData(200, "로그인 성공", authResponse), HttpStatus.OK);
    }
}