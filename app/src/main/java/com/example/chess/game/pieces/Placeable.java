package com.example.chess.game.pieces;

import com.example.chess.game.components.Player;

public interface Placeable {
    public Piece create(Player player);
}
