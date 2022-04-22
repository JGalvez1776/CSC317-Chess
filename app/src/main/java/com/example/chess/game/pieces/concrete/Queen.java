package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Player player) {
        super("Queen", player, 8);
    }

    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        moves.add(new Move(1, 0));
        moves.add(new Move(0, 1));
        moves.add(new Move(-1, 0));
        moves.add(new Move(0, -1));
        int[] directions = new int[]{-1, 1};
        for (int x : directions) {
            for (int y : directions) {
                moves.add(new Move(x, y));
            }
        }
        return moves;
    }
}