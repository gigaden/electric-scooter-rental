package ru.gigaden.electric_scooter_rental.exception;

public class RentalPointNotFoundException extends BaseException {

  private static final String reason = "Ошибка при поиске точки аренды";

  public RentalPointNotFoundException(String message) {
    super(message, reason);
  }
}