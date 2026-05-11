package ru.gigaden.electric_scooter_rental.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.dto.UserRegisteredEvent;
import ru.gigaden.electric_scooter_rental.service.EmailService;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

  private final EmailService emailService;

  @KafkaListener(topics = "${kafka.topics.user-registered}", groupId = "${spring.kafka.consumer.group-id}")
  public void handleUserRegistered(UserRegisteredEvent event) {
    log.info("Получено событие регистрации: {}", event);
    emailService.sendWelcomeEmail(event.email(), event.username());
  }
}