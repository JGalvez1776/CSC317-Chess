/*
 * @author: Min Tran
 * @description: This fragment handles the daily puzzle game functionality and interface.
 */

package com.example.chess;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.chess.game.GameController;
import com.example.chess.game.PuzzleGameController;

public class PuzzleFragment extends GameFragment {

    // fragment variables
    private static final int LAYOUT = R.layout.fragment_game;
    protected AppCompatActivity containerActivity;
    protected View inflatedView;

    // permissions variables
    ActivityResultLauncher<String> requestPermissionLauncher;

    // game and board variables
    protected GameController controller;
    protected Chessboard chessboard;
    protected int attempts = 0;

    /**
     * Sets container activity.
     * @param containerActivity activity that fragment is contained in
     */
    public void setContainerActivity(AppCompatActivity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * Upon view creation, setup layout, permission launcher, and game controller.
     * @param inflater layout inflater
     * @param container view group container
     * @param savedInstanceState saved instance state
     * @return Inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get inflated view
        inflatedView = inflater.inflate(LAYOUT, container, false);

        // setup buttons
        inflatedView.findViewById(R.id.undo_button).setOnClickListener(this);
        inflatedView.findViewById(R.id.share_button).setOnClickListener(this);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.current_check)).setAlpha(0.0F);

        // initializes permission launcher
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openShareFragment();
            });

        // fetch the game controller
        new FetchPuzzle().execute(PuzzleGameController.DAILY_PUZZLE_URL);

        return inflatedView;
    }

    /**
     * Fetches the controller and draws the chessboard once fetched.
     */
    @Override
    public void setupBoard() {
        new FetchPuzzle().execute(PuzzleGameController.DAILY_PUZZLE_URL);
        chessboard = new Chessboard(containerActivity, inflatedView, controller);
        chessboard.drawBoard();
    }

    /**
     * Performs associated actions depending on which view was clicked.
     * @param view view that was clicked
     */
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // refreshes board, updates number of attempts
            case R.id.undo_button:
                if (controller != null) {
                    setupBoard();
                    attempts++;
                    ((TextView) inflatedView.findViewById(R.id.attempts_count))
                            .setText("Attempts: " + attempts);
                }
                break;
            // opens share fragment
            case R.id.share_button:
                if (ContextCompat.checkSelfPermission(
                        containerActivity, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED) {
                    openShareFragment();
                } else requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                break;
        }
    }

    /**
     * Performs transaction to open and view share fragment with number of attempts.
     */
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

    /**
     * Fetches puzzle game controller.
     */
    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("deprecation")
    private class FetchPuzzle extends AsyncTask<String, Integer, GameController> {

        /**
         * Given daily puzzle api url, initializes puzzle game controller.
         * @param urls daily puzzle api url
         * @return Created controller.
         */
        @Override
        protected GameController doInBackground(String... urls) {
            return new PuzzleGameController();
        }

        /**
         * Sets controller to fetched controller and draws chessboard.
         * @param result fetched controller
         */
        protected void onPostExecute(GameController result) {
            controller = result;
            chessboard = new Chessboard(containerActivity, inflatedView, controller);
            chessboard.drawBoard();
        }
    }

}
