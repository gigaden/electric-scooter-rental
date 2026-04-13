package ru.gigaden.electric_scooter_rental.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Repository;
import ru.gigaden.electric_scooter_rental.exception.DatabaseException;

import java.io.Serializable;
import java.util.List;

/**
 * Базовый репозиторий для работы с сущностями
 */
@Repository
@Slf4j
public abstract class BaseRepositoryHiber<T, PK extends Serializable> {

    private final Class<T> type;
    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseRepositoryHiber(Class<T> type) {
        this.type = type;
    }

    protected T save(T entity) {
        try {
            entityManager.persist(entity);
            return entity;
        } catch (HibernateException e) {
            log.error("Ошибка базы данных при сохранении {}: {}", type.getSimpleName(), entity, e);
            throw new DatabaseException("Ошибка сохранения сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected T update(T entity) {
        try {
            return entityManager.merge(entity);
        } catch (HibernateException e) {
            log.error("Ошибка БД при обновлении {}: {}", type.getSimpleName(), entity, e);
            throw new DatabaseException("Ошибка обновления сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected void delete(T entity) {
        try {
            if (entity != null) {
                entityManager.remove(entity);
            }
        } catch (HibernateException e) {
            log.error("Ошибка БД при удалении {} : {}", type.getSimpleName(), entity, e);
            throw new DatabaseException("Ошибка удаления сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected List<T> findAll(int page, int size, String sortBy) {
        try {
            String jpql = "SELECT e FROM " + type.getSimpleName() + " e ORDER BY e." + sortBy + " DESC";
            TypedQuery<T> query = entityManager.createQuery(jpql, type)
                    .setFirstResult(page * size)
                    .setMaxResults(size);

            return query.getResultList();
        } catch (HibernateException e) {
            log.error("Ошибка получения списка {} с пагинацией", type.getSimpleName(), e);
            throw new DatabaseException("Ошибка получения списка " + type.getSimpleName() + " " + e);
        }
    }

    protected T find(PK id) {
        try {
            return entityManager.find(type, id);
        } catch (HibernateException e) {
            log.error("Ошибка поиска сущности {} с id: {}", type.getSimpleName(), id, e);
            throw new DatabaseException("Ошибка поиска сущности " + type.getSimpleName() + " " + e);
        }
    }

    protected boolean exists(PK id) {
        return find(id) != null;
    }
}