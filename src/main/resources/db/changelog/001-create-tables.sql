-- табличка с ролями
CREATE TABLE IF NOT EXISTS roles
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) UNIQUE NOT NULL
);

-- табличка со стандартными полями для юзера
CREATE TABLE IF NOT EXISTS users
(
    id                UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    username          VARCHAR(255) UNIQUE                                NOT NULL,
    password          VARCHAR(255)                                       NOT NULL,
    email             VARCHAR(255) UNIQUE                                NOT NULL,
    registration_date TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
    updated_on        TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL

);

-- сводная табличка для ролей пользователей
CREATE TABLE user_roles
(
    user_id UUID REFERENCES users (id) ON DELETE CASCADE,
    role_id UUID REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- табличка с точками аренды самокатов
-- coords - координаты точки аренды
CREATE TABLE IF NOT EXISTS rental_points
(
    id          UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    address     VARCHAR(1024) UNIQUE                               NOT NULL,
    latitude    DECIMAL(9, 6)                                      NOT NULL,
    longitude   DECIMAL(9, 6)                                      NOT NULL,
    description VARCHAR(1024),
    added_on    TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL,
    updated_on  TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL

);

-- табличка с самокатами
-- battery_power - текущий заряд батареи, не больше 100
-- mileage - текущий пробег самоката (будет в километрах)
-- model добавлю пока просто как название, а потом уже можно вынести в отдельную таблицу где будут все параметры модели
CREATE TABLE IF NOT EXISTS scooters
(
    id            UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    point_id      UUID          NOT NULL REFERENCES rental_points (id),
    latitude      DECIMAL(9, 6) NOT NULL,
    longitude     DECIMAL(9, 6) NOT NULL,
    model         VARCHAR(255)  NOT NULL,
    description   VARCHAR(1024),
    status        VARCHAR(128)  NOT NULL   DEFAULT 'AVAILABLE' CHECK ( status IN ('AVAILABLE', 'RENTED', 'OUT_OF_SERVICE', 'MAINTENANCE') ),
    battery_power SMALLINT      NOT NULL CHECK ( battery_power BETWEEN 0 AND 100),
    mileage       INTEGER       NOT NULL CHECK ( mileage >= 0 ),
    updated_on    TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp NOT NULL
);

-- табличка содержит тарифы, почасовые/подписку
CREATE TABLE tariffs
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(100) NOT NULL,
    type      VARCHAR(50)  NOT NULL CHECK (type IN ('HOURLY', 'SUBSCRIPTION')),
    is_active BOOLEAN          DEFAULT TRUE
);

-- стоимость конкретного тарифа
CREATE TABLE hourly_tariffs
(
    tariff_id        UUID PRIMARY KEY REFERENCES tariffs (id) ON DELETE CASCADE,
    price_per_hour   DECIMAL(10, 2) NOT NULL,
    discount_percent SMALLINT DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 100)
);

-- стоимость подписки
CREATE TABLE subscription_tariffs
(
    tariff_id        UUID PRIMARY KEY REFERENCES tariffs (id) ON DELETE CASCADE,
    price            DECIMAL(10, 2) NOT NULL,
    duration_days    SMALLINT       NOT NULL,
    discount_percent SMALLINT DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 100)
);

-- сводная табличка
CREATE TABLE user_subscriptions
(
    id         UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id    UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    tariff_id  UUID                     NOT NULL REFERENCES tariffs (id),
    start_date TIMESTAMP WITH TIME ZONE DEFAULT now(),
    end_date   TIMESTAMP WITH TIME ZONE NOT NULL,
    status     VARCHAR(50)              DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'CANCELLED')),
    created_on TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- сводная табличка, которая будет хранить в себе историю аренды
-- start_mileage end_mileage - начальный и конечный пробег самоката
CREATE TABLE IF NOT EXISTS rentals
(
    id              UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id         UUID REFERENCES users (id)                                                                     NOT NULL,
    scooter_id      UUID                                                                                           NOT NULL REFERENCES scooters (id) NOT NULL,
    status          VARCHAR(50)              DEFAULT 'IN_PROGRESS' CHECK ( status IN ('IN_PROGRESS', 'FINISHED') ) NOT NULL,
    start_date      TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp                                             NOT NULL,
    end_date        TIMESTAMP WITH TIME ZONE,
    start_mileage   INTEGER                                                                                        NOT NULL,
    end_mileage     INTEGER,
    tariff_id       UUID REFERENCES tariffs (id),
    subscription_id UUID REFERENCES user_subscriptions (id),
    cost            DECIMAL(10, 2)
);