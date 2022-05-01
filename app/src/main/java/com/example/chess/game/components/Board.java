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
        // get all places piece can move to
        ArrayList<int[]> moves = getMoves(x,y);

        // TODO: Also make sure that moves do not put oneself into check!
        // System.out.println(isCheck(getPiece(x,y).getPlayer()));

        // TODO: Add the special moves here!!!

        return moves;
    }

    private ArrayList<int[]> getMoves(int x, int y) {
        ArrayList<int[]> moves = new ArrayList<>();
        Piece piece = getPiece(x, y);
        if (piece == null)
            return null;
        List<Move> potentialMoves = piece.getPotentialMoves();
        for (Move move : potentialMoves) {
            int curX = x + move.getShiftX();
            int curY = y + move.getShiftY();
            boolean checkNext = canMoveTo(piece, curX, curY);
            boolean blocked = false;
            while (checkNext && !blocked) {
                moves.add(new int[]{curX, curY});

                // check if enemy piece is blocking the way
                Piece curPiece = getPiece(curX,curY);
                if (curPiece != null)
                    if (otherPlayer(piece.getPlayer()).equals(curPiece.getPlayer()))
                        blocked = true;

                // get next curX curY and checkNext
                curX += move.getShiftX();
                curY += move.getShiftY();
                checkNext = move.isRepeatable() && canMoveTo(piece, curX, curY);
            }
        }
        return moves;
    }

    public boolean isCheck(String player) {
        Player play = new Player(player);
        int[][] kings = getPiecePosition(play,"King");
        if (kings == null) return false;
        int[] pos = kings[0];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // check if there is a piece and if they are opponent player
                Piece p = getPiece(x,y);
                if (p != null) {
                    if (p.getPlayer().equals(otherPlayer(play))) {
                        ArrayList<int[]> moves = getMoves(x,y);
                        for (int[] m: moves) {
                            if (m[0] == pos[0] && m[1] == pos[1])
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Player otherPlayer(Player p) {
        if (p.toString().equals(WHITE)) {
            return players[1];
        } else return players[0];
    }

    private boolean inBoard(int x, int y) {
        if ((x < 0 || x >= 8)
        || (y < 0 || y >= 8))
            return false;
        return true;
    }

    public int[][] getPiecePosition(Player player, String name) {
        int a = 0;
        int[][] all = new int[2][2];
        int[] pos = new int[2]; // 0 = x, 1 = y
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece piece = getPiece(x,y);
                if (piece != null) {
                    if (piece.toString().equals(name) && piece.getPlayer().equals(player)) {
                        pos[0] = x;
                        pos[1] = y;
                        all[a] = pos;
                        a++;
                    }
                }
            }
        }
        if (a <= 0) return null;
        return all;
    }

    public boolean isAlive(String player) {
        Player p = new Player(player);
        if (getPiecePosition(p, "King") == null) return false;
        return true;
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
        if (!inBoard(x,y)) return null;
        return board[y][x];
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }



}