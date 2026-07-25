package com.quickbite.core.product.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickbite.core.branch.dto.BranchDto;
import com.quickbite.core.product.domain.ProductBranchDetailsEntity;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        String message,
        ProductDto product,
        ProductBranchDetailsDto branchDetails
) {
}
