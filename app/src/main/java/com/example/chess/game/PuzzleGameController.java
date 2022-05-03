/**
 * @author: Min Tran
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PuzzleGameController extends GameController {

    public static final String DAILY_PUZZLE_URL = "https://api.chess.com/pub/puzzle";
    public static final String RANDOM_PUZZLE_URL = "https://api.chess.com/pub/puzzle/random";
    public String[] solution;
    public int currMove = 0;
    public boolean correct = true;
    public int[] computerMove;
    private static HashMap<Character, String> fenMap;
    static {
        fenMap = new HashMap<>();
        fenMap.put('R', "Rook");
        fenMap.put('Q', "Queen");
        fenMap.put('K', "King");
        fenMap.put('N', "Knight");
        fenMap.put('B', "Bishop");
        fenMap.put('P', "Pawn");
    }
    private static HashMap<Character, Integer> posMap;
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
     * Creates a new instance using a url to a chess.com puzzle
     * @param url API Url for the chess.com puzzle api
     */
    public PuzzleGameController(String url) {
        JSONObject json = getJSON(url);
        String position = null;
        String sol = null;
        try {
            position = json.getString("fen");
            sol = json.getString("pgn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        game = new Board(position);
        sol = parseSolution(sol);
        for (String s: solution) System.out.println(s);
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
        // do normal select
        int result = super.select(x,y);

        // if piece moved, check move against solution
        if (result == PIECE_MOVED) {
            y = convertPosition(x, y)[1];
            Piece piece = game.getPiece(x, y);
            if (piece != null) {
                int pos[] = convertPosition(x,y);
                String move = piece.getPlayer() + " " + piece + " " + pos[0] + " " + pos[1];
                if (piece instanceof King && (Math.abs(x - lastMoveorigin[0]) >= 2)) {
                    int difference = x - lastMoveorigin[0];
                    if (difference > 0) move = "O-O";
                    else move = "O-O-O";
                }
                System.out.println(move);
                if (!move.equals(solution[currMove])) {
                    correct = false;
                    endGame();
                    return result;
                }
                currMove++;
                // determine to end game or do computer move
                if (solution[currMove].equals("*"))
                    endGame();
            }
        }
        return result;
    }

    /**
     * Sets how a computer moves in response to a correct player move
     * @param player Player the computer plays as
     * @param name Name of the move the computer is to make
     * @param x X coordinate of starting location of the piece
     * @param y Y coordinate of starting location of the piece
     */
    public void setComputerMove(Player player, String name, int x, int y) {
        System.out.print("SET: " + x + " " + y);
        List<int[]> pieces = game.getPiecePosition(player, name);
        int[] pos = convertPosition(x,y);
        for (int[] location : pieces) {
            List<int[]> validMoves = game.getValidMoves(location[0], location[1]);
            if (validMoves != null && validMove(validMoves, pos[0], pos[1])) {
                computerMove = new int[]{location[0],location[1],pos[0],pos[1]};
            }
        }
    }

    /**
     * Performs a computer move in response to a player move
     * @return 4 integers in an array which are the positions
     *         x,y of the start and end of a move
     */
    public int[] doComputerMove() {
        String[] split = solution[currMove].split(" ");
        System.out.println("Attempting: " + solution[currMove]);
        System.out.println("Breakdown: " + Arrays.toString(split));
        setComputerMove(new Player(split[0]), split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        System.out.println("Comp Moves: " + Arrays.toString(computerMove));

        game.move(computerMove[0],computerMove[1],computerMove[2],computerMove[3]);
        currMove++;
        int[] cMove1 = convertPosition(computerMove[0],computerMove[1]);
        int[] cMove2 = convertPosition(computerMove[2],computerMove[3]);
        int[] animMove = new int[]{cMove1[0],cMove1[1],cMove2[0],cMove2[1]};
        return animMove;
    }

    /**
     * Parses the solution to a puzzle from how it is formatted in the Chess.com API
     * @param sol String solution to a puzzle from the API
     * @return String output of the solution that can be used by this controller
     */
    private String parseSolution(String sol) {
        String result;
        Player currPlayer = game.getCurrentPlayer();
        int m = sol.indexOf("1...");
        if (m == -1) m = sol.indexOf("1.");
        System.out.println(sol);
        System.out.println(m);
        result = sol.substring(m);
        String[] split = result.split(" ");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
            if (split[i].equals("*") || split[i].equals("1-0") || split[i].equals("0-1")) {
                split[i] = "*";
                continue;
            }
            if (Character.isDigit(split[i].charAt(0))) {
                split[i] = split[i].substring(1);
            }
            split[i] = split[i].replaceAll("\\.|x|\\+","");
            System.out.println(split[i]);

            if (split[i].equals("O-O-O") || split[i].equals("O-O")) {
                currPlayer = game.otherPlayer(currPlayer);
                continue;
            }

            // translate to move
            String pieceName = fenMap.get(split[i].charAt(0));
            if (!Character.isUpperCase(split[i].charAt(0))) {
                pieceName = fenMap.get('P');
            }
            split[i] = currPlayer + " " + pieceName + " "
                    + posMap.get(split[i].charAt(1)) + " " + posMap.get(split[i].charAt(2));
            currPlayer = game.otherPlayer(currPlayer);
        }
        solution = split;
        System.out.println("SOLUTION: " + Arrays.toString(solution));
        return result;
    }

    /**
     * Retrieves JSON from url and returns JSONObject.
     * @param urlString string of url with JSON
     * @return JSONObject of JSON in url.
     */
    private JSONObject getJSON(String urlString) {
        try {
            String json = ""; String line;
            URL url = new URL(urlString);

            // open connection and read json
            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("user-agent", "Mozilla/5.0 " +
                    "(Macintosh; Intel Mac OS X 10_14_6)AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 OPR/71.0.3770.284");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while ((line = in.readLine()) != null) {
                json += line;
            }
            in.close();

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
