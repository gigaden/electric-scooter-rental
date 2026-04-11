package ru.gigaden.electric_scooter_rental.exception;

public class UserNotFoundException extends BaseException {

    private final String reason = "Ошибка при поиске пользователя";

    public UserNotFoundException(String message) {
        super(message);
    }
}