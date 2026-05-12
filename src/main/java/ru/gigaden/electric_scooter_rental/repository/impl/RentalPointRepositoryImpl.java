package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.repository.RentalPointRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория пользователей
 */
@Repository
public class RentalPointRepositoryImpl extends BaseRepositoryHiber<RentalPoint, UUID> implements RentalPointRepository {
  protected RentalPointRepositoryImpl() {
    super(RentalPoint.class);
  }

  @Override
  public RentalPoint saveRentalPoint(RentalPoint rentalPoint) {
    return save(rentalPoint);
  }

  @Override
  public Optional<RentalPoint> findRentalPointById(UUID id) {
    return Optional.ofNullable(find(id));
  }

  @Override
  public Collection<RentalPoint> findAllRentalPoints(int page, int size) {
    return findAll(page, size);
  }

  @Override
  public RentalPoint updateRentalPoint(RentalPoint rentalPoint) {
    return update(rentalPoint);
  }

  @Override
  public void deleteRentalPoint(RentalPoint rentalPoint) {
    delete(rentalPoint);
  }

  @Override
  public Collection<RentalPoint> findRentalPointsByRadius(double latitude, double longitude, double radiusKm, int page, int size) {
    String jpql = "SELECT p FROM RentalPoint p WHERE " +
                  "6371 * acos(cos(radians(:lat)) * cos(radians(p.latitude)) * " +
                  "cos(radians(p.longitude) - radians(:lon)) + " +
                  "sin(radians(:lat)) * sin(radians(p.latitude))) <= :radius " +
                  "ORDER BY p.address";
    TypedQuery<RentalPoint> query = entityManager.createQuery(jpql, RentalPoint.class)
        .setParameter("lat", latitude)
        .setParameter("lon", longitude)
        .setParameter("radius", radiusKm)
        .setFirstResult(page * size)
        .setMaxResults(size);
    return query.getResultList();
  }
}
