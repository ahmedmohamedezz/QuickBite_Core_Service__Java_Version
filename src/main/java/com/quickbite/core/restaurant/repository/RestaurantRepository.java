package com.quickbite.core.restaurant.repository;

import com.quickbite.core.restaurant.domain.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
