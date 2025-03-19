package com.example.gridgameapi.dto;

import lombok.Getter;

@Getter
public class MoveRequest {
    private String gameId;
    private String direction;

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
