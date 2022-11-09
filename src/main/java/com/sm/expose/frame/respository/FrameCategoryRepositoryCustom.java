package com.sm.expose.frame.respository;

import com.sm.expose.frame.domain.FrameCategory;

import java.util.List;

public interface FrameCategoryRepositoryCustom {
    List<FrameCategory> findByCategoryId(long categoryId);
}
