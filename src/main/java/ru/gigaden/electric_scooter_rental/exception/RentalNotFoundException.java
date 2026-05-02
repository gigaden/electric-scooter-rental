package ru.gigaden.electric_scooter_rental.exception;

public class RentalNotFoundException extends BaseException {

    private final String reason = "Ошибка при поиске аренды";

    public RentalNotFoundException(String message) {
        super(message);
    }
}