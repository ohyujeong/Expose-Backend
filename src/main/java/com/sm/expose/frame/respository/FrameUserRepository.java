package com.sm.expose.frame.respository;

import com.sm.expose.frame.domain.FrameUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FrameUserRepository extends JpaRepository<FrameUser, Long>, FrameUserRepositoryCustom {
}
