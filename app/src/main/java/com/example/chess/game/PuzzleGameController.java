package com.example.chess.game;

import com.example.chess.game.components.Board;
import com.example.chess.game.components.Player;
import com.example.chess.game.pieces.Piece;
import com.example.chess.game.pieces.Placeable;
import com.example.chess.game.pieces.concrete.Bishop;
import com.example.chess.game.pieces.concrete.King;
import com.example.chess.game.pieces.concrete.Knight;
import com.example.chess.game.pieces.concrete.Pawn;
import com.example.chess.game.pieces.concrete.Queen;
import com.example.chess.game.pieces.concrete.Rook;

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
        posMap.put('a',0); posMap.put('1',0);
        posMap.put('b',1); posMap.put('2',1);
        posMap.put('c',2); posMap.put('3',2);
        posMap.put('d',3); posMap.put('4',3);
        posMap.put('e',4); posMap.put('5',4);
        posMap.put('f',5); posMap.put('6',5);
        posMap.put('g',6); posMap.put('7',6);
        posMap.put('h',7); posMap.put('8',7);
    }

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

    @Override
    public int select(int x, int y) {
        int result = super.select(x,y);
        if (result == PIECE_MOVED) {
            y = convertPosition(x, y)[1];
            Piece piece = game.getPiece(x, y);
            if (piece != null) {
                String move = piece.getPlayer() + " " + piece + " " + x + " " + y;
                if (!move.equals(solution[currMove])) {
                    correct = false;
                    endGame();
                    return result;
                }
                currMove++;
                // determine to end game or do computer move
                if (solution[currMove].equals("*"))
                    endGame();
                else {
                    String[] split = solution[currMove].split(" ");
                    setComputerMove(new Player(split[0]), split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                    game.move(computerMove[0],computerMove[1],computerMove[2],computerMove[3]);
                    currMove++;
                }

            }
        }
        return result;
    }

    public void setComputerMove(Player player, String name, int x, int y) {
        System.out.println(player+" "+name+" "+x+" "+y);
        int[][] pieces = game.getPiecePosition(player, name);
        List<int[]> validMoves1 = game.getValidMoves(pieces[0][0],pieces[0][1]);
        List<int[]> validMoves2 = game.getValidMoves(pieces[1][0],pieces[1][1]);
        if (validMoves1 != null && validMove(validMoves1,x,y)) {
            computerMove = new int[]{pieces[0][0],pieces[0][1],x,y};
        } else if (validMoves2 != null && validMove(validMoves2,x,y)) {
            computerMove = new int[]{pieces[1][0],pieces[1][1],x,y};
        }
    }

    public int[] getComputerMove() {
        return computerMove;
    }

    private String parseSolution(String sol) {
        String result;
        Player currPlayer = game.getCurrentPlayer();
        int m = sol.indexOf("1...");
        result = sol.substring(m);
        String[] split = result.split(" ");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("*")) continue;
            if (Character.isDigit(split[i].charAt(0))) {
                split[i] = split[i].substring(1);
            }
            split[i] = split[i].replaceAll("\\.|x|\\+","");

            // translate to move
            split[i] = currPlayer + " " + fenMap.get(split[i].charAt(0)) + " "
                    + posMap.get(split[i].charAt(1)) + " " + posMap.get(split[i].charAt(2));
            currPlayer = game.otherPlayer(currPlayer);
        }
        solution = split;
        return result;
    }

    private JSONObject getJSON(String urlString) {
        try {
            String json = "";
            String line;
            URL url = new URL(urlString);

            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6)AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 OPR/71.0.3770.284");
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            while ((line = in.readLine()) != null) {
                json += line;
            }
            in.close();

            JSONObject jsonObject = new JSONObject(json);

            return jsonObject;
        } catch (Exception e) {
            System.err.println("An error occured fetching JSON");
            e.printStackTrace();
        }
        return null;
    }

}
