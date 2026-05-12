-- Очистка таблиц перед тестами
DELETE
FROM rentals;
DELETE
FROM scooters;
DELETE
FROM rental_points;
DELETE
FROM user_subscriptions;
DELETE
FROM hourly_tariffs;
DELETE
FROM subscription_tariffs;
DELETE
FROM tariffs;
DELETE
FROM user_roles;
DELETE
FROM users;
DELETE
FROM roles;

-- Роли
INSERT INTO roles (id, name)
VALUES ('11111111-1111-1111-1111-111111111111', 'USER'),
       ('22222222-2222-2222-2222-222222222222', 'ADMIN');

-- Пользователи
INSERT INTO users (id, username, password, email, registration_date, updated_on)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'testuser', '$2a$10$9rQZ8Z8Z8Z8Z8Z8Z8Z8Z8O', 'test@example.com', NOW(),
        NOW()),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'admin', '$2a$10$9rQZ8Z8Z8Z8Z8Z8Z8Z8Z8O', 'admin@example.com', NOW(),
        NOW());

-- Связь пользователь-роли
INSERT INTO user_roles (user_id, role_id)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111'),
       ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222');

-- Точки аренды
INSERT INTO rental_points (id, address, latitude, longitude, description, added_on, updated_on)
VALUES ('cccccccc-cccc-cccc-cccc-cccccccccccc',
        'г. Москва, ул. Тверская, д. 1',
        55.7558, 37.6173,
        'Центральная точка проката',
        NOW(), NOW()),
       ('dddddddd-dddd-dddd-dddd-dddddddddddd',
        'г. Москва, ул. Арбат, д. 10',
        55.7512, 37.5956,
        'Точка у метро',
        NOW(), NOW());

-- Самокаты
INSERT INTO scooters (id, point_id, latitude, longitude, model, description, status, battery_power, mileage, updated_on)
VALUES ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',
        'cccccccc-cccc-cccc-cccc-cccccccccccc',
        55.7558, 37.6173,
        'Nimbus2000',
        'Мощный городской самокат',
        'AVAILABLE',
        100,
        150,
        NOW()),
       ('ffffffff-ffff-ffff-ffff-ffffffffffff',
        'dddddddd-dddd-dddd-dddd-dddddddddddd',
        55.7512, 37.5956,
        'SwiftX',
        'Лёгкий самокат для коротких поездок',
        'AVAILABLE',
        85,
        320,
        NOW());

-- Тарифы
INSERT INTO tariffs (id, name, type, is_active)
VALUES ('10000000-0000-0000-0000-000000000001', 'Default Hourly Tariff', 'HOURLY', true),
       ('10000000-0000-0000-0000-000000000002', 'Premium Monthly', 'SUBSCRIPTION', true);

-- Почасовые тарифы
INSERT INTO hourly_tariffs (tariff_id, price_per_hour, discount_percent)
VALUES ('10000000-0000-0000-0000-000000000001', 250.00, 0);

-- Подписочные тарифы
INSERT INTO subscription_tariffs (tariff_id, price, duration_days, discount_percent)
VALUES ('10000000-0000-0000-0000-000000000002', 2999.00, 30, 10);