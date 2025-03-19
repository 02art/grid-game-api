package com.example.gridgameapi.dto;

import lombok.Getter;

@Getter
public class MoveResponse {
    private String position;
    private String status;

    public MoveResponse() {
    }

    public MoveResponse(String position, String status) {
        this.position = position;
        this.status = status;
    }

}
