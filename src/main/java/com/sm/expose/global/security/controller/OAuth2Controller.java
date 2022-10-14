package com.sm.expose.global.security.controller;

import com.sm.expose.global.security.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class OAuth2Controller {
    private final UserDetailsServiceImpl userDetailsService;

    @GetMapping("/")
    public String index(){
        return "main";
    }
}
