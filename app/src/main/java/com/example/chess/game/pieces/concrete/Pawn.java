/*
 * Contains the Pawn piece for a chess game
 */
package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Board;
import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    /**
     * Default constructor for a piece
     * @param player Player who owns this piece
     */
    public Pawn(Player player) {
        super("Pawn", player, 1);
    }

    /**
     * Returns all moves a piece can make
     * @return List of all moves the piece can make
     */
    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        int y = 1;
        if (owner.toString().equals(Board.BLACK))
            y = -1;

        moves.add(new Move(0, y, false, false));
        if (!hasMoved())
            moves.add(new Move(0, 2 * y, false, false));

        return moves;
    }

    

}