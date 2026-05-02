package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalCreateDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalResponseDto;
import ru.gigaden.electric_scooter_rental.dto.rental.RentalUpdateStatusDto;
import ru.gigaden.electric_scooter_rental.entity.Rental;
import ru.gigaden.electric_scooter_rental.entity.RentalStatus;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.entity.Tariff;
import ru.gigaden.electric_scooter_rental.entity.User;
import ru.gigaden.electric_scooter_rental.entity.UserSubscription;
import ru.gigaden.electric_scooter_rental.exception.RentalNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.ScooterIsNotAvailableException;
import ru.gigaden.electric_scooter_rental.exception.ScooterNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.TariffNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserNotFoundException;
import ru.gigaden.electric_scooter_rental.exception.UserSubscriptionNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RentalMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalRepository;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;
import ru.gigaden.electric_scooter_rental.repository.TariffRepository;
import ru.gigaden.electric_scooter_rental.repository.UserRepository;
import ru.gigaden.electric_scooter_rental.repository.UserSubscriptionRepository;
import ru.gigaden.electric_scooter_rental.service.RentalService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * Реализация сервиса аренды
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;
    private final TariffRepository tariffRepository;
    private final UserSubscriptionRepository subscriptionRepository;

    @Transactional
    @Override
    public RentalResponseDto createRental(RentalCreateDto dto) {

        Rental newRental = buildRentalFromDto(dto);
        Scooter scooter = newRental.getScooter();

        Rental savedRental = rentalRepository.createRental(newRental);
        scooter.setStatus(ScooterStatus.RENTED);
        scooterRepository.updateScooter(scooter);

        RentalResponseDto response = rentalMapper.mapRentalToResponseDto(savedRental);
        log.info("Создана новая аренда с id = {}", savedRental.getId());

        return response;
    }

    /**
     * Ищем аренду по id
     *
     * @param rentalId - id аренды
     * @return - дто аренды
     * @throws RentalNotFoundException - если аренда не найдена
     */
    @Override
    public RentalResponseDto findRentalById(UUID rentalId) {

        Rental rental = getRowRentalOrThrow(rentalId);
        RentalResponseDto response = rentalMapper.mapRentalToResponseDto(rental);
        log.info("Получили аренду с id = {}", rentalId);

        return response;
    }

    /**
     * Ищем все аренды
     *
     * @param size - размер списка
     * @param page - начальная страница
     * @return - список аренд
     */
    @Override
    public Collection<RentalResponseDto> findAllRentals(int page, int size) {

        Collection<RentalResponseDto> response = rentalRepository.findAllRentals(page, size).stream()
                .map(rentalMapper::mapRentalToResponseDto)
                .toList();
        log.info("Нашли все аренды page = {} size = {}", page, size);

        return response;
    }

    /**
     * Обновляем статус аренды
     *
     * @param rentalId - id аренды
     * @param dto      - дто со статусом
     * @return - дто с арендой
     * @throws RentalNotFoundException - если аренда не найдена
     */
    @Transactional
    @Override
    public RentalResponseDto updateRentalStatus(UUID rentalId, RentalUpdateStatusDto dto) {

        Rental rental = getRowRentalOrThrow(rentalId);
        rental.setStatus(dto.status());
        Rental updatedRental = rentalRepository.updateRentalStatus(rental);
        RentalResponseDto response = rentalMapper.mapRentalToResponseDto(updatedRental);
        log.info("Обновили статус аренды с id = {} status = {}", rentalId, dto.status());

        return response;
    }

    /**
     * Ищем незамапенную аренда по id
     */
    private Rental getRowRentalOrThrow(UUID rentalId) {
        return rentalRepository.findRentalById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException("Аренда с id = %s не найдена".formatted(rentalId)));
    }

    /**
     * Собираем новую аренду из разных сущностей
     */
    private Rental buildRentalFromDto(RentalCreateDto dto) {
        User user = userRepository.findUserById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = %s не найден".formatted(dto.userId())));

        Scooter scooter = scooterRepository.findScooterById(dto.scooterId())
                .orElseThrow(() -> new ScooterNotFoundException("Самокат с id = %s не найден".formatted(dto.scooterId())));

        if (!scooter.getStatus().equals(ScooterStatus.AVAILABLE)) {
            throw new ScooterIsNotAvailableException("Самокат с id = %s занят".formatted(scooter.getId()));
        }

        Tariff tariff = null;
        if (dto.tariffId() != null) {
            tariff = tariffRepository.findTariffById(dto.tariffId())
                    .orElseThrow(() -> new TariffNotFoundException("Тариф с id = %s не найден".formatted(dto.tariffId())));
        }

        UserSubscription subscription = null;
        if (dto.userSubscriptionId() != null) {
            subscription = subscriptionRepository.findUserSubscriptionById(dto.userSubscriptionId())
                    .orElseThrow(() -> new UserSubscriptionNotFoundException("Подписка с id = %s не найден".formatted(dto.userSubscriptionId())));
        }

        return Rental.builder()
                .user(user)
                .scooter(scooter)
                .status(RentalStatus.IN_PROGRESS)
                .startDate(LocalDateTime.now())
                .startMileage(dto.startMileage())
                .tariff(tariff)
                .userSubscription(subscription)
                .build();
    }
}
