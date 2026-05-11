package ru.gigaden.electric_scooter_rental.exception;

public class ScooterIsNotAvailableException extends BaseException {

  private static final String reason = "Самокат не свободен";

  public ScooterIsNotAvailableException(String message) {
    super(message, reason);
  }
}