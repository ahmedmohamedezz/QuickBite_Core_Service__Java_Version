CREATE TABLE password_resets
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL,
    otp_hash    TEXT      NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP NOT NULL,
    consumed_at TIMESTAMP,

    CONSTRAINT fk_password_resets_user_id FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE INDEX idx_password_resets_user_id ON password_resets (user_id);
