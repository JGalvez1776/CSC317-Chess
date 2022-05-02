package com.example.chess;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.chess.game.GameController;


public class GameFragment extends Fragment implements View.OnClickListener {

    // fragment variables
    private static final int LAYOUT = R.layout.fragment_game;
    protected AppCompatActivity containerActivity;
    protected View inflatedView;

    // game and board variables
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
     * Upon view creation, setup layout.
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
        TextView undoButton = inflatedView.findViewById(R.id.undo_button);
        SharedPreferences sharedPref = containerActivity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getInt("undo",1) == 1) {
            undoButton.setOnClickListener(this);
        } else undoButton.setAlpha(0.0F);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.attempts_count)).setAlpha(0.0F);
        ((TextView) inflatedView.findViewById(R.id.move_feedback)).setAlpha(0.0F);
        ((TextView) inflatedView.findViewById(R.id.share_button)).setAlpha(0.0F);

        setupBoard();
        return inflatedView;
    }

    /**
     * Gets the controller and draws the chessboard.
     */
    public void setupBoard() {
        controller = getController();
        chessboard = new Chessboard(containerActivity, inflatedView, controller);
        chessboard.drawBoard();
    }

    /**
     * Returns a controller with the appropriate starting board.
     * @return Game controller.
     */
    public GameController getController() {
        return new GameController();
    }

    /**
     * Holds on click functions for each button in the layout.
     * @param view view that was clicked
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.undo_button) {
            setupBoard();
        }
    }
}
