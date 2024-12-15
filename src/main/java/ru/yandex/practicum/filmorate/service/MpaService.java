package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<MpaRating> getAllMpa() {
        log.info("Получение всех MPA рейтингов");
        return mpaStorage.getAllMpa();
    }

    public MpaRating getMpaById(Long id) {
        log.info("Получение MPA рейтинга с id: {}", id);
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MPA рейтинг с id " + id + " не найден"));
    }
}
