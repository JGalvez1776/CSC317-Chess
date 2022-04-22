package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(Player player) {
        super("Rook", player, 5);
    }


    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        moves.add(new Move(1, 0));
        moves.add(new Move(0, 1));
        moves.add(new Move(-1, 0));
        moves.add(new Move(0, -1));
        return moves;
    }
}