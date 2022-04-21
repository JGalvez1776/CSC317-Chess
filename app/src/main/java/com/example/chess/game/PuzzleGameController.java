package com.example.chess.game;

import com.example.chess.game.components.Board;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class PuzzleGameController extends GameController {

    public static final String DAILY_PUZZLE_URL = "https://api.chess.com/pub/puzzle";

    public PuzzleGameController(String url) {
        JSONObject json = getJSON(url);
        String position = null;
        try {
            position = json.getString("fen");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        game = new Board(position);

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
