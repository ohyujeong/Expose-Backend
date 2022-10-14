package com.sm.expose.global.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer"; //인증방식

    @Builder
    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
//    private String jwtToken;
//    Long userId;
//
//    public AuthResponse(String jwtToken, Long id){
//        this.jwtToken=jwtToken;
//        this.userId=id;
//    }
}
