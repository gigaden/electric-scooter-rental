package ru.gigaden.electric_scooter_rental.exception;

public class SubscriptionException extends BaseException {

  private static final String reason = "Ошибка при обработке подписки";

  public SubscriptionException(String message) {
    super(message, reason);
  }
}