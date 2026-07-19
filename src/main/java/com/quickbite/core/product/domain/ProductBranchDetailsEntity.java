package com.quickbite.core.product.domain;

import com.quickbite.core.retstaurant_branches.domain.BranchEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "product_branch_details",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_pbd_branch_product",
                columnNames = {"branch_id", "product_id"}
        ),
        indexes = {
                @Index(name = "idx_pbd_branch_id", columnList = "branch_id"),
                @Index(name = "idx_pbd_product_id", columnList = "product_id")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProductBranchDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pbd_branch_id"))
    private BranchEntity branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pbd_product_id"))
    private ProductEntity product;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
}