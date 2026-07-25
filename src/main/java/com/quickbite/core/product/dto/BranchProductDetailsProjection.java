package com.quickbite.core.product.dto;

public interface BranchProductDetailsProjection {
    Long getId();

    String getName();

    String getDescription();

    String getImageUrl();

    Long getRestaurantId();

    Long getCategoryId();

    String getCategoryName();

    Integer getPrice();

    Integer getStock();

    Boolean getIsActive();
}
