package com.example.gridgameapi.player;

import java.util.Objects;

class Position {
    int row;
    int col;

    Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    static com.example.gridgameapi.player.Position fromGridPosition(String gridPos) {
        char colChar = gridPos.charAt(0);
        int row = Integer.parseInt(gridPos.substring(1)) - 1;
        int col = colChar - 'A';
        return new com.example.gridgameapi.player.Position(row, col);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position pos)) return false;
        return row == pos.row && col == pos.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}