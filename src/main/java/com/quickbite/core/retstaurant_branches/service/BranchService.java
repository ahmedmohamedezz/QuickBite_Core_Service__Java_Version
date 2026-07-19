package com.quickbite.core.retstaurant_branches.service;

import com.quickbite.core.common.exception.impl.UserUnAuthorizedException;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import com.quickbite.core.restaurant.service.RestaurantService;
import com.quickbite.core.retstaurant_branches.domain.BranchEntity;
import com.quickbite.core.retstaurant_branches.dto.*;
import com.quickbite.core.retstaurant_branches.exception.BranchNotFoundException;
import com.quickbite.core.retstaurant_branches.mapper.BranchMapper;
import com.quickbite.core.retstaurant_branches.repository.BranchRepository;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.enums.SystemRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final RestaurantService restaurantService;
    private final BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public List<NearbyBranchProjection> findNearby(BigDecimal lat, BigDecimal lng) {
        return branchRepository.findNearbyBranches(lat, lng);
    }

    @Transactional
    public BranchResponse create(
            Long restaurantId,
            Long userId,
            SystemRole userRole,
            BranchRegisterDto dto
    ) {
        // make sure the user owns this restaurant
        RestaurantDto restaurant = restaurantService.findById(restaurantId);
        if (
                !userRole.equals(SystemRole.system_admin) &&
                        !restaurant.ownerId().equals(userId)
        ) {
            throw new UserUnAuthorizedException();
        }

        BranchEntity branchEntity = BranchEntity.builder()
                .restaurant(restaurantService.getProxy(restaurantId))
                .countryCode(dto.countryCode())
                .addressText(dto.addressText())
                .label(dto.label())
                .lat(dto.lat())
                .lng(dto.lng())
                .opensAt(dto.opensAt())
                .closesAt(dto.closesAt())
                .deliveryRadius(dto.deliveryRadius())
                .currency(dto.currency())
                .isActive(false)
                .acceptOrders(true)
                .commission(0)
                .build();

        BranchEntity savedEntity = branchRepository.save(branchEntity);

        return BranchResponse.builder()
                .message("Branch created successfully")
                .branch(branchMapper.toDto(savedEntity))
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, List<BranchDto>> findBranches(Long restaurantId) {
        List<BranchEntity> branchEntities = branchRepository.findBranches(restaurantId);
        List<BranchDto> data = branchEntities.stream().map(branchMapper::toDto).toList();
        return Map.of("data", data);
    }

    @Transactional
    public BranchResponse update(Long userId, Long branchId, @Valid BranchUpdateDto dto) {
        BranchEntity branchEntity = branchRepository.findById(branchId).orElseThrow(BranchNotFoundException::new);
        UserEntity user = branchRepository.getOwner(branchId);

        if (user.getSystemRole().equals(SystemRole.restaurant_user) && !user.getId().equals(userId)) {
            throw new UserUnAuthorizedException();
        }

        branchMapper.updateEntityFromDto(dto, branchEntity);
        branchRepository.save(branchEntity);

        return BranchResponse.builder()
                .message("Branch updated")
                .branch(branchMapper.toDto(branchEntity))
                .build();
    }

    @Transactional
    public BranchResponse adminUpdate(Long branchId, @Valid BranchAdminUpdateDto dto) {
        BranchEntity entity =
                branchRepository.findById(branchId).orElseThrow(BranchNotFoundException::new);

        if (dto.isActive() != null) {
            entity.setIsActive(dto.isActive());
        }

        if (dto.commission() != null) {
            entity.setCommission(dto.commission());
        }

        branchRepository.save(entity);
        return BranchResponse.builder()
                .message("Branch updated")
                .branch(branchMapper.toDto(entity))
                .build();
    }
}
