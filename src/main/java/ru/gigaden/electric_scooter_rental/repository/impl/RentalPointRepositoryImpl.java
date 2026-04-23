package ru.gigaden.electric_scooter_rental.repository.impl;

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
    public Collection<RentalPoint> findAllRentalPoints(int page, int size, String sortBy) {
        return findAll(page, size, sortBy);
    }

    @Override
    public RentalPoint updateRentalPoint(RentalPoint rentalPoint) {
        return update(rentalPoint);
    }

    @Override
    public void deleteRentalPoint(RentalPoint rentalPoint) {
        delete(rentalPoint);
    }
}
