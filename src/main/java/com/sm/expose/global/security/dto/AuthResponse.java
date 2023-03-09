package com.sm.expose.global.security.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String jwtToken;
    private Long userId;
    private String nickName;

    public AuthResponse(String jwtToken, Long id, String nickName){
        this.jwtToken=jwtToken;
        this.userId=id;
        this.nickName=nickName;
    }
}