package ru.gigaden.electric_scooter_rental.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Сущность для мапинга с БД подписочных тарифов
 */
@Entity
@Table(name = "subscription_tariffs")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionTariff {

    @Id
    @OneToOne
    @MapsId
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "discount_percent")
    private Short discountPercent;
}
