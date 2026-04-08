package ru.gigaden.electric_scooter_rental.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Сущность для мапинга с БД тарифов
 */
@Entity
@Table(name = "tariffs")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TariffType type;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToOne(mappedBy = "tariff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HourlyTariff hourlyTariff;

    @OneToOne(mappedBy = "tariff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SubscriptionTariff subscriptionTariff;
}
