INSERT INTO tariffs(id, name, type, is_active)
VALUES (gen_random_uuid(), 'Default Hourly Tariff', 'HOURLY', true);

INSERT INTO hourly_tariffs (tariff_id, price_per_hour, discount_percent)
VALUES (
           (SELECT id FROM tariffs WHERE name = 'Default Hourly Tariff'),
           250,
           0
       );