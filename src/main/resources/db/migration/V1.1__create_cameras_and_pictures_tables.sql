CREATE TABLE cameras
(
    id         SERIAL PRIMARY KEY,
    nasa_id    INT       NOT NULL UNIQUE,
    name       TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE pictures
(
    id         BIGSERIAL PRIMARY KEY,
    nasa_id    BIGINT    NOT NULL UNIQUE,
    img_src    TEXT      NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    camera_id  INT       NOT NULL REFERENCES cameras (id)
);