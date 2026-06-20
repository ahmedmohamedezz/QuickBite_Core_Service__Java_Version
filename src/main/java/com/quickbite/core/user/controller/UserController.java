package com.quickbite.core.user.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getByUserId(principal.getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal UserPrincipal principal
            , @RequestBody Map<String, String> updateData) {

        UserResponse response = userService.update(principal.getId(), updateData);
        return ResponseEntity.ok(response);
    }
}
