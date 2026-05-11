package ru.gigaden.electric_scooter_rental.service;

public interface EmailService {

  void sendWelcomeEmail(String to, String username);
}