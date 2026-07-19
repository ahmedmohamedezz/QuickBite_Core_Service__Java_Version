package com.quickbite.core.product.domain;
import com.quickbite.core.restaurant.domain.RestaurantEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "product_categories",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_product_categories_restaurant_name",
                columnNames = {"restaurant_id", "name"}
        ),
        indexes = @Index(name = "idx_product_categories_restaurant_id", columnList = "restaurant_id")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ProductCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_categories_restaurant_id"))
    private RestaurantEntity restaurant;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductEntity> products;
}