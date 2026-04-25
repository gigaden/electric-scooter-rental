package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.Scooter;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с самокатами
 */
public interface ScooterRepository {

    Scooter saveScooter(Scooter scooter);

    Optional<Scooter> findScooterById(UUID id);

    Collection<Scooter> findAllScooters(int page, int size, String sortBy);

    Scooter updateScooter(Scooter scooter);

    void deleteScooterByEntity(Scooter scooter);

    boolean checkScooterIsExistById(UUID id);
}