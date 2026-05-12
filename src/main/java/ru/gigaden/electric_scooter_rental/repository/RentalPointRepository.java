package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.RentalPoint;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с точками аренды
 */
public interface RentalPointRepository {
  RentalPoint saveRentalPoint(RentalPoint rentalPoint);

  Optional<RentalPoint> findRentalPointById(UUID id);

  Collection<RentalPoint> findAllRentalPoints(int page, int size);

  RentalPoint updateRentalPoint(RentalPoint rentalPoint);

  void deleteRentalPoint(RentalPoint rentalPoint);

  Collection<RentalPoint> findRentalPointsByRadius(double latitude, double longitude, double radiusKm, int page, int size);
}
