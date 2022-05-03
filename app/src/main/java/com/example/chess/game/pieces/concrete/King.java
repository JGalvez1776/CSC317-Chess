/**
 * Contains the King piece for a chess game
 */
package com.example.chess.game.pieces.concrete;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    /**
     * Default constructor for a piece
     * @param player Player who owns this piece
     */
    public King(Player player) {
        super("King", player, 0);
    }

    /**
     * Returns all moves a piece can make
     * @return List of all moves the piece can make
     */
    @Override
    public List<Move> getPotentialMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        moves.add(new Move(1, 0, false, true));
        moves.add(new Move(0, 1, false, true));
        moves.add(new Move(-1, 0, false, true));
        moves.add(new Move(0, -1, false, true));
        int[] directions = new int[]{-1, 1};
        for (int x : directions) {
            for (int y : directions) {
                moves.add(new Move(x, y, false, true));
            }
        }
        return moves;
    }
}