package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

public class Bishop extends Piece {
    public Bishop(Player player) {
        super("Bishop", player, 3);
    }
}