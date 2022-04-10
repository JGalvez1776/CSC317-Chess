package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

public class King extends Piece {
    public King(Player player) {
        super("King", player, 0);
    }
}