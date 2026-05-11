package ru.gigaden.electric_scooter_rental.exception;

public class DatabaseException extends BaseException {

  private static final String reason = "Ошибка при запросе к базе данных";

  public DatabaseException(String message) {
    super(message, reason);
  }
}