/*
 * @author: Min Tran and Jaygee Galvez
 * @description: Uses the GameController to make a controller suitable
 *               for playing chess puzzles instead of chess games.
 */

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
                position = json.getString("fen");
                pgn = json.getString("pgn");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (position != null) game = new Board(position);
        if (pgn != null) parsePGN(pgn);
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
    @Override
    public int select(int x, int y) {
        int result = super.select(x,y);

        // if piece moved, check move against solution
        if (result == PIECE_MOVED) {
            String[] move = getMoveString(x,y); // get move string

            // check if move was correct
            if (!String.join(" ",move).equals
                    (String.join(" ",solution[currMove]))) {
                correct = false; endGame();
                return result;
            }
            currMove++;

            // determine to end game or do computer move
            if (puzzleComplete()) endGame();
        }
        return result;
    }

    /**
     * Checks if the puzzle is completed correctly.
     * @return Whether or not the puzzle is completed correctly.
     */
    private boolean puzzleComplete() {
        return currMove >= solution.length || solution[currMove][0].equals("*");
    }

    /**
     * Returns a string representing a move made, given the end positions of the piece.
     * @param x end x coordinate
     * @param y end y coordinate
     * @return String representation of move.
     */
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

    /**
     * Performs a computer move in response to a player move
     * @return 4 integers in an array which are the positions
     *         x,y of the start and end of a move
     */
    public int[] doComputerMove() {
        String[] move = solution[currMove];
        int[] computerMove = getComputerMove(move);
        game.move(computerMove[0],computerMove[1],computerMove[2],computerMove[3]);
        currMove++;
        if (puzzleComplete()) endGame();
        return computerMove;
    }

    /**
     * Calculates the next computer move and returns an integer representation of the move.
     * @param move String array representing move.
     * @return 4 integers in an array which are the positions
     *         x,y of the start and end of a move
     */
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

    /**
     * Given a computer move, returns the appropriate coordinates for animating it.
     * @param move integer array representation of computer move
     * @return Integer array of coordinates for animation.
     */
    public int[] getMoveAnimation(int[] move) {
        int[] startPos = convertPosition(move[0],move[1]);
        int[] endPos = convertPosition(move[2],move[3]);
        return new int[]{startPos[0],startPos[1],endPos[0],endPos[1]};
    }

    /**
     * Given a PGN representation of the chess game, parses it into an array of strings
     * arrays representing a move.
     * @param pgn PGN string
     */
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

    /**
     * Converts a single move in PGN format to a string array representation of the move.
     * @param pgnMove PGN move string
     * @param currPlayer player using this move
     * @return String array representation of move.
     */
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

    /**
     * Trims a raw PGN move string of unnecessary characters and numbers.
     * @param pgnMove raw PGN move string
     * @return Trimmed PGN move string.
     */
    private String trimPGNMove(String pgnMove) {
        // remove move number and extra characters
        while (Character.isDigit(pgnMove.charAt(0)))
            pgnMove = pgnMove.substring(1);
        pgnMove = pgnMove.replaceAll("[.x#+]","");
        return pgnMove;
    }

    /**
     * Returns a string array of the starting position denoted by the PGN string.
     * @param pgnMove PGN move string
     * @return String array of the starting position.
     */
    @SuppressWarnings("ConstantConditions")
    private String[] getPGNMoveStart(String pgnMove) {
        String[] startPos = new String[]{ANY_POSITION, ANY_POSITION};
        if (pgnMove.length() > 3) {
            char check = pgnMove.charAt(1);
            // check first character
            if (Character.isAlphabetic(check)) startPos[0] =
                    Integer.toString(posMap.get(check));
            if (Character.isDigit(check)) startPos[1] =
                    Integer.toString(posMap.get(check));
            if (pgnMove.length() > 4) {
                // check second character
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
