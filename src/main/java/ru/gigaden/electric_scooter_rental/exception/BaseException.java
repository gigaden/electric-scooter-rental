package ru.gigaden.electric_scooter_rental.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

  private final String reason;

  public BaseException(String message, String reason) {
    super(message);
    this.reason = reason;
  }

}