package ru.gigaden.electric_scooter_rental.exception;

public class UserSubscriptionNotFoundException extends BaseException {

    private final String reason = "Ошибка при поиске подписки пользователя";

    public UserSubscriptionNotFoundException(String message) {
        super(message);
    }
}