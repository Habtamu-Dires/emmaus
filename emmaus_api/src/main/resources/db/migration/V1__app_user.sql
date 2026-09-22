--- app_user table
CREATE TABLE app_user (
       id BIGSERIAL PRIMARY KEY,
       public_id UUID NOT NULL UNIQUE,

       email VARCHAR(255) UNIQUE ,
       password_hash VARCHAR(255) NOT NULL,
       is_temp_password BOOLEAN NOT NULL DEFAULT FALSE,

       roles VARCHAR(20)[] NOT NULL,
       status VARCHAR(20) NOT NULL,

       first_name VARCHAR(100),
       last_name VARCHAR(100),
       profile_pic TEXT,

       is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,

       created_at TIMESTAMPTZ DEFAULT now(),
       updated_at TIMESTAMPTZ DEFAULT now(),
       remark TEXT
);

CREATE INDEX ix_user_public_id
    ON app_user(public_id);

-- refresh_token
CREATE TABLE refresh_token(
      id BIGSERIAL PRIMARY KEY,
      user_id BIGINT NOT NULL REFERENCES app_user(id),

      token_hash TEXT NOT NULL,
      issued_at TIMESTAMPTZ NOT NULL,
      expires_at TIMESTAMPTZ NOT NULL,
      revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX ux_refresh_token_hash
    ON refresh_token(token_hash);

CREATE INDEX ix_refresh_token_user
    ON refresh_token(user_id);

--- password_reset_token
CREATE TABLE reset_token (
      id BIGSERIAL PRIMARY KEY,
      user_id BIGINT NOT NULL REFERENCES app_user(id),
      token_hash TEXT NOT NULL,
      used BOOLEAN NOT NULL DEFAULT FALSE,
      resend_count INT DEFAULT 0,
      last_sent_at TIMESTAMPTZ,
      expires_at TIMESTAMPTZ NOT NULL,
      attempts INT NOT NULL
);

CREATE INDEX ix_otp_user_id
    ON reset_token(user_id);

--- otp_rate_limit
CREATE TABLE reset_token_rate_limit (
        id BIGSERIAL PRIMARY KEY ,
        user_id BIGINT NOT NULL UNIQUE REFERENCES app_user(id),

        window_start DATE NOT NULL,
        sent_count INT NOT NULL,

        violation_count INT NOT NULL DEFAULT 0,

        blocked_until TIMESTAMPTZ DEFAULT NULL
);

--- login_retry_limit
CREATE TABLE login_retry_limit (
       id BIGSERIAL PRIMARY KEY ,
       user_id BIGINT NOT NULL UNIQUE REFERENCES app_user(id),

       window_start DATE NOT NULL,
       sent_count INT NOT NULL,

       violation_count INT NOT NULL DEFAULT 0,

       blocked_until TIMESTAMPTZ DEFAULT NULL
);
