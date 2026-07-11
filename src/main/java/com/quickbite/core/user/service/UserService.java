package com.quickbite.core.user.service;

import com.quickbite.core.auth.dto.RestaurantOwnerDto;
import com.quickbite.core.auth.dto.UserRegisterDto;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.dto.UserResponse;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.exception.UserNotFoundException;
import com.quickbite.core.user.mapper.UserMapper;
import com.quickbite.core.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto getByUserId(Long userId) {
        UserEntity user =
                userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return userMapper.toDto(user);
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
                .user(userMapper.toDto(updatedUser))
                .build();
    }

    public UserEntity getUserProxy(Long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Transactional
    public UserDto createRestaurantOwner(RestaurantOwnerDto restaurantOwner) {
        UserEntity user = userMapper.fromRestaurantOwner(restaurantOwner);
        user.setSystemRole(SystemRole.restaurant_user);

        UserEntity savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
