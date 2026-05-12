INSERT INTO users(id, username, password, email)
VALUES (gen_random_uuid(), 'admin', '$2a$10$oMB9J5P0mQMM.edL2HT.xe.agRuc81r/3MXRAD4UiDKv1NjFROtdC',
        'admin@ya.ru'); --пароль admin

INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM users WHERE username = 'admin'),
        (SELECT id FROM roles WHERE name = 'ADMIN'));