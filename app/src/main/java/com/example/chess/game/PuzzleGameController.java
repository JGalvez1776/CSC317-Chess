package com.example.chess.game;

import com.example.chess.game.components.Board;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;
import com.example.chess.game.pieces.concrete.King;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

public class PuzzleGameController extends GameController {

    public static final String DAILY_PUZZLE_URL = "https://api.chess.com/pub/puzzle";
    public static final String QUEEN_CASTLE = "QueenCastle";
    public static final String KING_CASTLE = "KingCastle";
    public static final String ANY_POSITION = "ANY";

    // controller variables
    public String[][] solution;
    public int currMove = 0;
    public boolean correct = true;

    // map to translate FEN chess notation
    private static final HashMap<Character, String> fenMap;
    static {
        fenMap = new HashMap<>();
        fenMap.put('R', "Rook"); fenMap.put('Q', "Queen");
        fenMap.put('K', "King"); fenMap.put('N', "Knight");
        fenMap.put('B', "Bishop"); fenMap.put('P', "Pawn");
    }

    // map to translate chess positions
    private static final HashMap<Character, Integer> posMap;
    static {
        posMap = new HashMap<>();
        posMap.put('a',0); posMap.put('1',7);
        posMap.put('b',1); posMap.put('2',6);
        posMap.put('c',2); posMap.put('3',5);
        posMap.put('d',3); posMap.put('4',4);
        posMap.put('e',4); posMap.put('5',3);
        posMap.put('f',5); posMap.put('6',2);
        posMap.put('g',6); posMap.put('7',1);
        posMap.put('h',7); posMap.put('8',0);
    }

