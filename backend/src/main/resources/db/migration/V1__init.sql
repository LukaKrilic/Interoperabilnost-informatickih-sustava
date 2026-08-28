CREATE TABLE tag (
    id BIGSERIAL PRIMARY KEY,
    gid VARCHAR(30) UNIQUE,
    name VARCHAR(200) NOT NULL,
    color VARCHAR(30),
    notes TEXT,
    workspace_gid VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    app_user_id BIGINT NOT NULL REFERENCES app_user(id),
    token VARCHAR(200) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
