package ru.gigaden.electric_scooter_rental.exception;

public class ScooterNotFoundException extends BaseException {

    private final String reason = "Ошибка при поиске самоката";

    public ScooterNotFoundException(String message) {
        super(message);
    }
}