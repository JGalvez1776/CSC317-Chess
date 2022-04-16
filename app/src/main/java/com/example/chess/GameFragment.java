package com.example.chess;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.chess.game.GameController;

import java.util.ArrayList;
import java.util.HashMap;

public class GameFragment extends Fragment implements View.OnClickListener {

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
        ((TextView) inflatedView.findViewById(R.id.attempts_count)).setText("");
        ((TextView) inflatedView.findViewById(R.id.move_feedback)).setText("");

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
                int x = getX(position);
                int y = getY(position);
                System.out.println(x + " " + y + " " + controller.getPieceName(x, y));
                int result = controller.select(x, y);

                switch (result) {
                    case GameController.NOTHING_SELECTED:
                        // TODO: Remove highlighted squares
                        break;
                    case GameController.PIECE_SELECTED:
                        // TODO: Highlight selected squares and potential moves
                        break;
                    case GameController.PIECE_MOVED:
                        updateBoard(controller, pieceMap, inflatedView, containerActivity);
                        // TODO: Handle game logistics (Check, game over, update turn string)
                        break;
                }
            }
        });

        return inflatedView;
    }

    /**
     * Creates a map of piece strings to their respective drawable ids.
     * @return Map of pieces.
     */
    public HashMap<String,Integer> createPieceMap() {
        HashMap<String,Integer> pMap = new HashMap<String,Integer>();
        pMap.put("blackbishop",R.drawable.blackbishop); pMap.put("whitebishop",R.drawable.whitebishop);
        pMap.put("blackking",R.drawable.blackking); pMap.put("whiteking",R.drawable.whiteking);
        pMap.put("blackknight",R.drawable.blackknight); pMap.put("whiteknight",R.drawable.whiteknight);
        pMap.put("blackpawn",R.drawable.blackpawn); pMap.put("whitepawn",R.drawable.whitepawn);
        pMap.put("blackqueen",R.drawable.blackqueen); pMap.put("whitequeen",R.drawable.whitequeen);
        pMap.put("blackrook",R.drawable.blackrook); pMap.put("whiterook",R.drawable.whiterook);
        return pMap;
    }

    /**
     * Returns a controller with the appropriate starting board.
     * @return Game controller.
     */
    public GameController setupBoard() {
        // TODO: implement save/load
        return new GameController();
    }

    /**
     * Updates the board UI based on the real board in the controller.
     * @param gc GameController to update from
     * @param pm piece map to use
     * @param inflatedView current fragment view
     * @param containerActivity activity that contains this fragment
     */
    public void updateBoard(GameController gc, HashMap<String,Integer> pm, View inflatedView, AppCompatActivity containerActivity) {
        // set adapter and listener
        GridView gridView = inflatedView.findViewById(R.id.board);
        gridView.setAdapter(getAdapter(gc, pm, containerActivity));
    }

    /**
     * Gets x coordinate based on raw position.
     * @param position raw position
     * @return X coordinate.
     */
    public int getX(int position) {
        return position%8;
    }

    /**
     * Gets y coordinate based on raw position.
     * @param position raw position
     * @return Y coordinate.
     */
    public int getY(int position) {
        return position/8;
    }

    /**
     * Gets board list for simple adapter.
     * @param gc GameController to update from
     * @param pm piece map to use
     * @return List of mappings of square colors and piece names.
     */
    private ArrayList<HashMap<String,Object>> getBoardList(GameController gc, HashMap<String,Integer> pm) {
        // board list including square color and piece images
        ArrayList<HashMap<String,Object>> boardList = new ArrayList<HashMap<String,Object>>();

        // piece images array
        int[] pieceImages = new int[64];
        for (int p = 0; p < 64; p++) {
            int x = getX(p); int y = getY(p);
            String name = gc.getPieceName(x,y);
            if (name == null) pieceImages[p] = R.drawable.blank;
            else pieceImages[p] = pm.get(gc.getPieceName(x,y));
        }

        // square colors array
        String[] squareColors = new String[64];
        boolean invert = false;
        for (int s = 0; s < 64; s++) {
            if (s%8 == 0) invert = !invert;
            if (((s%2 == 0) && invert) || ((s%2 != 0) && !invert)) {
                squareColors[s] = "W";
            } else squareColors[s] = "B";
        }

        // add to board list
        for (int i = 0; i < Math.min(pieceImages.length,squareColors.length); i++) {
            HashMap<String,Object> item = new HashMap<>();
            item.put("piece",pieceImages[i]); item.put("square",squareColors[i]);
            boardList.add(item);
        }
        return boardList;
    }

    /**
     * Gets simple adapter needed to update board.
     * @param gc GameController to update from
     * @param pm piece map to use
     * @param containerActivity activity that contains this fragment
     * @return SimpleAdapter to update board with.
     */
    private SimpleAdapter getAdapter(GameController gc, HashMap<String,Integer> pm, AppCompatActivity containerActivity) {
        // get list for simple adapter
        ArrayList<HashMap<String,Object>> boardList = getBoardList(gc, pm);

        // create simple adapter
        String[] type = {"piece","square"};
        int into[] = {R.id.piece,R.id.square};
        SimpleAdapter simpleAdapter = new SimpleAdapter(containerActivity,
                boardList, R.layout.board_square,type,into) {
            /**
             * Updates grid cell color (for checkerboard pattern)
             * @param position cell position
             * @param convertView convert view
             * @param parent parent view group
             * @return Board square view object.
             */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView square = view.findViewById(R.id.square);
                int color = getResources().getColor(R.color.gray); // something idk
                if (square.getText().toString().equals("W")) {
                    color = getResources().getColor(R.color.white);
                }
                square.setBackgroundColor(color);
                square.setText("");
                return view;
            }
        };
        return simpleAdapter;
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
