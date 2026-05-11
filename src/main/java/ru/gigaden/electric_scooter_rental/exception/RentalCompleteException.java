package ru.gigaden.electric_scooter_rental.exception;

public class RentalCompleteException extends BaseException {

  private static final String reason = "Ошибка при завершении аренды";

  public RentalCompleteException(String message) {
    super(message, reason);
  }
}