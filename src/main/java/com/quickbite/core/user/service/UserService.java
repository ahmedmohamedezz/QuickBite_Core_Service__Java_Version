package com.quickbite.core.user.service;

import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.exception.UserNotFoundException;
import com.quickbite.core.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDto getByUserId(Long userId) {
        UserEntity user =
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return UserDto.fromEntity(user);
    }

    @Transactional
    public UserResponse update(Long userId, Map<String, String> updateData) {
        // get user
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // update fields
        if (updateData.containsKey("name")) {
            user.setName(updateData.get("name"));
        }

        if (updateData.containsKey("phone")) {
            user.setPhone(updateData.get("phone"));
        }

        // save updates
        UserEntity updatedUser = userRepository.save(user);
        return UserResponse.builder()
                .message("Profile updated")
                .user(UserDto.fromEntity(updatedUser))
                .build();
    }
}
