package ru.gigaden.electric_scooter_rental.exception;

public class TariffNotFoundException extends BaseException {

  private static final String reason = "Ошибка при поиске тарифа";

  public TariffNotFoundException(String message) {
    super(message, reason);
  }
}