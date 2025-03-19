package com.example.gridgameapi.model;

import lombok.Getter;

@Getter
public class Game {
    private final String gameId;
    private String currentPosition;
    private final boolean[][] blockedFields;

    public Game(String gameId, String currentPosition, boolean[][] blockedFields) {
        this.gameId = gameId;
        this.currentPosition = currentPosition;
        this.blockedFields = blockedFields;
    }

    public void setCurrentPosition(String currentPosition) {
        this.currentPosition = currentPosition;
    }

}
