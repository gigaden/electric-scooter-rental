package ru.gigaden.electric_scooter_rental.repository.impl;

import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Rental;
import ru.gigaden.electric_scooter_rental.repository.RentalRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория аренд
 */
@Repository
public class RentalRepositoryImpl extends BaseRepositoryHiber<Rental, UUID> implements RentalRepository {

    protected RentalRepositoryImpl() {
        super(Rental.class);
    }

    @Override
    public Rental createRental(Rental rental) {

        return save(rental);
    }

    @Override
    public Rental updateRental(Rental rental) {
        return update(rental);
    }

    @Override
    public Optional<Rental> findRentalById(UUID rentalId) {

        return Optional.ofNullable(find(rentalId));
    }

    @Override
    public Collection<Rental> findAllRentals(int page, int size) {

        return findAll(page, size);
    }
}
