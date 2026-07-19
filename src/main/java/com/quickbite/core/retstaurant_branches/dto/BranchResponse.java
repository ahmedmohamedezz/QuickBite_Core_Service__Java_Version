package com.quickbite.core.retstaurant_branches.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.restaurant.dto.RestaurantDto;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record BranchResponse(
        String message,
        BranchDto branch,
        RestaurantDto restaurant
) {
}
