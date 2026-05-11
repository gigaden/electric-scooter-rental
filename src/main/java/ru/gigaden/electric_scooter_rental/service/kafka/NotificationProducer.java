package ru.gigaden.electric_scooter_rental.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.gigaden.electric_scooter_rental.dto.UserRegisteredEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Value("${kafka.topics.user-registered}")
  private String userRegisteredTopic;

  public void sendUserRegisteredEvent(UserRegisteredEvent event) {
    log.info("Отправляем событие регистрации пользователя {} в тему {}", event.userId(), userRegisteredTopic);
    kafkaTemplate.send(userRegisteredTopic, "event", event);
  }
}