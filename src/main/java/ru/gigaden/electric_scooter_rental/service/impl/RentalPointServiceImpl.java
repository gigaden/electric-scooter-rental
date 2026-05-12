package ru.gigaden.electric_scooter_rental.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointCreateDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointResponseDto;
import ru.gigaden.electric_scooter_rental.dto.point.RentalPointUpdateDto;
import ru.gigaden.electric_scooter_rental.entity.RentalPoint;
import ru.gigaden.electric_scooter_rental.exception.RentalPointNotFoundException;
import ru.gigaden.electric_scooter_rental.mapper.RentalPointMapper;
import ru.gigaden.electric_scooter_rental.repository.RentalPointRepository;
import ru.gigaden.electric_scooter_rental.service.RentalPointService;

import java.util.Collection;
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
    log.debug("Получили точку аренды с id = {}", id);

    return response;
  }

  /**
   * Получаем все точки аренды с пагинацией и сортировкой
   *
   * @param page - номер страницы
   * @param size - размер
   */
  @Override
  public Collection<RentalPointResponseDto> findAllRentalPoints(int page, int size) {

    Collection<RentalPointResponseDto> points = rentalPointRepository
        .findAllRentalPoints(page, size).stream()
        .map(pointMapper::mapRentalPointToResponse)
        .toList();
    log.debug("Получили список точек аренды page = {}, size = {}", page, size);

    return points;
  }

  /**
   * Метод обновляет точку аренды по её id
   *
   * @param id  - id точки аренды
   * @param dto - данные для обновления
   * @throws RentalPointNotFoundException - если точка аренды не найдена
   */
  @Override
  @Transactional
  public RentalPointResponseDto updateRentalPoint(UUID id, RentalPointUpdateDto dto) {

    RentalPoint rentalPoint = findRowRentalPointOrThrow(id);
    updateRentalPointFields(rentalPoint, dto);
    RentalPoint updated = rentalPointRepository.updateRentalPoint(rentalPoint);
    RentalPointResponseDto response = pointMapper.mapRentalPointToResponse(updated);
    log.info("Точка аренды с id = {} обновлена", id);

    return response;
  }

  @Override
  public Collection<RentalPointResponseDto> findRentalPointsByRadius(double latitude, double longitude, double radiusKm, int page, int size) {

    Collection<RentalPointResponseDto> points = rentalPointRepository
        .findRentalPointsByRadius(latitude, longitude, radiusKm, page, size).stream()
        .map(pointMapper::mapRentalPointToResponse)
        .toList();
    log.debug("Найдено {} точек аренды в радиусе {} км", points.size(), radiusKm);

    return points;
  }

  /**
   * Обновляем сущность точки аренды данными из дто
   */
  private void updateRentalPointFields(RentalPoint rentalPoint, RentalPointUpdateDto dto) {

    if (dto.address() != null) {
      rentalPoint.setAddress(dto.address());
    }
    if (dto.latitude() != null) {
      rentalPoint.setLatitude(dto.latitude());
    }
    if (dto.longitude() != null) {
      rentalPoint.setLongitude(dto.longitude());
    }
    if (dto.description() != null) {
      rentalPoint.setDescription(dto.description());
    }
  }

  /**
   * Метод удаляет точку аренды id
   *
   * @param id - id точки аренды
   * @throws RentalPointNotFoundException - если точка аренды не найдена
   */
  @Transactional
  @Override
  public void deleteRentalPointById(UUID id) {

    RentalPoint rentalPoint = findRowRentalPointOrThrow(id);
    rentalPointRepository.deleteRentalPoint(rentalPoint);

    log.info("Точка аренды с id = {} удалена", id);
  }

  /**
   * Метод получает по незамапенный объект точки аренды
   */
  @Override
  public RentalPoint findRowRentalPointOrThrow(UUID id) {

    return rentalPointRepository.findRentalPointById(id)
        .orElseThrow(() -> new RentalPointNotFoundException("Точка аренды не найдена"));
  }
}
