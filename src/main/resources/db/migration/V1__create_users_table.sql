CREATE TYPE system_role_type AS ENUM ('customer', 'delivery_agent', 'system_admin',
    'restaurant_user');

CREATE TABLE users
(
    id            SERIAL PRIMARY KEY,
    email         TEXT             NOT NULL UNIQUE,
    phone         TEXT             NOT NULL UNIQUE,
    name          TEXT             NOT NULL,
    password_hash TEXT             NOT NULL,
    system_role   system_role_type NOT NULL,
    created_at    TIMESTAMP        NOT NULL,
    updated_at    TIMESTAMP        NOT NULL,
    deleted_at    TIMESTAMP
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_system_role ON users (system_role);