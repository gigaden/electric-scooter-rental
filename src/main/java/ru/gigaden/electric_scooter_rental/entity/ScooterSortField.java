package ru.gigaden.electric_scooter_rental.entity;

import lombok.Getter;

/**
 * Enum с полями для сортировки пользователя
 */
@Getter
public enum ScooterSortField {
  BATTERY("batteryPower"),
  MILEAGE("mileage");

  private final String field;

  ScooterSortField(String field) {
    this.field = field;
  }

  public static ScooterSortField fromString(String value) {
    for (ScooterSortField sortField : ScooterSortField.values()) {
      if (sortField.name().equalsIgnoreCase(value) || sortField.field.equalsIgnoreCase(value)) {
        return sortField;
      }
    }
    throw new IllegalArgumentException("Неизвестное поле сортировки: " + value);
  }
}
