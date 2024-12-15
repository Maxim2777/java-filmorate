-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(255),
    login VARCHAR(255),
    name VARCHAR(255),
    birthday DATE,
    PRIMARY KEY (user_id)
);

-- Создание таблицы дружбы
CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT,
    friend_id BIGINT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);


-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255),
    description TEXT,
    release_date DATE,
    duration INT,
    PRIMARY KEY (film_id)
);

-- Создание таблицы рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_rating_id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

-- Связь фильмов с рейтингами MPA
CREATE TABLE IF NOT EXISTS film_mpa_rating (
    film_id BIGINT,
    mpa_rating_id BIGINT,
    PRIMARY KEY (film_id, mpa_rating_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (mpa_rating_id) REFERENCES mpa_rating(mpa_rating_id) ON DELETE CASCADE
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genre (
    genre_id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

-- Связь фильмов с жанрами
CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
);

-- Таблица для лайков фильмов
CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

