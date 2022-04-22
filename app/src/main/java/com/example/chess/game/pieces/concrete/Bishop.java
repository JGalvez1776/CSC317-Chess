package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends Piece {
    public Bishop(Player player) {
        super("Bishop", player, 3);
    }

    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        int[] directions = new int[]{-1, 1};
        for (int x : directions) {
            for (int y : directions) {
                moves.add(new Move(x, y));
            }
        }
        return moves;
    }
}