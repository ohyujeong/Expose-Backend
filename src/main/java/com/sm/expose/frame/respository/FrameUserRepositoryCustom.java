package com.sm.expose.frame.respository;


import com.sm.expose.frame.domain.FrameUser;

import java.util.List;

public interface FrameUserRepositoryCustom {
    FrameUser findByFrameUser(Long frameId, Long userId);
    List<FrameUser> findByFrame(Long frameId);
    List<FrameUser> findByUser(Long userId);
    List<FrameUser> findByOtherUser(Long frameId, Long userId);
    List<FrameUser> findByFrameUserLike(Long userId);
}
