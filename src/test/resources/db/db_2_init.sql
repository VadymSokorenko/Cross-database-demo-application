--create users table
CREATE TABLE employees
(
    ldap_login VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    surname    VARCHAR(255) NOT NULL
);

--populate users
INSERT INTO employees
VALUES ('user', 'Test', 'Testenko');