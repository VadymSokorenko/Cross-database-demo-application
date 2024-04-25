--create users table
CREATE TABLE users
(
    user_id    BIGSERIAL PRIMARY KEY,
    login      VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL
);

--populate users
INSERT INTO users
VALUES (DEFAULT, 'admin', 'Vadym', 'Sokorenko');