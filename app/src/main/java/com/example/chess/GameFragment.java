package com.example.chess;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chess.game.GameController;

import java.util.ArrayList;
import java.util.HashMap;

public class GameFragment extends Fragment implements View.OnClickListener {

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

        setupBoard();

        return inflatedView;
    }

    public void setupBoard() {
        // create the game controller
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
        FragmentTransaction transaction = containerActivity.
                getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.undo_button:
                setupBoard();
                break;
        }
    }
}
