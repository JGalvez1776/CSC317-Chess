package com.example.chess.game.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.example.chess.game.pieces.Piece;
import com.example.chess.game.pieces.Placeable;
import com.example.chess.game.pieces.concrete.*;

public class Board {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static final String WHITE = "White";
    public static final String BLACK = "Black";
    
    private Piece[][] board = new Piece[HEIGHT][WIDTH];
    private Player[] players = new Player[]{new Player(WHITE), new Player(BLACK)};
    private int currentPlayer = 0;

    private static final String DEFAULT_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private static HashMap<Character, Placeable> pieceMap;
    static {
        pieceMap = new HashMap<>();
        pieceMap.put('r', Rook::new);
        pieceMap.put('q', Queen::new);
        pieceMap.put('k', King::new);
        pieceMap.put('n', Knight::new);
        pieceMap.put('b', Bishop::new);
        pieceMap.put('p', Pawn::new);
    }

    public Board() {
        this(DEFAULT_BOARD);
    }

    @Override
    public String toString() {
        // TODO: Convert to FEN notation (Format constructor use to make a Board)
        return "NULL";
    }

    public Board(String initalPosition) {
        // Lowercase is black
        // "8/6p1/8/K6P/7P/kr5R/8/8 w - - 0 1"
        // FEN (Forsyth-Edwards Notation)
        // https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
        int row = HEIGHT - 1;
        int column = 0;
        int stringIndex = 0;
        char character = initalPosition.charAt(0);
        while (character != ' ') {
            character = initalPosition.charAt(stringIndex);
            if (character == '/' || column >= WIDTH) {
                row--;
                column = -1;
            } else if (Character.isDigit(character)) {
                column = column + Character.getNumericValue(character) - 1;
            } else {
                // Calculates the correct player and adds to board
                Player player = players[Character.toLowerCase(character) == character ? 1 : 0];
                board[row][column] = pieceMap.get(Character.toLowerCase(character)).create(player);
            }
            stringIndex++;
            column++;
        }

        char player = initalPosition.charAt(stringIndex);
        if (player == 'b') {
            currentPlayer = 1;
        } else {
            currentPlayer = 0;
        }

        // TODO: Handle castling
    }

    public void move(int startX, int startY, int endX, int endY) {
        Piece selected = getPiece(startX, startY);
        // TODO: Add in all the special moves / check for it here

        place(null, startX, startY);
        place(selected, endX, endY);
        currentPlayer = (currentPlayer + 1) % players.length;
        selected.setMoved();
    }

    public List<int[]> getValidMoves(int x, int y) {
        ArrayList<int[]> moves = new ArrayList<>();
        Piece piece = getPiece(x, y);
        if (piece == null)
            return null;
        List<Move> potentialMoves = piece.getPotentialMoves();
        for (Move move : potentialMoves) {
            int curX = x + move.getShiftX();
            int curY = y + move.getShiftY();
            boolean checkNext = true && canMoveTo(piece, curX, curY);
            while (checkNext) {
                moves.add(new int[]{curX, curY});
                curX += move.getShiftX();
                curY += move.getShiftY();
                checkNext = move.isRepeatable() && canMoveTo(piece, curX, curY);
            }
        }

        // TODO: Also make sure that moves do not put oneself into check!
        // find king
        System.out.println(Arrays.toString(getPiecePosition(new King(piece.getPlayer()))));

        // TODO: Add the special moves here!!!


        return moves;
    }

    private boolean isCheck(int x, int y, String player) {
        return false;
    }

    private int[] getPiecePosition(Piece piece) {
        int[] pos = new int[2]; // 0 = x, 1 = y
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (piece.equals(getPiece(x,y))) {
                    pos[0] = x; pos[1] = y;
                    return pos;
                }
            }
        }
        return null;
    }


    private boolean canMoveTo(Piece piece, int x, int y) {

        return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT &&
               (getPiece(x, y) == null ||
                !getPiece(x, y).getPlayer().equals(piece.getPlayer()));
    }

    private void place(Piece piece, int x, int y) {
        board[y][x] = piece;
    }



    public Piece getPiece(int x, int y) {
        return board[y][x];
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }



}