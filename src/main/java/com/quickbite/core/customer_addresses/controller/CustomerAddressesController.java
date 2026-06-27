package com.quickbite.core.customer_addresses.controller;

import com.quickbite.core.common.security.UserPrincipal;
import com.quickbite.core.customer_addresses.dto.CustomerAddressDto;
import com.quickbite.core.customer_addresses.dto.CustomerAddressResponse;
import com.quickbite.core.customer_addresses.service.CustomerAddressesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerAddressesController {
    private final CustomerAddressesService customerAddressesService;

    @GetMapping("/addresses")
    public ResponseEntity<Map<String, List<CustomerAddressDto>>> findAllUserAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CustomerAddressDto> data = customerAddressesService.findAll(userPrincipal.getId());
        Map<String, List<CustomerAddressDto>> result = new HashMap<>();
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddressResponse> addCustomerAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CustomerAddressDto address
    ) {
        CustomerAddressResponse response =
                customerAddressesService.addCustomerAddress(userPrincipal.getId(), address);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> updateCustomerAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody CustomerAddressDto address,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CustomerAddressResponse response =
                customerAddressesService.updateCustomerAddress(userPrincipal.getId(), addressId, address);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<CustomerAddressResponse> deleteCustomerAddress(
            @PathVariable Long addressId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        customerAddressesService.deleteCustomerAddress(userPrincipal.getId(), addressId);

        CustomerAddressResponse response = CustomerAddressResponse.builder()
                .message("Address deleted")
                .build();

        return ResponseEntity.ok(response);
    }
}
