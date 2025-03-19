package com.example.gridgameapi.dto;

import lombok.Getter;

@Getter
public class StartGameResponse {
    private String gameId;
    private String startingPosition;
    private boolean[][] blockedFields;

    public StartGameResponse() {
    }

    public StartGameResponse(String gameId, String startingPosition, boolean[][] blockedFields) {
        this.gameId = gameId;
        this.startingPosition = startingPosition;
        this.blockedFields = blockedFields;
    }

}
