package com.example.chess;

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

import java.util.HashMap;

public class PuzzleFragment extends GameFragment {

    private static final int LAYOUT = R.layout.fragment_game;

    private AppCompatActivity containerActivity;
    private View inflatedView;
    private GameController controller;
    private HashMap<String,Integer> pieceMap;

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
        // TODO: implement undo button toggle
        inflatedView.findViewById(R.id.undo_button).setOnClickListener(this);

        // update view to fit game mode
        ((TextView) inflatedView.findViewById(R.id.current_turn)).setText("");

        // create the game controller
        controller = setupBoard();
        pieceMap = createPieceMap();
        updateBoard(controller, pieceMap, inflatedView, containerActivity);

        // add listener for board squares
        GridView gridView = inflatedView.findViewById(R.id.board);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // TODO: click on board square functionality
                System.out.println(getX(position)+" "+getY(position));
            }
        });

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
