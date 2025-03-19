package com.example.gridgameapi.controller;

import com.example.gridgameapi.dto.MoveRequest;
import com.example.gridgameapi.dto.StartGameResponse;
import com.example.gridgameapi.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start")
    public StartGameResponse startGame() {
        return gameService.startGame();
    }

    @PostMapping("/move")
    public ResponseEntity<?> move(@RequestBody MoveRequest request) {
        return gameService.move(request);
    }
}
