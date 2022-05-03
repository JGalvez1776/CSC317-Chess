/*
 * @author: Jaygee Galvez
 * @description: Servers as a controller class for the game of chess.
 *               Abstracts the implementation to instead focus on selecting
 *               pieces to move and where to.
 */

package com.example.chess.game;

import com.example.chess.game.components.Board;
import com.example.chess.game.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    // controller variables
    protected Board game;
    protected int[] selected = null;
    protected int[] lastMoveOrigin = null;
    public boolean gameOver = false;

    // final variables
    public static final int NOTHING_SELECTED = 0;
    public static final int PIECE_SELECTED = 1;
    public static final int PIECE_MOVED = 2;
    public static final String BLACK = Board.BLACK;
    public static final String WHITE = Board.WHITE;

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

    /**
     * Converts board position to controller position.
     * @param x board x
     * @param y board y
     * @return Controller position.
     */
    protected int[] convertPosition(int x, int y) {
        return new int[] {x, Math.abs(Board.HEIGHT - y - 1)};
    }

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
        if (gameOver) return 0;
        y = convertPosition(x, y)[1];

        if (selected != null) {
            List<int[]> potentialMoves = game.getValidMoves(selected[0], selected[1]);
            if (potentialMoves != null && validMove(potentialMoves, x, y)) {
                game.move(selected[0], selected[1], x, y);
                lastMoveOrigin = selected;
                selected = null;
                return PIECE_MOVED;
            }
            selected = null;
        }

        // Selected the piece and returns if the pieces is real (Properly selected)
        Piece selectedPiece = game.getPiece(x, y);
        if (selectedPiece != null && selectedPiece.getPlayer().equals(game.getCurrentPlayer())) {
            selected = new int[]{x, y};
        }

        return selected != null ? PIECE_SELECTED : NOTHING_SELECTED;
    }

    /**
     * Checks whether or not a move is valid.
     * @param moves list of potential moves
     * @param x x coordinate
     * @param y y coordinate
     * @return Whether or not the move is valid.
     */
    protected boolean validMove(List<int[]> moves, int x, int y) {
        for (int[] position : moves) {
            if (position[0] == x && position[1] == y)
                return true;
        }
        return false;
    }

    /**
     * Returns list of all available moves to make for the selected piece.
     * @return List of all available moves.
     */
    public List<int[]> getAvailableMoves() {
        List<int[]> availableMoves = new ArrayList<>();
        List<int[]> potentialMoves = game.getValidMoves(selected[0], selected[1]);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int[] pos = convertPosition(x,y);
                if (potentialMoves != null && validMove(potentialMoves, x, y)) {
                    availableMoves.add(new int[]{pos[0],pos[1]});
                }
            }
        }
        return availableMoves;
    }

    /**
     * Returns string of current player.
     * @return Current player.
     */
    public String getCurrentPlayer() {
        return game.getCurrentPlayer().toString();
    }

    /**
     * Returns string of current player in check.
     * @return Current player in check. Returns null if no player is in check.
     */
    public String getCurrentCheck() {
        if (gameOver) return null;
        if (game.isCheck(WHITE)) {
            return WHITE;
        } else if (game.isCheck(BLACK)) {
            return BLACK;
        } else return null;
    }

    /**
     * Returns string of current player who has won.
     * @return Current player who has won. Returns null if no player is in check.
     */
    public String checkWinner() {
        if (!game.isAlive(WHITE)) {
            return BLACK;
        } else if (!game.isAlive(BLACK)) {
            return WHITE;
        } else return null;
    }

    /**
     * Ends the game, preventing anymore moves from being made.
     */
    public void endGame() {
        gameOver = true;
    }

}