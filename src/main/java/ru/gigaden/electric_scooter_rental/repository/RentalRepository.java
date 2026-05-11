package ru.gigaden.electric_scooter_rental.repository;

import ru.gigaden.electric_scooter_rental.entity.Rental;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;
import ru.gigaden.electric_scooter_rental.entity.Role;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для управления арендами самокатов
 */
public interface RentalRepository {

    Rental createRental(Rental rental);

    Rental updateRental(Rental rental);

    Optional<Rental> findRentalById(UUID rentalId);

    Collection<Rental> findAllRentals(int page, int size);

    Collection<Rental> findRentalsByUserId(UUID userId, int page, int size);

    Collection<Rental> findRentalsByScooterId(UUID scooterId, int page, int size);

    Collection<Rental> findFinishedRentalsByScooterId(UUID scooterId, int page, int size);
}