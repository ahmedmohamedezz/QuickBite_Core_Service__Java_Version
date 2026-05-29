CREATE TYPE location_type AS ENUM ('office', 'home', 'public_place');

CREATE TABLE customer_addresses
(
    id               SERIAL PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    label            TEXT           NOT NULL,
    country          TEXT           NOT NULL,
    city             TEXT           NOT NULL,
    street           TEXT           NOT NULL,
    building         TEXT,
    apartment_number TEXT,
    type             location_type  NOT NULL,
    lat              DECIMAL(10, 7) NOT NULL,
    lng              DECIMAL(10, 7) NOT NULL,
    is_default       BOOLEAN        NOT NULL,

    CONSTRAINT fk_customer_addresses_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE INDEX idx_customer_addresses_user_id ON customer_addresses (user_id);