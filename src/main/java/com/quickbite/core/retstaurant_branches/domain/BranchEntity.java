package com.quickbite.core.retstaurant_branches.domain;

import com.quickbite.core.common.enums.Currency;
import com.quickbite.core.restaurant.domain.RestaurantEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "restaurant_branches",
        indexes = {
                @Index(name = "idx_restaurant_branches_restaurant_id", columnList = "restaurant_id"),
                @Index(name = "idx_restaurant_branches_is_active", columnList = "is_active"),
        }
)
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Column(name = "address_text", nullable = false)
    private String addressText;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    @Column(name = "accept_orders", nullable = false)
    private Boolean acceptOrders;

    @Column(name = "delivery_radius", nullable = false)
    private Short deliveryRadius = 0;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "currency")
    private Currency currency;

    @Column(nullable = false)
    private Integer commission = 0;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
