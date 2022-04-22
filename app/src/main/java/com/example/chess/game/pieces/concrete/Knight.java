package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Player player) {
        super("Knight", player, 3);
    }

    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        int[] distances = new int[]{2, -2, -1, 1};
        for (int x : distances) {
            for (int y : distances) {
                if (Math.abs(x) != Math.abs(y)) {
                    moves.add(new Move(x, y, false, true));
                }
            }
        }
        return moves;
    }
}