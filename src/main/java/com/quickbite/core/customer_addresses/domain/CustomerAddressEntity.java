package com.quickbite.core.customer_addresses.domain;

import com.quickbite.core.customer_addresses.enums.AddressType;
import com.quickbite.core.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "customer_addresses", indexes = {
                @Index(name = "idx_customer_addresses_user_id", columnList = "user_id")
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddressEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_customer_addresses_user_id"),
            nullable = false
    )
    private UserEntity user;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    private String building;

    @Column(name = "apartment_number")
    private String apartmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AddressType type;

    @Column(nullable = false)
    private BigDecimal lat;

    @Column(nullable = false)
    private BigDecimal lng;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}
