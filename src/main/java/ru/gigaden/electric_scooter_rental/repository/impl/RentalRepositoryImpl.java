package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.TypedQuery;
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

    @Override
    public Collection<Rental> findRentalsByUserId(UUID userId, int page, int size) {
        String jpql = "SELECT r FROM Rental r WHERE r.user.id = :userId ORDER BY r.startDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class)
            .setParameter("userId", userId)
            .setFirstResult(page * size)
            .setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public Collection<Rental> findRentalsByScooterId(UUID scooterId, int page, int size) {
        String jpql = "SELECT r FROM Rental r WHERE r.scooter.id = :scooterId ORDER BY r.startDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class)
            .setParameter("scooterId", scooterId)
            .setFirstResult(page * size)
            .setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public Collection<Rental> findFinishedRentalsByScooterId(UUID scooterId, int page, int size) {
        String jpql = "SELECT r FROM Rental r WHERE r.scooter.id = :scooterId AND r.status = 'FINISHED' ORDER BY r.endDate DESC";
        TypedQuery<Rental> query = entityManager.createQuery(jpql, Rental.class)
            .setParameter("scooterId", scooterId)
            .setFirstResult(page * size)
            .setMaxResults(size);
        return query.getResultList();
    }
}
