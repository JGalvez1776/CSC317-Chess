/**
 * Contains the Rook piece for a chess game
 */
package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    /**
     * Default constructor for a piece
     * @param player Player who owns this piece
     */
    public Rook(Player player) {
        super("Rook", player, 5);
    }

    /**
     * Returns all moves a piece can make
     * @return List of all moves the piece can make
     */
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