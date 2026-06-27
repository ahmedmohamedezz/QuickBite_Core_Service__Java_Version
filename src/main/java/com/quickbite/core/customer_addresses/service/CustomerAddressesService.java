package com.quickbite.core.customer_addresses.service;

import com.quickbite.core.customer_addresses.domain.CustomerAddressEntity;
import com.quickbite.core.customer_addresses.dto.CustomerAddressDto;
import com.quickbite.core.customer_addresses.dto.CustomerAddressResponse;
import com.quickbite.core.customer_addresses.exception.CustomerAddressNotFoundException;
import com.quickbite.core.customer_addresses.mapper.CustomerAddressMapper;
import com.quickbite.core.customer_addresses.repository.CustomerAddressesRepository;
import com.quickbite.core.user.domain.UserEntity;
import com.quickbite.core.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerAddressesService {
    private final CustomerAddressesRepository customerAddressesRepository;
    private final UserService userService;
    private final CustomerAddressMapper addressMapper;

    @Transactional(readOnly = true)
    public List<CustomerAddressDto> findAll(Long userId) {
        return customerAddressesRepository.findAllByUserId(userId);
    }

    @Transactional
    public CustomerAddressResponse addCustomerAddress(Long userId, CustomerAddressDto address) {
        CustomerAddressEntity entity = addressMapper.toEntity(address);

        // get user proxy without hitting the db
        UserEntity userProxy = userService.getUserProxy(userId);
        entity.setUser(userProxy);

        CustomerAddressEntity saved = customerAddressesRepository.save(entity);

        return CustomerAddressResponse.builder()
                .message("Address added")
                .address(addressMapper.toDto(saved))
                .build();
    }

    @Transactional
    public CustomerAddressResponse updateCustomerAddress(
            Long userId, Long addressId, CustomerAddressDto address
    ) {
        CustomerAddressEntity entity = customerAddressesRepository
                .findByUser_IdAndId(userId, addressId)
                .orElseThrow(CustomerAddressNotFoundException::new);

        // if default address override
        if (Boolean.TRUE.equals(address.isDefault())) {
            customerAddressesRepository.unsetCurrentDefaultAddress(userId);
        }

        addressMapper.updateEntityFromDto(address, entity);

        return CustomerAddressResponse.builder()
                .message("Address updated")
                .address(addressMapper.toDto(entity))
                .build();
    }

    @Transactional
    public void deleteCustomerAddress(Long userId, Long addressId) {
        customerAddressesRepository.deleteUserAddress(userId, addressId);
    }
}
