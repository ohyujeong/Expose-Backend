package com.sm.expose.global.security.controller;

import com.sm.expose.global.security.domain.User;
import com.sm.expose.global.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public User getCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        return user;
    }
}