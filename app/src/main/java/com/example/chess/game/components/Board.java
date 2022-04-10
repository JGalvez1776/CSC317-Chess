package com.example.chess.game.components;

import java.util.HashMap;

import com.example.chess.game.pieces.Piece;
import com.example.chess.game.pieces.Placeable;
import com.example.chess.game.pieces.concrete.*;

public class Board {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    
    private Piece[][] board = new Piece[HEIGHT][WIDTH];
    private Player[] players = new Player[]{new Player("White"), new Player("Black")};

    private static final String DEFAULT_BOARD = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR ";
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

    public Board(String initalPosition) {
        // Lowercase is black
        // "8/6p1/8/K6P/7P/kr5R/8/8 w - - 0 1"
        int row = HEIGHT - 1;
        int column = 0;
        int stringIndex = 0;
        char character = initalPosition.charAt(0);
        while (character != ' ' && stringIndex < initalPosition.length()) {
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
    }



    public Piece getPiece(int x, int y) {
        return board[y][x];
    }



}