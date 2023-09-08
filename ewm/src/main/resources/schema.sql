CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email   VARCHAR(255) UNIQUE NOT NULL,
    name    VARCHAR(251)        NOT NULL
    );

CREATE TABLE IF NOT EXISTS category
(
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS event
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    category_id        BIGINT        NOT NULL,
    initiator_id       BIGINT        NOT NULL,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP     NOT NULL,
    latitude           FLOAT         NOT NULL,
    longitude          FLOAT         NOT NULL,
    paid               BOOLEAN       NOT NULL,
    participant_limit  INTEGER       NOT NULL,
    request_moderation BOOLEAN       NOT NULL,
    title              VARCHAR(120)  NOT NULL,
    state              VARCHAR(15)   NOT NULL,
    created_on         TIMESTAMP     NOT NULL,
    published_on       TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE RESTRICT,
    FOREIGN KEY (initiator_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS request
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id     BIGINT      NOT NULL,
    requester_id BIGINT      NOT NULL,
    status       VARCHAR(15) NOT NULL,
    created      TIMESTAMP   NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS compilation
(
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned         BOOLEAN      NOT NULL,
    title          VARCHAR(120) NOT NULL
    );

CREATE TABLE IF NOT EXISTS event_compilation
(
    event_id       BIGINT NOT NULL,
    compilation_id BIGINT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE,
    FOREIGN KEY (compilation_id) REFERENCES compilation (compilation_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS comment
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id    BIGINT        NOT NULL,
    event_id   BIGINT        NOT NULL,
    text       VARCHAR(2000) NOT NULL,
    created_on TIMESTAMP     NOT NULL,
    updated_on TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES event (event_id) ON DELETE CASCADE
    )