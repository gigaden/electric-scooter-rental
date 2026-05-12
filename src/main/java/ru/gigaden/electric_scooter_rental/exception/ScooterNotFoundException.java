package ru.gigaden.electric_scooter_rental.exception;

public class ScooterNotFoundException extends BaseException {

  private static final String reason = "Ошибка при поиске самоката";

  public ScooterNotFoundException(String message) {
    super(message, reason);
  }
}