package com.example.gridgameapi.service;

import com.example.gridgameapi.dto.MoveRequest;
import com.example.gridgameapi.dto.MoveResponse;
import com.example.gridgameapi.dto.StartGameResponse;
import com.example.gridgameapi.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final Map<String, Game> games = new HashMap<>();
    private final Random random = new Random();

    public StartGameResponse startGame() {
        String gameId = UUID.randomUUID().toString();
        boolean[][] blocked;
        do {
            blocked = generateRandomBlockedFields();
        } while (!existsPath(blocked));

        Game game = new Game(gameId, "A1", blocked);
        games.put(gameId, game);

        return new StartGameResponse(gameId, "A1", blocked);
    }

    public ResponseEntity<?> move(MoveRequest request) {
        Game game = games.get(request.getGameId());
        if (game == null) {
            return ResponseEntity.badRequest().body("Ungültige Game ID");
        }

        String current = game.getCurrentPosition();
        String nextPos = calcNextPosition(current, request.getDirection());
        if (nextPos == null) {
            games.remove(request.getGameId());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MoveResponse(current, "failed"));
        }

        if (isBlocked(game.getBlockedFields(), nextPos)) {
            return ResponseEntity.badRequest()
                    .body(new MoveResponse(current, "blocked"));
        }
        game.setCurrentPosition(nextPos);

        if ("E5".equals(nextPos)) {
            games.remove(request.getGameId());
            return ResponseEntity.ok(new MoveResponse(nextPos, "success"));
        }

        return ResponseEntity.ok(new MoveResponse(nextPos, "ongoing"));
    }


    private String calcNextPosition(String current, String dir) {
        char col = current.charAt(0);
        int  row = Integer.parseInt(current.substring(1));

        switch (dir) {
            case "up"    -> row--;
            case "down"  -> row++;
            case "left"  -> col--;
            case "right" -> col++;
            default -> {
                return null;
            }
        }

        if (col < 'A' || col > 'E' || row < 1 || row > 5) {
            return null;
        }
        return "" + col + row;
    }

    private boolean[][] generateRandomBlockedFields() {
        boolean[][] fields = new boolean[5][5];
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                // blocked fields %
                if (random.nextDouble() < 0.4) {
                    fields[r][c] = true;
                } 
            }
        }

        fields[0][0] = false;
        fields[4][4] = false;
        return fields;
    }

    private boolean existsPath(boolean[][] blocked) {
        boolean[][] visited = new boolean[5][5];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0,0});
        visited[0][0] = true;

        int[][] dirs = { {1,0},{-1,0},{0,1},{0,-1} };
        while(!queue.isEmpty()) {
            int[] cur = queue.poll();
            if(cur[0] == 4 && cur[1] == 4) {
                return true;
            }
            for(int[] d : dirs) {
                int nr = cur[0]+d[0], nc = cur[1]+d[1];
                if(nr>=0 && nr<5 && nc>=0 && nc<5 &&
                        !blocked[nr][nc] &&
                        !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    queue.offer(new int[]{nr,nc});
                }
            }
        }
        return false;
    }


    private boolean isBlocked(boolean[][] fields, String nextPos) {
        int row = Integer.parseInt(nextPos.substring(1)) - 1;
        int col = nextPos.charAt(0) - 'A';
        return fields[row][col];
    }
}
