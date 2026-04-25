package ru.gigaden.electric_scooter_rental.repository.impl;

import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Реализация репозитория самокатов
 */
@Repository
public class ScooterRepositoryImpl extends BaseRepositoryHiber<Scooter, UUID> implements ScooterRepository {

    protected ScooterRepositoryImpl() {
        super(Scooter.class);
    }

    @Override
    public Scooter saveScooter(Scooter scooter) {
        return save(scooter);
    }

    @Override
    public Optional<Scooter> findScooterById(UUID id) {
        return Optional.ofNullable(find(id));
    }

    @Override
    public Collection<Scooter> findAllScooters(int page, int size, String sortBy) {
        return findAll(page, size, sortBy);
    }

    @Override
    public Scooter updateScooter(Scooter scooter) {
        return update(scooter);
    }

    @Override
    public void deleteScooterByEntity(Scooter scooter) {
        delete(scooter);
    }

    @Override
    public boolean checkScooterIsExistById(UUID id) {
        return exists(id);
    }
}
