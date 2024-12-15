package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public ResponseEntity<List<MpaRating>> getAllMpa() {
        log.info("Запрос на получение всех MPA рейтингов");
        List<MpaRating> mpaRatings = mpaService.getAllMpa();
        return ResponseEntity.ok(mpaRatings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MpaRating> getMpaById(@PathVariable Long id) {
        log.info("Запрос на получение MPA рейтинга с id: {}", id);
        MpaRating mpaRating = mpaService.getMpaById(id);
        return ResponseEntity.ok(mpaRating);
    }
}
