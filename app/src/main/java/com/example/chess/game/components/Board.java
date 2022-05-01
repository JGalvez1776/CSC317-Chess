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
    private boolean[] canCastle = new boolean[4];
    // white kingside, white queenside, black kingside, black queenside

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
        stringIndex += 2;

        // handles castling
        char c = initalPosition.charAt(stringIndex);
        while (c != ' ') {
            c = initalPosition.charAt(stringIndex);
            switch (c) {
                case 'K': canCastle[0] = true; break;
                case 'Q': canCastle[1] = true; break;
                case 'k': canCastle[2] = true; break;
                case 'q': canCastle[3] = true; break;
            }
            stringIndex++;
        }
        for (boolean b: canCastle) System.out.println(b);
    }

    public void move(int startX, int startY, int endX, int endY) {
        Piece selected = getPiece(startX, startY);
        Player player = selected.getPlayer();

        place(null, startX, startY);
        place(selected, endX, endY);

        if (selected.toString().equals("Pawn")) {
            // promotion
            if (selected.getPlayer().toString().equals(WHITE) && (endY >= 7)) {
                selected = place(new Queen(players[currentPlayer]),endX,endY);
            } else if (selected.getPlayer().toString().equals(BLACK) && (endY <= 0)) {
                selected = place(new Queen(players[currentPlayer]),endX,endY);
            }
            // en pasante
            if (endX == startX-1) {
                Piece left = getPiece(startX - 1, startY);
                if (left != null && left.getPlayer().equals(otherPlayer(player))) {
                    place(null, startX - 1, startY);
                }
            }
            if (endX == startX+1) {
                Piece right = getPiece(startX+1,startY);
                if (right != null && right.getPlayer().equals(otherPlayer(player))) {
                    place(null, startX+1, startY);
                }
            }
        }

        if (selected.toString().equals("King")) {
            // castling
            if (selected.getPlayer().toString().equals(WHITE)) {
                if (endX - startX > 1) {
                    place(null,7,0);
                    place(new Rook(players[currentPlayer]),endX-1,startY);
                    canCastle[0] = false; canCastle[1] = false;
                }
                if (endX - startX < -1) {
                    place(null,0,0);
                    place(new Rook(players[currentPlayer]),endX+1,startY);
                    canCastle[0] = false; canCastle[1] = false;
                }
            } else {
                if (endX - startX > 1) {
                    place(null,7,7);
                    place(new Rook(players[currentPlayer]),endX-1,startY);
                    canCastle[2] = false; canCastle[3] = false;
                }
                if (endX - startX < -1) {
                    place(null,0,7);
                    place(new Rook(players[currentPlayer]),endX+1,startY);
                    canCastle[2] = false; canCastle[3] = false;
                }
            }
        }

        currentPlayer = (currentPlayer + 1) % players.length;
        selected.setMoved();
    }

    public List<int[]> getValidMoves(int x, int y) {
        // get all places piece can move to
        ArrayList<int[]> moves = getMoves(x,y);

        // TODO: Also make sure that moves do not put oneself into check!

        return moves;
    }

    private ArrayList<int[]> getMoves(int x, int y) {
        ArrayList<int[]> moves = new ArrayList<>();
        Piece piece = getPiece(x, y);
        if (piece == null)
            return null;
        Player player = piece.getPlayer();
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

        // diagonal pawn capture and en pasante
        if (piece.toString().equals("Pawn")) {
            int dir = 1;
            if (player.toString().equals(BLACK)) dir = -1;
            Piece diag1 = getPiece(x+1,y+dir);
            Piece right = getPiece(x+1,y);
            if ((diag1 != null && diag1.getPlayer().equals(otherPlayer(player)))
            || (right != null && right.getPlayer().equals(otherPlayer(player)))) {
                if (inBoard(x+1,y+dir))
                    moves.add(new int[]{x+1,y+dir});
            }
            Piece diag2 = getPiece(x-1,y+dir);
            Piece left = getPiece(x-1,y);
            if ((diag2 != null && diag2.getPlayer().equals(otherPlayer(player)))
            || (left != null && left.getPlayer().equals(otherPlayer(player)))) {
                if (inBoard(x-1,y+dir))
                    moves.add(new int[]{x-1,y+dir});
            }
        }

        // check for castling
        if (piece instanceof King && !piece.hasMoved()) {
            if (piece.getPlayer().toString().equals(WHITE)) {
                if (canCastle[1]) moves.add(new int[]{x - 2, y});
                if (canCastle[0]) moves.add(new int[]{x + 2, y});
            } else {
                if (canCastle[3]) moves.add(new int[]{x - 2, y});
                if (canCastle[2]) moves.add(new int[]{x + 2, y});
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
                            // check for pawn en pasante
                            if (p.toString().equals("Pawn")) {
                                if (player.equals(WHITE))
                                    if (m[0] == pos[0] && m[1] == pos[1]-1) return true;
                                if (player.equals(BLACK))
                                    if (m[0] == pos[0] && m[1] == pos[1]+1) return true;
                            }
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

    private Piece place(Piece piece, int x, int y) {
        board[y][x] = piece;
        return board[y][x];
    }

    public Piece getPiece(int x, int y) {
        if (!inBoard(x,y)) return null;
        return board[y][x];
    }

    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }



}