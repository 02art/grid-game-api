package com.example.gridgameapi.player;

import com.example.gridgameapi.dto.MoveRequest;
import com.example.gridgameapi.dto.MoveResponse;
import com.example.gridgameapi.dto.StartGameResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

public class GameClient {

    private static final String BASE_URL = "http://localhost:8080/api/game";
    private final RestTemplate restTemplate = new RestTemplate();
    private String gameId;
    private final char[][] localMap = new char[5][5];

    public GameClient() {
        for (int i = 0; i < 5; i++) {
            Arrays.fill(localMap[i], '?');
        }
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return false;
                }
                return super.hasError(response);
            }
        });
    }

    public void play() {
        StartGameResponse startResp = restTemplate.postForObject(
                BASE_URL + "/start", null, StartGameResponse.class
        );
        gameId = startResp.getGameId();
        String startPosition = startResp.getStartingPosition();
        System.out.println("Spiel gestartet, Game-ID: " + gameId + ", Start: " + startPosition);

        Position startPos = Position.fromGridPosition(startPosition);
        localMap[startPos.row][startPos.col] = '.';

        List<String> moveHistory = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        visited.add(startPosition);

        boolean found = dfs(startPosition, visited, moveHistory);
        if (!found) {
            System.out.println("Kein Pfad gefunden (sollte nicht passieren)!");
        } else {
            System.out.println("\nGefundener Zugpfad: " + moveHistory);
        }
        printFinalMap(moveHistory);
    }

    private boolean dfs(String current, Set<String> visited, List<String> moveHistory) {
        if ("E5".equals(current)) {
            Position goal = Position.fromGridPosition("E5");
            localMap[goal.row][goal.col] = '.';
            return true;
        }

        List<String> directions = new ArrayList<>(Arrays.asList("up", "right", "down", "left"));
        directions.sort(Comparator.comparingInt(dir -> {
            String next = calcNextPosition(current, dir);
            if (next == null) return Integer.MAX_VALUE;
            Position pos = Position.fromGridPosition(next);
            return calcDistance(pos, Position.fromGridPosition("E5"));
        }));

        for (String dir : directions) {
            String next = calcNextPosition(current, dir);
            if (next == null || visited.contains(next)) {
                continue;
            }

            MoveRequest req = new MoveRequest();
            req.setGameId(gameId);
            req.setDirection(dir);
            MoveResponse resp = restTemplate.postForObject(
                    BASE_URL + "/move", req, MoveResponse.class
            );
            if ("failed".equals(resp.getStatus())) {
                continue;
            }
            if ("blocked".equals(resp.getStatus())) {
                Position blockedPos = Position.fromGridPosition(next);
                localMap[blockedPos.row][blockedPos.col] = 'X';
                System.out.printf("Bewege '%s' -> Blockiert bei: %s%n", dir, next);
                continue;
            }

            System.out.printf("Bewege '%s' -> neue Position: %s, Status: %s%n",
                    dir, resp.getPosition(), resp.getStatus());
            Position nextPos = Position.fromGridPosition(resp.getPosition());
            localMap[nextPos.row][nextPos.col] = '.';
            moveHistory.add(dir);
            visited.add(next);

            if ("success".equals(resp.getStatus())) {
                return true;
            }

            if (dfs(resp.getPosition(), visited, moveHistory)) {
                return true;
            }

            String reverse = getReverseDirection(dir);
            MoveRequest backReq = new MoveRequest();
            backReq.setGameId(gameId);
            backReq.setDirection(reverse);
            MoveResponse backResp = restTemplate.postForObject(
                    BASE_URL + "/move", backReq, MoveResponse.class
            );
            System.out.printf("Backtracking: Bewege '%s' -> zurück zu: %s, Status: %s%n",
                    reverse, backResp.getPosition(), backResp.getStatus());
            moveHistory.add(reverse);
        }
        return false;
    }

    private int calcDistance(Position a, Position b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    private String calcNextPosition(String current, String dir) {
        char col = current.charAt(0);
        int row = Integer.parseInt(current.substring(1));
        switch (dir) {
            case "up" -> row--;
            case "down" -> row++;
            case "left" -> col--;
            case "right" -> col++;
            default -> { return null; }
        }
        if (col < 'A' || col > 'E' || row < 1 || row > 5) {
            return null;
        }
        return "" + col + row;
    }

    private String getReverseDirection(String dir) {
        return switch (dir) {
            case "up" -> "down";
            case "down" -> "up";
            case "left" -> "right";
            case "right" -> "left";
            default -> "";
        };
    }

    private void printFinalMap(List<String> moveHistory) {
        char[][] finalMap = new char[5][5];
        for (int i = 0; i < 5; i++) {
            Arrays.fill(finalMap[i], '?');
        }

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (localMap[i][j] == 'X') {
                    finalMap[i][j] = 'X';
                }
            }
        }

        Position pos = Position.fromGridPosition("A1");
        finalMap[pos.row][pos.col] = '*';
        for (String move : moveHistory) {
            pos = movePosition(pos, move);
            if (pos != null) {
                finalMap[pos.row][pos.col] = '*';
            }
        }

        finalMap[0][0] = 'S';
        finalMap[4][4] = 'E';

        System.out.println("\nFinale erkannte Karte:");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print(finalMap[i][j] + " ");
            }
            System.out.println();
        }
    }

    private Position movePosition(Position pos, String move) {
        int row = pos.row, col = pos.col;
        switch (move) {
            case "up" -> row--;
            case "down" -> row++;
            case "left" -> col--;
            case "right" -> col++;
        }
        if (row < 0 || row >= 5 || col < 0 || col >= 5) {
            return null;
        }
        return new Position(row, col);
    }

    public static void main(String[] args) {
        new GameClient().play();
    }
}


