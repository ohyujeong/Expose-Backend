package com.sm.expose.global.security.dto;

import java.util.Map;

/*
OAuth 인증에 성공하면 Spring OAuth에 의해 전달될 OAuth2User 클래스의 인스턴스 매핑
getName() 을 재정의하고 getEmail() 메서드를 코딩하여 사용자 이름과 이메일을 각각 반환
 */
public class GoogleOAuth2User implements OAuth2UserImpl {

    private Map<String, Object> attributes;

    public GoogleOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProviderType() {
        return "google";
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getProfileImage() {
        return attributes.get("picture").toString();
    }
}
