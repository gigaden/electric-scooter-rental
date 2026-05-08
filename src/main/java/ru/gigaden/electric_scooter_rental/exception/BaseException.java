package ru.gigaden.electric_scooter_rental.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private String reason;

    public BaseException(String message) {
        super(message);
    }

}