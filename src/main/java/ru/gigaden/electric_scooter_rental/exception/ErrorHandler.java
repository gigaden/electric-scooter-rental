package ru.gigaden.electric_scooter_rental.exception;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Класс отлавливает исключения и возвращает ответ в нужном формате.
 */
@RestControllerAdvice
public class ErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

  @ExceptionHandler({
      UserNotFoundException.class,
      RoleNotFoundException.class,
      RentalPointNotFoundException.class,
      ScooterNotFoundException.class,
      RentalNotFoundException.class,
      TariffNotFoundException.class,
      UserSubscriptionNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFoundException(final BaseException e, WebRequest request) {
    log.error("Ошибка 404 {}: {} в запросе {}",
        e.getClass().getSimpleName(), e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.NOT_FOUND, e.getReason());
  }

  @ExceptionHandler({
      DatabaseException.class
  })
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleConnectionException(final BaseException e, WebRequest request) {
    log.error("Ошибка 500 {}: {} в запросе {}",
        e.getClass().getSimpleName(), e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, e.getReason());
  }

  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      ValidationException.class,
      NumberFormatException.class,
      HttpMessageNotReadableException.class,
      IllegalArgumentException.class,
      DateTimeParseException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> invalidMethodArgument(Exception e, WebRequest request) {
    log.error("Ошибка  400 {}: {} в запросе {}",
        e.getClass(), e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "Неверный формат запроса");
  }

  @ExceptionHandler({
      ScooterIsNotAvailableException.class,
      RentalCompleteException.class,
      SubscriptionException.class,
      UserNotUniqueException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  public Map<String, String> handleConflictException(final BaseException e, WebRequest request) {
    log.error("Ошибка  409 {}: {} в запросе {}",
        e.getClass(), e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.BAD_REQUEST, e.getReason());
  }

  @ExceptionHandler({
      BadCredentialsException.class,
      AuthenticationCredentialsNotFoundException.class
  })
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Map<String, String> handleAuth(Exception e, WebRequest request) {
    return buildErrorResponse(e, HttpStatus.UNAUTHORIZED, "Ошибка аутентификации");
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public Map<String, String> handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
    log.error("Ошибка 403 {}: {} в запросе {}",
        e.getClass(), e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.FORBIDDEN, "Недостаточно прав");
  }

  @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public Map<String, String> handleUnsupportedMediaType(org.springframework.web.HttpMediaTypeNotSupportedException e, WebRequest request) {
    log.error("Ошибка 415 Неподдерживаемый тип контента: {} в запросе {}",
        e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Неподдерживаемый формат данных");
  }

  @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  public Map<String, String> handleMethodNotAllowed(org.springframework.web.HttpRequestMethodNotSupportedException e, WebRequest request) {
    log.error("Ошибка 405 Недопустимый метод: {} в запросе {}",
        e.getMessage(), request.getDescription(false));
    return buildErrorResponse(e, HttpStatus.METHOD_NOT_ALLOWED, "Метод не поддерживается для этого ресурса");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleGlobalException(Exception e, WebRequest request) {
    log.error("❗ Неожиданная ошибка 500: {} в запросе {}",
        e.getClass().getSimpleName(), request.getDescription(false), e);
    return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
  }

  public Map<String, String> buildErrorResponse(Exception e, HttpStatus status, String reason) {
    Map<String, String> response = new LinkedHashMap<>();
    response.put("status", status.name());
    response.put("reason", reason);
    response.put("message", e.getMessage());
    response.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    return response;
  }
}