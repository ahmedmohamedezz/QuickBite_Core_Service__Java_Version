package com.quickbite.core.restaurant.service;

import com.quickbite.core.common.exception.impl.UserUnAuthorizedException;
import com.quickbite.core.restaurant.domain.RestaurantEntity;
import com.quickbite.core.restaurant.dto.*;
import com.quickbite.core.restaurant.enums.RestaurantStatus;
import com.quickbite.core.restaurant.exception.RestaurantNotFoundException;
import com.quickbite.core.restaurant.mapper.RestaurantMapper;
import com.quickbite.core.restaurant.repository.RestaurantRepository;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.dto.UserDto;
import com.quickbite.core.user.enums.SystemRole;
import com.quickbite.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final UserService userService;

    @Transactional
    public RestaurantDto create(Long userId, RestaurantRegisterDto data) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setName(data.name());
        restaurantEntity.setLogoURL(data.logoURL());
        restaurantEntity.setPrimaryCountry(data.primaryCountry());
        restaurantEntity.setOwner(userService.getUserProxy(userId));
        restaurantEntity.setStatusUpdatedAt(LocalDateTime.now());
        restaurantEntity.setStatus(RestaurantStatus.pending);

        RestaurantEntity saved = restaurantRepository.save(restaurantEntity);
        return restaurantMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<RestaurantDto> findAll() {
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();

        return restaurantEntities.stream()
                .map(restaurantMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public RestaurantDto findById(Long id) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id).orElseThrow(RestaurantNotFoundException::new);
        return restaurantMapper.toDto(restaurantEntity);
    }

    @Transactional
    public RestaurantResponse create(RestaurantWithOwnerDto dto) {
        UserDto owner = userService.createRestaurantOwner(dto.owner());

        RestaurantEntity toBeSaved = RestaurantEntity.builder()
                .name(dto.name())
                .logoURL(dto.logoURL())
                .primaryCountry(dto.primaryCountry())
                .status(RestaurantStatus.pending)
                .statusUpdatedAt(LocalDateTime.now())
                .owner(userService.getUserProxy(owner.id()))
                .build();

        RestaurantEntity restaurantCreatedEntity = restaurantRepository.save(toBeSaved);
        RestaurantDto restaurant = restaurantMapper.toDto(restaurantCreatedEntity);

        return RestaurantResponse.builder()
                .message("Restaurant created successfully (with owner)")
                .restaurant(restaurant)
                .owner(owner)
                .build();
    }

    @Transactional
    public RestaurantResponse update(Long userId, Long restaurantId, RestaurantUpdateDto dto) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId).orElseThrow(RestaurantNotFoundException::new);

        // validate user is restaurant owner, if not system_admin
        UserDto user = userService.getByUserId(userId);
        if (user.systemRole().equals(SystemRole.restaurant_user) && !restaurant.getOwner().getId().equals(user.id())) {
            throw new UserUnAuthorizedException();
        }

        restaurantMapper.updateEntityFromDto(dto, restaurant);
        restaurant = restaurantRepository.save(restaurant);

        return RestaurantResponse.builder()
                .message("Restaurant Updated")
                .restaurant(restaurantMapper.toDto(restaurant))
                .build();
    }

    @Transactional
    public RestaurantResponse adminUpdate(Long restaurantId, RestaurantAdminUpdateDto dto) {
        RestaurantEntity entity = restaurantRepository.findById(restaurantId).orElseThrow(RestaurantNotFoundException::new);

        if (dto.status() != null) {
            entity.setStatus(dto.status());
            entity.setStatusUpdatedAt(LocalDateTime.now());
        }

        restaurantRepository.save(entity);
        return RestaurantResponse.builder()
                .message("Restaurant Updated")
                .restaurant(restaurantMapper.toDto(entity))
                .build();
    }

    public RestaurantEntity getProxy(Long id) {
        return restaurantRepository.getReferenceById(id);
    }
}
