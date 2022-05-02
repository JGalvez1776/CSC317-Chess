package com.example.chess;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    ActivityResultLauncher<String> requestPermissionLauncher;
    protected int attempts = 0;

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

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) openShareFragment();
                });

        // create the game controller
        new FetchPuzzle().execute(PuzzleGameController.DAILY_PUZZLE_URL);

        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);

        // setup buttons
        inflatedView.findViewById(R.id.undo_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.share_button).setOnClickListener(this);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.current_check)).setAlpha(0.0F);

        return inflatedView;
    }

    @Override
    public void setupBoard() {
        new FetchPuzzle().execute(PuzzleGameController.DAILY_PUZZLE_URL);
        chessboard = new Chessboard(containerActivity, inflatedView, controller);
        chessboard.drawBoard();
    }

    /**
     * Holds on click functions for each button in the layout.
     * @param view view that was clicked
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.undo_button:
                setupBoard();
                attempts++;
                ((TextView) inflatedView.findViewById(R.id.attempts_count)).setText("Attempts: "+attempts);
                break;
            case R.id.share_button:
                if (ContextCompat.checkSelfPermission(
                        containerActivity, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    openShareFragment();
                } else requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                break;
        }
    }

    public void openShareFragment() {
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();
        ShareFragment sf = new ShareFragment();
        sf.setContainerActivity(containerActivity);
        sf.setAttempts(attempts);
        transaction.replace(R.id.container, sf);
        transaction.addToBackStack(null);
        transaction.commit();
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
