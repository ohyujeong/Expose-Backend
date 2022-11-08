package com.sm.expose.frame.respository;

import com.sm.expose.frame.domain.Frame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameRepository extends JpaRepository<Frame,Long>{
}
