package ru.gigaden.electric_scooter_rental.entity;

/**
 * Статус, в котором может находиться самокат
 */
public enum ScooterStatus {
  AVAILABLE,
  RENTED,
  OUT_OF_SERVICE,
  MAINTENANCE
}
