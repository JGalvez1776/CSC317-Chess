package com.example.chess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.chess.game.GameController;

import java.util.HashMap;

public class PuzzleFragment extends GameFragment {

    private static final int LAYOUT = R.layout.fragment_game;

    private AppCompatActivity containerActivity;
    private View inflatedView;
    private GameController controller;
    private HashMap<String,Integer> pieceMap;

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
        // TODO: implement undo button toggle
        inflatedView.findViewById(R.id.undo_button).setOnClickListener(this);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.current_turn)).setText("");

        // create the game controller
        controller = setupBoard();
        pieceMap = createPieceMap();
        updateBoard(controller, pieceMap, inflatedView);

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

}
