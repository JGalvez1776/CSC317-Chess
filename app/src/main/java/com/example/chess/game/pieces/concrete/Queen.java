package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

public class Queen extends Piece {
    public Queen(Player player) {
        super("Queen", player, 8);
    }
}