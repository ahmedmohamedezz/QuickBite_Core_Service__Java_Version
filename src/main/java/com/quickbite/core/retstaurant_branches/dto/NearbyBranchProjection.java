package com.quickbite.core.retstaurant_branches.dto;

import com.quickbite.core.common.enums.Currency;

import java.math.BigDecimal;

public interface NearbyBranchProjection {
    Long getId();

    Long getRestaurantId();

    String getAddressText();

    String getLabel();

    BigDecimal getLat();

    BigDecimal getLng();

    Boolean getIsActive();

    Boolean getAcceptOrders();

    Currency getCurrency();

    String getName();

    String getLogoUrl();
}