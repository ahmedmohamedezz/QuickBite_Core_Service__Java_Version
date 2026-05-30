CREATE TABLE customer_addresses
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    label            TEXT           NOT NULL,
    country          TEXT           NOT NULL,
    city             TEXT           NOT NULL,
    street           TEXT           NOT NULL,
    building         TEXT,
    apartment_number TEXT,
    type             TEXT           NOT NULL CHECK ( type in ('office', 'home', 'public_place') ),
    lat              DECIMAL(10, 7) NOT NULL,
    lng              DECIMAL(10, 7) NOT NULL,
    is_default       BOOLEAN        NOT NULL,

    CONSTRAINT fk_customer_addresses_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE INDEX idx_customer_addresses_user_id ON customer_addresses (user_id);