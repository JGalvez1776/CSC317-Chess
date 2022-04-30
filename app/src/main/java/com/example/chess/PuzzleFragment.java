package com.example.chess;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chess.game.GameController;
import com.example.chess.game.PuzzleGameController;

import java.net.URL;
import java.util.HashMap;

public class PuzzleFragment extends GameFragment {

    private static final int LAYOUT = R.layout.fragment_game;

    protected AppCompatActivity containerActivity;
    protected View inflatedView;
    protected GameController controller;
    protected Chessboard chessboard;

    /**
     * Sets container activity.
     * @param containerActivity activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, sets layout, and returns inflated view.
     * @param inflater layout inflater
     * @param container view group container
     * @param savedInstanceState saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create the game controller
        new FetchPuzzle().execute(PuzzleGameController.DAILY_PUZZLE_URL);


        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);
        // setup buttons
        // TODO: implement undo button toggle
        inflatedView.findViewById(R.id.undo_button).setOnClickListener(this);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.current_turn)).setAlpha(0.0F);
        ((TextView) inflatedView.findViewById(R.id.current_check)).setAlpha(0.0F);

        return inflatedView;
    }

    /**
     * Holds on click functions for each button in the layout.
     * @param view view that was clicked
     */
    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.undo_button:
                // TODO: undo button functionality
                break;
        }
    }

    private class FetchPuzzle extends AsyncTask<String, Integer, GameController> {

        @Override
        protected GameController doInBackground(String... urls) {
            return new PuzzleGameController(urls[0]);
        }

        protected void onPostExecute(GameController result) {
            controller = result;
            chessboard = new Chessboard(containerActivity, inflatedView, controller);
            chessboard.drawBoard();
        }
    }

}
