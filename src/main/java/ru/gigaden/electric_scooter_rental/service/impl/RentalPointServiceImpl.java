package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.entity.RentalPointSortField;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RentalPointMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalPointRepository;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Основная реализация сервиса точек аренды
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalPointServiceImpl implements RentalPointService {

    private final RentalPointRepository rentalPointRepository;
    private final RentalPointMapper pointMapper;

    /**
     * Создаём новую точку аренды
     *
     * @param dto - dto новой точки аренды
     * @return - дто с созданной новой точкой аренды
     */
    @Transactional
    @Override
    public RentalPointResponseDto addRentalPoint(RentalPointCreateDto dto) {
        RentalPoint rentalPoint = pointMapper.mapCreateToRentalPoint(dto);
        RentalPoint savedRentalPoint = rentalPointRepository.saveRentalPoint(rentalPoint);
        RentalPointResponseDto response = pointMapper.mapRentalPointToResponse(savedRentalPoint);
        log.info("Создали новую точку аренды id = {}", response.id());

        return response;
    }

    /**
     * Метод возвращает точку аренды по её id
     *
     * @param id - id точки аренды
     * @return - дто с точкой аренды
     * @throws RentalPointNotFoundException если точка аренды не найдена
     */
    @Override
    public RentalPointResponseDto findRentalPointById(UUID id) {
        RentalPoint rentalPoint = findRowRentalPointOrThrow(id);
        RentalPointResponseDto response = pointMapper.mapRentalPointToResponse(rentalPoint);
        log.info("Получили точку аренды с id = {}", id);

        return response;
    }

    @Override
    public Collection<RentalPointResponseDto> findAllRentalPoints(int page, int size, RentalPointSortField sortField) {
        return List.of();
    }

    @Override
    public RentalPointResponseDto updateRentalPoint(UUID id, RentalPointUpdateDto dto) {
        return null;
    }

    @Override
    public void deleteRentalPointById(UUID id) {

    }

    /**
     * Метод получает по незамапенный объект точки аренды
     */
    private RentalPoint findRowRentalPointOrThrow(UUID id) {
        return rentalPointRepository.findRentalPointById(id)
                .orElseThrow(() -> {
                    log.error("Точка аренды с id = {} не найдена", id);
                    return new RentalPointNotFoundException("Точка аренды не найдена");
                });
    }
}
