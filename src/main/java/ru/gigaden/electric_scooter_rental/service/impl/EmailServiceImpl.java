package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.service.EmailService;

/**
 * Сервис для отправки email.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  @Override
  public void sendWelcomeEmail(String to, String username) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Добро пожаловать в систему проката самокатов!");
    message.setText("Привет, " + username + "!\n\nВаш аккаунт успешно создан. Приятных поездок!");

    mailSender.send(message);
    log.info("Отправлено приветственное письмо пользователю {}", username);
  }
}