/**
 * @author: Jaugee Galvez
 * @description: Servers as a controller class for the game of chess.
 *               Abstracts the implementation to instead focus on selecting
 *               pieces to move and where to.
 */

package com.example.chess.game;

import com.example.chess.game.components.Board;
import com.example.chess.game.pieces.Piece;

import java.util.Locale;

public class GameController {
    private final Board game;
    //private Piece selected = null;
    private int[] selected = null;

    // TODO: Fill in
    public static final int NOTHING_SELECTED = 0;
    public static final int PIECE_SELECTED = 1;
    public static final int PIECE_MOVED = 2;

    /**
     * Default constructor that initializes with default game of chess
     */
    public GameController() {
        this.game = new Board();
    }

    /**
     * Constructor that uses a specific Board object (Allows puzzles)
     * @param game Board to be contained and used by the controller
     */
    public GameController(Board game) {
        this.game = game;
    }


    /**
     * Grabs the piece at (x, y) and returns its name.
     * @param x int x coordinate to grab the piece from
     * @param y int y coordinate to grab the piece from
     * @return String which is the name of the piece formatted as PlayerPiece EX: BlackPawn
     */
    public String getPieceName(int x, int y) {
        // Converts y to match implementation of the model
        y = convertPosition(x, y)[1];
        Piece piece = game.getPiece(x, y);
        if (piece == null) return null;
        String name = piece.getName();
        return name.toLowerCase();
        
    }

    private int[] convertPosition(int x, int y) {
        return new int[] {x, Math.abs(Board.HEIGHT - y - 1)};
    }

    /*
        NOTE: This method is what we want to call when squares are clicked. At the moment
        it's not fully implemented.
     */

    /**
     * Serves the core game logic. Represents "selecting" a square on the board.
     * First call of select will select a piece at a square.
     * Second call of select will move the piece to the selected square (If its a valid move)
     * If during either call, the position contains no piece or is an invalid move, must reselect
     * @param x int x coordinate to grab the piece from
     * @param y int y coordinate to grab the piece from
     * @return int error code. See class's static constants
     */
    public int select(int x, int y) {
        /*
            TODO: Ignore this comment it's notes for me
            Options:
                Nothing/Piece unselected - 0
                Piece first clicked - 1
                Piece moved - 2
         */

        y = convertPosition(x, y)[1];

        if (selected == null) {
            // Selected the piece and returns if the pieces is real (Properly selected)
            Piece selectedPiece = game.getPiece(x, y);

            if (selectedPiece != null && selectedPiece.getPlayer().equals(game.getCurrentPlayer()))
                    selected = new int[]{x, y};

            //selected = selectedPiece != null ? new int[]{x, y} : null;
            //selected = game.getPiece(x, y);
            return selected != null ? 1 : 0;
        } else {
            // TODO: If not valid unselect, otherwise move and return 2

            if (false) {
                // Have unselect if piece is invalid
                return 0;
            }
            game.move(selected[0], selected[1], x, y);
            selected = null;

            return 2;
        }
    }
}