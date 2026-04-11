package ru.gigaden.electric_scooter_rental.exception;

public class RoleNotFoundException extends BaseException {

    private final String reason = "Ошибка при поиске роли пользователя";

    public RoleNotFoundException(String message) {
        super(message);
    }
}