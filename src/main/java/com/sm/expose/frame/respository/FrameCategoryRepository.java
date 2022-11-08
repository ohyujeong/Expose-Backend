package com.sm.expose.frame.respository;

import com.sm.expose.frame.domain.Category;
import com.sm.expose.frame.domain.Frame;
import com.sm.expose.frame.domain.FrameCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FrameCategoryRepository extends JpaRepository<FrameCategory, Long> {
    List<FrameCategory> findByFrame(Frame frame);
    List<FrameCategory> findByCategory(Category category);
}
