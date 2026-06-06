package com.quickbite.core.user.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return userService.getByUserId(principal.getId());
    }
}
