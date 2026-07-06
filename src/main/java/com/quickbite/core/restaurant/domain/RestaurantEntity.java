package com.quickbite.core.restaurant.domain;

import com.quickbite.core.restaurant.enums.RestaurantStatus;
import com.quickbite.core.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "restaurants", indexes = {
        @Index(name = "idx_restaurants_owner_id", columnList = "owner_id"),
        @Index(name = "idx_restaurants_status", columnList = "status"),
        @Index(name = "idx_restaurants_primary_country", columnList = "primary_country"),
        @Index(name = "idx_restaurants_created_at", columnList = "created_at")
})
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RestaurantStatus status;

    @Column(name = "logo_url", nullable = false)
    private String logoURL;

    @Column(name = "primary_country", nullable = false)
    private String primaryCountry;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "status_updated_at", nullable = false)
    private LocalDateTime statusUpdatedAt;
}
