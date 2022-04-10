package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

public class Rook extends Piece {
    public Rook(Player player) {
        super("Rook", player, 5);
    }
}