package com.quickbite.core.customer_addresses.repository;

import com.quickbite.core.customer_addresses.domain.CustomerAddressEntity;
import com.quickbite.core.customer_addresses.dto.CustomerAddressDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressesRepository extends JpaRepository<CustomerAddressEntity, Long> {
    List<CustomerAddressDto> findAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE CustomerAddressEntity entity SET entity.isDefault = false WHERE " +
            "entity.user.id = :userId AND entity.isDefault = true")
    void unsetCurrentDefaultAddress(Long userId);

    @Modifying
    @Query("DELETE FROM CustomerAddressEntity entity WHERE entity.user.id = :userId AND entity.id " +
            "= :addressId")
    void deleteUserAddress(Long userId, Long addressId);

    Optional<CustomerAddressEntity> findByUser_IdAndId(Long userId, Long addressId);
}
