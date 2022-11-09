package com.sm.expose.frame.respository;

import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameCategoryRepository extends JpaRepository<FrameCategory, Long>, FrameCategoryRepositoryCustom{
    List<FrameCategory> findByFrame(Frame frame);
}
