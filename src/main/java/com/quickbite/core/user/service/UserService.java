package com.quickbite.core.user.service;

import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.exception.UserNotFoundException;
import com.quickbite.core.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getByUserId(Long userId) {
        UserEntity user =
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return UserResponse.fromEntity(user);
    }
}
