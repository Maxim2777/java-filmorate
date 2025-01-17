package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {
    List<MpaRating> getAllMpa();

    Optional<MpaRating> getMpaById(Long id);
}