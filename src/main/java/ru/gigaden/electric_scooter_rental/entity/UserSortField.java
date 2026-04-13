package ru.gigaden.electric_scooter_rental.entity;

import lombok.Getter;

/**
 * Enum с полями для сортировки пользователя
 */
@Getter
public enum UserSortField {
    USERNAME("username"),
    REGISTERED("registeredOn");

    private final String field;

    UserSortField(String field) {
        this.field = field;
    }

    public static UserSortField fromString(String value) {
        for (UserSortField sortField : UserSortField.values()) {
            if (sortField.name().equalsIgnoreCase(value) || sortField.field.equalsIgnoreCase(value)) {
                return sortField;
            }
        }
        throw new IllegalArgumentException("Неизвестное поле сортировки: " + value);
    }
}
