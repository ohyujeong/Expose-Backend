package com.sm.expose.frame.respository;


import com.sm.expose.frame.domain.FrameUser;

public interface FrameUserRepositoryCustom {
    FrameUser findByFrameUser(Long frameId, Long userId);
}
