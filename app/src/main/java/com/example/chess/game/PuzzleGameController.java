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

    // controller variables
    public String[][] solution;
    public int[] computerMove;
    public int currMove = 0;
    public boolean correct = true;
    public String[] orig;

    // map to translate FEN chess notation
    private static HashMap<Character, String> fenMap;
    static {
        fenMap = new HashMap<>();
        fenMap.put('R', "Rook"); fenMap.put('Q', "Queen");
        fenMap.put('K', "King"); fenMap.put('N', "Knight");
        fenMap.put('B', "Bishop"); fenMap.put('P', "Pawn");
    }

    // map to translate chess positions
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
     * Default constructor for class. Parses board and solution from daily puzzle API.
     */
    public PuzzleGameController() {
        JSONObject json = getJSON(DAILY_PUZZLE_URL);
        String position = null;
        String pgn = null;
        try {
            position = json.getString("fen");
            pgn = json.getString("pgn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (position != null) game = new Board(position);
        if (pgn != null) parseSolution(pgn);
    }

    @Override
    public int select(int x, int y) {
        // do normal select
        int result = super.select(x,y);

        // if piece moved, check move against solution
        if (result == PIECE_MOVED) {
            y = convertPosition(x, y)[1];
            Piece piece = game.getPiece(x, y);

            if (piece != null) {
                int[] pos = convertPosition(x,y);

                // get move string
                String[] move = new String[]{piece.getPlayer().toString(), piece.toString(),
                        Integer.toString(pos[0]), Integer.toString(pos[1]), "ANY", "ANY"};

                // check for castle
                if (piece instanceof King && (Math.abs(x - lastMoveorigin[0]) >= 2)) {
                    int difference = x - lastMoveorigin[0];
                    if (difference > 0) move = new String[]{piece.getPlayer().toString(),"KingCastle"};
                    else move = new String[]{piece.getPlayer().toString(),"QueenCastle"};
                }

                // check if piece location was specified
                int[] realMoveorigin = convertPosition(lastMoveorigin[0],lastMoveorigin[1]);
                if (solution[currMove].length > 4) {
                    if (!solution[currMove][4].equals("ANY")) {
                        move[4] = Integer.toString(realMoveorigin[0]);
                    }
                    if (!solution[currMove][5].equals("ANY")) {
                        move[5] = Integer.toString(realMoveorigin[1]);
                    }
                }
                System.out.println(orig[currMove]);
                System.out.println(Arrays.toString(move));
                System.out.println(Arrays.toString(solution[currMove]));

                if (!String.join(" ",move).equals(String.join(" ",solution[currMove]))) {
                    correct = false;
                    endGame();
                    return result;
                }
                currMove++;
                // determine to end game or do computer move
                if (currMove >= solution.length || solution[currMove][0].equals("*"))
                    endGame();
            }
        }
        return result;
    }

    public int[] doComputerMove() {
        System.out.println(orig[currMove]);
        String[] move = solution[currMove];
        System.out.println("Attempting: " + Arrays.toString(solution[currMove]));
        if (String.join(" ",move).equals("White KingCastle")) {
            computerMove = new int[]{4, 0, 6, 0};
        } else if (String.join(" ",move).equals("White QueenCastle")) {
            computerMove = new int[]{4, 0, 2, 0};
        } else if (String.join(" ",move).equals("Black KingCastle")) {
            computerMove = new int[]{4, 7, 6, 7};
        } else if (String.join(" ",move).equals("Black QueenCastle")) {
            computerMove = new int[]{4, 7, 2, 7};
        } else {
            System.out.println("Breakdown: " + Arrays.toString(move));
            Player player = new Player(move[0]);
            String name = move[1];
            int x = Integer.parseInt(move[2]);
            int y = Integer.parseInt(move[3]);
            System.out.print("SET: " + x + " " + y);
            List<int[]> pieces = game.getPiecePosition(player, name);
            int[] pos = convertPosition(x, y);
            for (int[] location : pieces) {
                List<int[]> validMoves = game.getValidMoves(location[0], location[1]);
                if (validMoves != null && validMove(validMoves, pos[0], pos[1])) {
                    if (!move[4].equals("ANY")) {
                        if (Integer.parseInt(move[4]) != location[0]) continue;
                    }
                    if (!move[5].equals("ANY")) {
                        if (Integer.parseInt(move[4]) != location[1]) continue;
                    }
                    computerMove = new int[]{location[0], location[1], pos[0], pos[1]};
                }
            }
        }

        System.out.println("Comp Moves: " + Arrays.toString(computerMove));
        game.move(computerMove[0],computerMove[1],computerMove[2],computerMove[3]);

        currMove++;
        System.out.println("NEXT MOVE: "+orig[currMove]+" "+Arrays.toString(solution[currMove]));
        int[] cMove1 = convertPosition(computerMove[0],computerMove[1]);
        int[] cMove2 = convertPosition(computerMove[2],computerMove[3]);
        int[] animMove = new int[]{cMove1[0],cMove1[1],cMove2[0],cMove2[1]};
        return animMove;
    }

    private String parseSolution(String sol) {
        String result;
        Player currPlayer = game.getCurrentPlayer();
        int m = sol.indexOf("1...");
        if (m == -1) m = sol.indexOf("1.");
        result = sol.substring(m);
        String[] split = result.split(" ");
        orig = result.split(" ");

        solution = new String[split.length][];
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("*") || split[i].equals("1-0") || split[i].equals("0-1")) {
                solution[i] = new String[]{"*"};
                continue;
            }
            while (Character.isDigit(split[i].charAt(0))) {
                split[i] = split[i].substring(1);
            }
            split[i] = split[i].replaceAll("\\.|x|#|\\+","");
            System.out.println(split[i]);

            if (split[i].equals("O-O-O") || split[i].equals("O-O")) {
                if (split[i].equals("O-O-O")) solution[i] = new String[]{currPlayer.toString(),"QueenCastle"};
                else if (split[i].equals("O-O")) solution[i] = new String[]{currPlayer.toString(),"KingCastle"};
                currPlayer = game.otherPlayer(currPlayer);
                continue;
            }

            // translate to move

            String pieceName = fenMap.get(split[i].charAt(0));
            if (!Character.isUpperCase(split[i].charAt(0))) {
                pieceName = fenMap.get('P');
                split[i] = "P"+split[i];
            }

            // check if position of piece is notated
            String piecePosX = "ANY";
            String piecePosY = "ANY";

            if (split[i].length() > 3) {
                char check1 = split[i].charAt(1);
                if (Character.isAlphabetic(check1)) piecePosX = Integer.toString(posMap.get(check1));
                if (Character.isDigit(check1)) piecePosY = Integer.toString(posMap.get(check1));
                if (split[i].length() > 4) {
                    char check2 = split[i].charAt(2);
                    if (Character.isAlphabetic(check2)) piecePosX = Integer.toString(posMap.get(check2));
                    if (Character.isDigit(check2)) piecePosY = Integer.toString(posMap.get(check2));
                }
            }

            int splitEnd = split[i].length()-1;
            solution[i] = new String[]{currPlayer.toString(),
                pieceName, posMap.get(split[i].charAt(splitEnd-1)).toString(),
                posMap.get(split[i].charAt(splitEnd)).toString(),
                piecePosX, piecePosY};
            currPlayer = game.otherPlayer(currPlayer);
        }
        for (String[] s: solution) System.out.println("SOLUTION: " + Arrays.toString(s));
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
