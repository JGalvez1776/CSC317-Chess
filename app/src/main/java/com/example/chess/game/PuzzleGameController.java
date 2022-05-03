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
            position = Board.DEFAULT_BOARD;//json.getString("fen");
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
                System.out.println(orig[currMove]);
                System.out.println(Arrays.toString(move));
                if (!String.join(" ",move).equals(String.join(" ",solution[currMove]))) {
                    correct = false;
                    endGame();
                    return result;
                }
                currMove++;
                // determine to end game or do computer move
                if (solution[currMove][0].equals("*"))
                    endGame();
            }
        }
        return result;
    }

    public int[] doComputerMove() {
        System.out.println(orig[currMove]);
        String[] move = solution[currMove];
        System.out.println("Attempting: " + solution[currMove]);
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
        sol = "1.Nf3 Nf6 2.c4 g6 3.Nc3 Bg7 4.d4 O-O 5.Bf4 d5 6.Qb3 dxc4 7.Qxc4 c6 8.e4 Nbd7 9.Rd1 Nb6 10.Qc5 Bg4 11.Bg5 Na4 12.Qa3 Nxc3 13.bxc3 Nxe4 14.Bxe7 Qb6 15.Bc4 Nxc3 16.Bc5 Rfe8+ 17.Kf1 Be6 18.Bxb6 Bxc4+ 19.Kg1 Ne2+ 20.Kf1 Nxd4+ 21.Kg1 Ne2+ 22.Kf1 Nc3+ 23.Kg1 axb6 24.Qb4 Ra4 25.Qxb6 Nxd1 26.h3 Rxa2 27.Kh2 Nxf2 28.Re1 Rxe1 29.Qd8+ Bf8 30.Nxe1 Bd5 31.Nf3 Ne4 32.Qb8 b5 33.h4 h5 34.Ne5 Kg7 35.Kg1 Bc5+ 36.Kf1 Ng3+ 37.Ke1 Bb4+ 38.Kd1 Bb3+ 39.Kc1 Ne2+ 40.Kb1 Nc3+ 41.Kc1 Rc2# 0-1";
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
            // check if position of piece is notated
            if (split[i].length() > 3) {
                if (split[i].length() > 4) {
                }
            }

            String pieceName = fenMap.get(split[i].charAt(0));
            int splitEnd = split[i].length()-1;
            if (!Character.isUpperCase(split[i].charAt(0))) {
                pieceName = fenMap.get('P');
            }
            solution[i] = new String[]{currPlayer.toString(),
                pieceName, posMap.get(split[i].charAt(splitEnd-1)).toString(),
                posMap.get(split[i].charAt(splitEnd)).toString(),
                "ANY", "ANY"};
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
