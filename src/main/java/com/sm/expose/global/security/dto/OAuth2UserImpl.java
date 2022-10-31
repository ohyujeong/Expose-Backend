package com.sm.expose.global.security.dto;

import java.util.Map;

public interface OAuth2UserImpl {
    Map<String, Object> getAttributes();
    String getProviderId();
    String getProviderType();
    String getEmail();
    String getName();
    String getProfileImage();
}
