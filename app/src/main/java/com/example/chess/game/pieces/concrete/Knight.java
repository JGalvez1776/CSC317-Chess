package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

public class Knight extends Piece {
    public Knight(Player player) {
        super("Knight", player, 3);
    }
}