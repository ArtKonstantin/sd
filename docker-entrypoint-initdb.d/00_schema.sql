-- Users
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    login    TEXT        NOT NULL UNIQUE,
    password TEXT        NOT NULL,
    role     TEXT        NOT NULL,
    removed  BOOLEAN     NOT NULL DEFAULT FALSE,
    created  timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Service Level Agreement
CREATE TABLE sla
(
    id            BIGSERIAL PRIMARY KEY,
    criticality   TEXT NOT NULL,
    reaction_time TIME NOT NULL,
    lead_time     TIME NOT NULL
);

-- Tickets
CREATE TABLE tickets
(
    id           BIGSERIAL PRIMARY KEY,
    customer_id  BIGINT      NOT NULL REFERENCES users,
    appointed_id BIGINT REFERENCES users,
    created      timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted     timestamptz,
    status       TEXT        NOT NULL,
    sla_id       INT REFERENCES sla,
    description  TEXT        NOT NULL,
    file         TEXT,
    solution     TEXT,
    closed       timestamptz
);

-- Votes
CREATE TABLE votes
(
    id           BIGSERIAL PRIMARY KEY,
    tickets_id   BIGINT NOT NULL UNIQUE REFERENCES tickets,
    customer_id  BIGINT NOT NULL REFERENCES users,
    appointed_id BIGINT NOT NULL REFERENCES users,
    rating       INT    NOT NULL
);

