package com.sm.expose.global.security.dto;

import java.util.Map;

/*
OAuth 인증에 성공하면 Spring OAuth에 의해 전달될 OAuth2User 클래스의 인스턴스 매핑
getName() 을 재정의하고 getEmail() 메서드를 코딩하여 사용자 이름과 이메일을 각각 반환
 */
public class KakaoOAuth2User implements OAuth2UserImpl {

    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    public KakaoOAuth2User(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProviderType() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return attributesAccount.get("email").toString();
    }

    @Override
    public String getName() {
        return attributesProfile.get("nickname").toString();
    }

    @Override
    public String getProfileImage() {
        return attributesProfile.get("thumbnail_image_url").toString();
    }
}
