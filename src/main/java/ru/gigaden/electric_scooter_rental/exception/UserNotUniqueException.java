package ru.gigaden.electric_scooter_rental.exception;

public class UserNotUniqueException extends BaseException {

  private static final String reason = "Конфликт уникальности данных";

  public UserNotUniqueException(String message) {
    super(message, reason);
  }
}