DROP TABLE IF EXISTS types CASCADE;
DROP TABLE IF EXISTS bikes CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS dealerships CASCADE;
DROP TABLE IF EXISTS dealerships_bikes CASCADE;


CREATE TABLE types
(
    id   SERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE brands
(
    id    SERIAL PRIMARY KEY,
    brand varchar(255) NOT NULL UNIQUE
);

CREATE TABLE bikes
(
    id       SERIAL PRIMARY KEY,
    type_id  BIGINT REFERENCES types (id),
    brand_id BIGINT REFERENCES brands (id),
    model    VARCHAR(255) NOT NULL UNIQUE,
    cost     BIGINT       NOT NULL
);

CREATE TABLE dealerships
(
    id        SERIAL PRIMARY KEY,
    city      VARCHAR(255) NOT NULL,
    street    varchar(255) NOT NULL,
    house_num BIGINT       NOT NULL
);

CREATE TABLE dealerships_bikes
(
    id SERIAL PRIMARY KEY ,
    dealership_id BIGINT REFERENCES dealerships (id),
    bike_id       BIGINT REFERENCES bikes (id),
    UNIQUE (dealership_id, bike_id)
);

INSERT INTO types(type)
VALUES ('chopper'),
       ('cross'),
       ('sport'),
       ('tourism'),
       ('naked'),
       ('enduro');

INSERT INTO brands(brand)
VALUES ('BMW'),
       ('KTM'),
       ('Honda'),
       ('Suzuki'),
       ('Voge'),
       ('Ducati'),
       ('Harley-Davidson'),
       ('Minsk');

INSERT INTO bikes(type_id, brand_id, model, cost)
VALUES (1, 3, 'Shadow 150', 299900),
       (1, 3, 'CMX 1100 Rebel', 1799900),
       (5, 3, 'CB 300F', 639900),
       (5, 8, 'SCR 250', 212960),
       (6, 8, 'X 250', 155580),
       (3, 1, 'M 1000 RR', 4300000),
       (5, 1, 'S 1000 R', 2600000),
       (4, 1, 'R 1250 RT', 3850000),
       (1, 1, 'R 18', 3150000),
       (2, 2, '250 SX F', 1319900),
       (2, 2, '125 SX', 1259900),
       (2, 2, '300 SX', 1419900),
       (1, 7, 'Breakout', 4200000),
       (1, 7, 'Fat Boy', 3900000),
       (1, 7, 'Fat Bob', 3200000),
       (3, 6, 'Panigale V4', 5350000),
       (5, 5, '350 AC', 497000),
       (6, 5, 'DS 525 X', 692000),
       (4, 4, 'V-Strom DL 1050', 1550000);

INSERT INTO dealerships(city, street, house_num)
VALUES ('Самара', 'Стара-Загора', 31),
       ('Москва', 'Зацепа', 21),
       ('Санкт-Петербург', 'Симонова', 1),
       ('Краснодар', 'Лаврова', 22);

INSERT INTO dealerships_bikes(dealership_id, bike_id)
VALUES (1, 1),
       (1, 2),
       (1, 10),
       (1, 4),
       (2, 3),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 14),
       (2, 15),
       (2, 18),
       (2, 8),
       (2, 9),
       (3, 12),
       (3, 13),
       (3, 16),
       (3, 17),
       (3, 3),
       (3, 5),
       (4, 12),
       (4, 1),
       (4, 10),
       (4, 5);