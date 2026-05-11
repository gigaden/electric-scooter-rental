package ru.gigaden.electric_scooter_rental.service;

import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalUpdateStatusDto;

import java.util.Collection;
import java.util.UUID;

/**
 * Интерфейс для сервиса аренды
 */
public interface RentalService {

    RentalResponseDto createRental(RentalCreateDto dto);

    RentalResponseDto findRentalById(UUID rentalId);

    Collection<RentalResponseDto> findAllRentals(int page, int size);

    Collection<RentalResponseDto> findRentalsByUserId(UUID userId, int page, int size);

    Collection<RentalResponseDto> findFinishedRentalsByScooterId(UUID scooterId, int page, int size);

    RentalResponseDto completeRental(UUID rentalId);
}
