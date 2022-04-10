package com.example.chess.game;

import com.example.chess.game.components.Board;
import com.example.chess.game.pieces.Piece;

public class GameController {
    private final Board game;
    private Piece selected = null;

    // TODO: Fill in
    // a public static final int NOTHING_SELECTED = 0;
    // a public static final int PIECE_SELECTED = 1;
    // a public static final int PIECE_MOVED = 2;

    public GameController(Board game) {
        this.game = game;
    }



    public String getPieceName(int x, int y) {
        Piece piece = game.getPiece(x, y);
        return piece != null ? piece.getPlayer() + piece.getName() : null;
        
    }

    public int select(int x, int y) {
        /*
            TODO: 
            Options:
                Nothing/Piece unselected - 0
                Piece first clicked - 1
                Piece moved - 2
         */

        if (selected != null) {
            selected = game.getPiece(x, y);
            return selected != null ? 1 : 0;
        } else {
            // TODO: If not valid unselect, otherwise move and return 2

            return 0;
        }
    }
}