    /**
     * Default constructor for class. Parses board and solution from daily puzzle API.
     */
    public PuzzleGameController() {
        JSONObject json = getJSON(DAILY_PUZZLE_URL);
        String position = null;
        String pgn = null;
        try {
            if (json != null) {
                position = Board.DEFAULT_BOARD;
                pgn = "[Event \"Third Rosenwald Trophy\"]\n" +
                        "[Site \"New York, NY USA\"]\n" +
                        "[Date \"1956.10.17\"]\n" +
                        "[EventDate \"1956.10.07\"]\n" +
                        "[Round \"8\"]\n" +
                        "[Result \"0-1\"]\n" +
                        "[White \"Donald Byrne\"]\n" +
                        "[Black \"Robert James Fischer\"]\n" +
                        "[ECO \"D92\"]\n" +
                        "[WhiteElo \"?\"]\n" +
                        "[BlackElo \"?\"]\n" +
                        "[PlyCount \"82\"]\n" +
                        "\n" +
                        "1.Nf3 Nf6 2.c4 g6 3.Nc3 Bg7 4.d4 O-O 5.Bf4 d5 6.Qb3 dxc4 7.Qxc4 c6 8.e4 Nbd7 9.Rd1 Nb6 10.Qc5 Bg4 11.Bg5 Na4 12.Qa3 Nxc3 13.bxc3 Nxe4 14.Bxe7 Qb6 15.Bc4 Nxc3 16.Bc5 Rfe8+ 17.Kf1 Be6 18.Bxb6 Bxc4+ 19.Kg1 Ne2+ 20.Kf1 Nxd4+ 21.Kg1 Ne2+ 22.Kf1 Nc3+ 23.Kg1 axb6 24.Qb4 Ra4 25.Qxb6 Nxd1 26.h3 Rxa2 27.Kh2 Nxf2 28.Re1 Rxe1 29.Qd8+ Bf8 30.Nxe1 Bd5 31.Nf3 Ne4 32.Qb8 b5 33.h4 h5 34.Ne5 Kg7 35.Kg1 Bc5+ 36.Kf1 Ng3+ 37.Ke1 Bb4+ 38.Kd1 Bb3+ 39.Kc1 Ne2+ 40.Kb1 Nc3+ 41.Kc1 Rc2# 0-1";//json.getString("pgn");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (position != null) game = new Board(position);
        if (pgn != null) parsePGN(pgn);
    }

    @Override
    public int select(int x, int y) {
        int result = super.select(x,y);

        // if piece moved, check move against solution
        if (result == PIECE_MOVED) {
            String[] move = getMoveString(x,y); // get move string

            // check if move was correct
            if (!String.join(" ",move).equals(String.join(" ",solution[currMove]))) {
                correct = false; endGame();
                return result;
            }
            currMove++;

            // determine to end game or do computer move
            if (currMove >= solution.length || solution[currMove][0].equals("*"))
                endGame();
        }
        return result;
    }

    private String[] getMoveString(int x, int y) {
        int[] pos = convertPosition(x,y);
        Piece piece = game.getPiece(pos[0], pos[1]);
        if (piece == null) return null;

        // get move string
        String[] move = new String[]{piece.getPlayer().toString(), piece.toString(),
                Integer.toString(pos[0]), Integer.toString(y), ANY_POSITION, ANY_POSITION};

        // check for castle
        if (piece instanceof King && (Math.abs(x - lastMoveOrigin[0]) >= 2)) {
            int difference = x - lastMoveOrigin[0];
            if (difference > 0) move = new String[]{piece.getPlayer().toString(),KING_CASTLE};
            else move = new String[]{piece.getPlayer().toString(),QUEEN_CASTLE};
        }

        // check starting piece location
        int[] realMoveOrigin = convertPosition(lastMoveOrigin[0],lastMoveOrigin[1]);
        if (solution[currMove].length > 4) {
            if (!solution[currMove][4].equals(ANY_POSITION))
                move[4] = Integer.toString(realMoveOrigin[0]);
            if (!solution[currMove][5].equals(ANY_POSITION))
                move[5] = Integer.toString(realMoveOrigin[1]);
        }
        return move;
    }

    public int[] doComputerMove() {
        String[] move = solution[currMove];
        int[] computerMove = getComputerMove(move);
        game.move(computerMove[0],computerMove[1],computerMove[2],computerMove[3]);
        currMove++;
        return computerMove;
    }

    private int[] getComputerMove(String[] move) {
        int[] computerMove = new int[4];

        // check for castle
        switch (String.join(" ",move)) {
            case "White "+KING_CASTLE:
                return new int[]{4, 0, 6, 0};
            case "White "+QUEEN_CASTLE:
                return new int[]{4, 0, 2, 0};
            case "Black "+KING_CASTLE:
                return new int[]{4, 7, 6, 7};
            case "Black "+QUEEN_CASTLE:
                return new int[]{4, 7, 2, 7};
        }
        // get move variables
        Player player = new Player(move[0]); String name = move[1];
        int[] endPos = convertPosition(Integer.parseInt(move[2]), Integer.parseInt(move[3]));
        List<int[]> pieces = game.getPiecePosition(player, name);

        // search pieces to get computer move
        for (int[] startPos : pieces) {
            List<int[]> validMoves = game.getValidMoves(startPos[0], startPos[1]);
            if (validMoves != null && validMove(validMoves, endPos[0], endPos[1])) {
                if ((!move[4].equals(ANY_POSITION) && Integer.parseInt(move[4]) != startPos[0])
                || (!move[5].equals(ANY_POSITION) && Integer.parseInt(move[4]) != startPos[1]))
                    continue;
                computerMove = new int[]{startPos[0], startPos[1], endPos[0], endPos[1]};
            }
        }
        return computerMove;
    }

    public int[] getMoveAnimation(int[] move) {
        int[] startPos = convertPosition(move[0],move[1]);
        int[] endPos = convertPosition(move[2],move[3]);
        return new int[]{startPos[0],startPos[1],endPos[0],endPos[1]};
    }

    private void parsePGN(String pgn) {
        Player currPlayer = game.getCurrentPlayer();

        String[] pgnMoves = pgn.substring(pgn.lastIndexOf('\n')+1).split(" ");
        solution = new String[pgnMoves.length][];

        // convert pgn moves
        for (int i = 0; i < pgnMoves.length; i++) {
            solution[i] = translatePGNMove(pgnMoves[i], currPlayer);
            currPlayer = game.otherPlayer(currPlayer);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String[] translatePGNMove(String pgnMove, Player currPlayer) {
        String player = currPlayer.toString();

        // check for endgame condition
        if (pgnMove.equals("*") || pgnMove.equals("1-0") || pgnMove.equals("0-1"))
            return new String[]{"*"};
        pgnMove = trimPGNMove(pgnMove);

        // check for castle
        if (pgnMove.charAt(0) == 'O')
            if (pgnMove.equals("O-O-O")) return new String[]{player,QUEEN_CASTLE};
            else if (pgnMove.equals("O-O")) return new String[]{player,KING_CASTLE};

        // translate to move
        String pieceName = fenMap.get(pgnMove.charAt(0));
        if (!Character.isUpperCase(pgnMove.charAt(0))) {
            pieceName = fenMap.get('P'); pgnMove = "P"+pgnMove;
        }
        // return starting piece of position
        String[] startPos = getPGNMoveStart(pgnMove);

        // convert to solution
        int splitEnd = pgnMove.length()-1;
        return new String[]{player,
                pieceName, posMap.get(pgnMove.charAt(splitEnd-1)).toString(),
                posMap.get(pgnMove.charAt(splitEnd)).toString(),
                startPos[0], startPos[1]};
    }

    private String trimPGNMove(String pgnMove) {
        // remove move number and extra characters
        while (Character.isDigit(pgnMove.charAt(0)))
            pgnMove = pgnMove.substring(1);
        pgnMove = pgnMove.replaceAll("[.x#+]","");
        return pgnMove;
    }

    @SuppressWarnings("ConstantConditions")
    private String[] getPGNMoveStart(String pgnMove) {
        String[] startPos = new String[]{ANY_POSITION, ANY_POSITION};
        if (pgnMove.length() > 3) {
            char check = pgnMove.charAt(1);
            if (Character.isAlphabetic(check)) startPos[0] =
                    Integer.toString(posMap.get(check));
            if (Character.isDigit(check)) startPos[1] =
                    Integer.toString(posMap.get(check));
            if (pgnMove.length() > 4) {
                check = pgnMove.charAt(2);
                if (Character.isAlphabetic(check)) startPos[0] =
                        Integer.toString(posMap.get(check));
                if (Character.isDigit(check)) startPos[1] =
                        Integer.toString(posMap.get(check));
            }
        }
        return startPos;
    }

    /**
     * Retrieves JSON from url and returns JSONObject.
     * @param urlString string of url with JSON
     * @return JSONObject of JSON in url.
     */
    @SuppressWarnings("SameParameterValue")
    private JSONObject getJSON(String urlString) {
        try {
            StringBuilder json = new StringBuilder(); String line;
            URL url = new URL(urlString);

            // open connection and read json
            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("user-agent", "Mozilla/5.0 " +
                    "(Macintosh; Intel Mac OS X 10_14_6)AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 OPR/71.0.3770.284");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while ((line = in.readLine()) != null) {
                json.append(line);
            }
            in.close();

            return new JSONObject(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
