package ru.gigaden.electric_scooter_rental.entity;

import lombok.Getter;

/**
 * Enum с полями для сортировки точек проката
 */
@Getter
public enum RentalPointSortField {
  TOTAL_SCOOTERS,
  AVAILABLE_SCOOTERS
}
