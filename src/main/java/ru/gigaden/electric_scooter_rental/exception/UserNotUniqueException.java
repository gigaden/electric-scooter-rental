package ru.gigaden.electric_scooter_rental.exception;

public class UserNotUniqueException extends BaseException {

    private final String reason = "Ошибка при проверке уникальности пользователя";

    public UserNotUniqueException(String message) {
        super(message);
    }
}