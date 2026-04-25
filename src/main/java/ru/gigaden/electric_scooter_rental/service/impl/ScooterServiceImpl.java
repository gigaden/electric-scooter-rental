package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterCreateDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterResponseDto;
import ru.gigaden.electric_scooter_rental.dto.scooter.ScooterUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.entity.Scooter;
import ru.gigaden.electric_scooter_rental.entity.ScooterSortField;
import ru.gigaden.electric_scooter_rental.entity.ScooterStatus;
import ru.gigaden.electric_scooter_rental.exception.ScooterNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.ScooterMapper;
import ru.gigaden.electric_scooter_rental.repository.ScooterRepository;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;
import ru.gigaden.electric_scooter_rental.service.ScooterService;

import java.util.Collection;
import java.util.UUID;

/**
 * Основная реализация сервиса самокатов
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScooterServiceImpl implements ScooterService {

    private final ScooterRepository scooterRepository;
    private final ScooterMapper scooterMapper;
    private final RentalPointService pointService;


    /**
     * Создание нового самоката
     *
     * @param dto - дто для нового самоката
     * @return - дто с созданным самокатом
     */
    @Transactional
    @Override
    public ScooterResponseDto addScooter(ScooterCreateDto dto) {

        Scooter newScooter = prepareNewScooter(dto);
        Scooter savedScooter = scooterRepository.saveScooter(newScooter);

        ScooterResponseDto response = scooterMapper.mapScooterToResponseDto(savedScooter);
        log.info("Добавлен новый самокат {}", response);

        return response;
    }

    /**
     * Метод возвращает самокат по его id
     *
     * @param id - id самоката для поиска
     * @return - дто с самокатом
     * @throws ScooterNotFoundException если самокат не найден
     */
    @Override
    public ScooterResponseDto findScooterById(UUID id) {
        Scooter scooter = findRowScooterOrThrow(id);
        ScooterResponseDto response = scooterMapper.mapScooterToResponseDto(scooter);
        log.info("Получили самокат с id = {}", id);

        return response;
    }

    /**
     * Получаем все самокаты с пагинацией и сортировкой
     *
     * @param page - номер страницы
     * @param size - размер
     * @param sort - поле, по которому сортируем
     */
    @Override
    public Collection<ScooterResponseDto> findAll(int page, int size, ScooterSortField sort) {
        Collection<ScooterResponseDto> response = scooterRepository.findAllScooters(page, size, sort.getField()).stream()
                .map(scooterMapper::mapScooterToResponseDto).toList();
        log.info("Получили список самокатов в количестве {}", response.size());

        return response;
    }

    /**
     * Метод обновляет самокат по его id
     *
     * @param scooterId - id самоката
     * @param dto       - данные для обновления
     * @throws ScooterNotFoundException - если самокат не найден
     */
    @Transactional
    @Override
    public ScooterResponseDto updateScooterById(UUID scooterId, ScooterUpdateDto dto) {
        Scooter existingScooter = findRowScooterOrThrow(scooterId);
        updateScooterFields(existingScooter, dto);
        Scooter updatedScooter = scooterRepository.updateScooter(existingScooter);
        ScooterResponseDto response = scooterMapper.mapScooterToResponseDto(updatedScooter);
        log.info("Самокат с id {} обновлён", scooterId);

        return response;
    }

    /**
     * Метод удаляет самокат по его id
     *
     * @param id - id самоката
     * @throws ScooterNotFoundException - если самокат не найден
     */
    @Transactional
    @Override
    public void deleteScooterById(UUID id) {
        Scooter scooter = findRowScooterOrThrow(id);
        scooterRepository.deleteScooterByEntity(scooter);
        log.info("Самокат с id {} удалён", id);
    }

    /**
     * Обновляем сущность самоката данными из дто
     */
    private void updateScooterFields(Scooter existingScooter, ScooterUpdateDto dto) {
        if (dto.rentalPointId() != null) {
            RentalPoint rentalPoint = pointService.findRowRentalPointOrThrow(dto.rentalPointId());
            existingScooter.setRentalPoint(rentalPoint);
        }
        if (dto.latitude() != null) {
            existingScooter.setLatitude(dto.latitude());
        }
        if (dto.longitude() != null) {
            existingScooter.setLongitude(dto.longitude());
        }
        if (dto.model() != null) {
            existingScooter.setModel(dto.model());
        }
        if (dto.description() != null) {
            existingScooter.setDescription(dto.description());
        }
        if (dto.status() != null) {
            existingScooter.setStatus(dto.status());
        }
        if (dto.batteryPower() != null) {
            existingScooter.setBatteryPower(dto.batteryPower());
        }
        if (dto.mileage() != null) {
            existingScooter.setMileage(dto.mileage());
        }

    }

    /**
     * Метод получает по незамапенный объект самоката
     */
    private Scooter findRowScooterOrThrow(UUID id) {
        return scooterRepository.findScooterById(id)
                .orElseThrow(() -> {
                    log.error("Самокат с id = {} не найден", id);
                    return new ScooterNotFoundException("Самокат не найден");
                });
    }

    /**
     * Метод подготавливает новый самокат для сохранения
     *
     * @param dto - дто самоката
     * @return - готовый объект для сохранения
     */
    private Scooter prepareNewScooter(ScooterCreateDto dto) {
        RentalPoint rentalPoint = pointService.findRowRentalPointOrThrow(dto.rentalPointId());
        return Scooter.builder()
                .rentalPoint(rentalPoint)
                .latitude(rentalPoint.getLatitude())
                .longitude(rentalPoint.getLongitude())
                .model(dto.model())
                .description(dto.description())
                .status(ScooterStatus.AVAILABLE)
                .batteryPower(dto.batteryPower())
                .mileage(dto.mileage())
                .build();
    }
}