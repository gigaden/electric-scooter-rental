package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.exception.DatabaseException;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Имплементация репозитория тарифов.
 */
@Repository
@Slf4j
public class TariffRepositoryImpl extends BaseRepositoryHiber<Tariff, UUID> implements TariffRepository {

  public TariffRepositoryImpl() {
    super(Tariff.class);
  }

  @Override
  public Optional<Tariff> findTariffById(UUID tariffId) {
    return Optional.ofNullable(find(tariffId));
  }

  @Override
  public Optional<Tariff> findTariffByName(String tariffName) {
    try {
      String jpql = "SELECT t FROM Tariff t WHERE t.name = :tariffName";
      TypedQuery<Tariff> query = entityManager.createQuery(jpql, Tariff.class)
          .setParameter("tariffName", tariffName);

      return Optional.ofNullable(query.getSingleResult());
    } catch (HibernateException e) {
      throw new DatabaseException("Ошибка получения тарифа с именем " + tariffName + e);
    }
  }

  @Override
  public Tariff addTariff(Tariff tariff) {
    return save(tariff);
  }

  @Override
  public void deleteTariffById(UUID tariffId) {
    delete(find(tariffId));
  }
}
