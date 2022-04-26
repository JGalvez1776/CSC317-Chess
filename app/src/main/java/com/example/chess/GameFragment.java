package com.example.chess;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    protected AppCompatActivity containerActivity;
    protected View inflatedView;
    protected GameController controller;
    protected HashMap<String,Integer> pieceMap;
    protected Chessboard chessboard;

    // for animation
    protected ImageView selectedPiece;
    // 0 = drawable id, 1 = x, 2 = y
    protected int selectedPieceVals[] = new int[3];
    // 0 = ui x, 1 = ui y
    protected int selectedPiecePos[] = new int[2];

    // board ui
    View[][][] drawnBoard = new View[8][8][2];

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
        controller = getController();
        pieceMap = createPieceMap();
        drawBoard();
        //updateBoard(controller, pieceMap, inflatedView, containerActivity);
        //addListeners();

        return inflatedView;
    }

    protected void addListeners() {
        // add listener for board squares
        GridView gridView = inflatedView.findViewById(R.id.board);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // TODO: click on board square functionality
                int x = getX(position);
                int y = getY(position);
                String name = controller.getPieceName(x, y);
                int result = controller.select(x, y);

                // animation helper values
                ImageView clone = inflatedView.findViewById(R.id.piece_clone);

                switch (result) {
                    case GameController.NOTHING_SELECTED:
                        // TODO: Remove highlighted squares
                        updateBoard(controller, pieceMap, inflatedView, containerActivity);
                        break;
                    case GameController.PIECE_SELECTED:
                        // highlights piece
                        TextView square = v.findViewById(R.id.square);
                        square.setBackgroundColor(getResources().getColor(R.color.yellow));

                        // gets selected piece values
                        selectedPiece = v.findViewById(R.id.piece);
                        selectedPieceVals[0] = pieceMap.get(name);
                        selectedPieceVals[1] = x; selectedPieceVals[2] = y;

                        // get position onscreen
                        square.getLocationInWindow(selectedPiecePos);
                        System.out.println(selectedPiecePos[0]+" "+selectedPiecePos[1]);

                        // TODO: Highlight potential moves
                        break;
                    case GameController.PIECE_MOVED:
                        animatePiece(clone, x, y);

                        // TODO: Handle game logistics (Check, game over, update turn string)
                        break;
                }
            }
        });

    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void animatePiece(ImageView clone, int x, int y) {
        // hides original, initializes clone
        selectedPiece.setAlpha(0.0f);
        clone.setAlpha(1.0f);
        clone.setImageDrawable(getResources().getDrawable(selectedPieceVals[0]));
        clone.setX(selectedPiecePos[0]); clone.setY(selectedPiecePos[1]-63);

        // animate clone to end location
        int squareSize = dpToPx(50);
        int moveX = x-selectedPieceVals[1];
        int moveY = y-selectedPieceVals[2];

        PropertyValuesHolder pX = PropertyValuesHolder.ofFloat("translationX",
                clone.getTranslationX()+(moveX*(squareSize)));
        PropertyValuesHolder pY = PropertyValuesHolder.ofFloat("translationY",
                clone.getTranslationY()+(moveY*squareSize));
        ObjectAnimator pieceMove = ObjectAnimator.ofPropertyValuesHolder(clone,pX,pY);
        pieceMove.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                // updates board
                updateBoard(controller, pieceMap, inflatedView, containerActivity);
                clone.setAlpha(0.0f);
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        pieceMove.setDuration(1000);
        pieceMove.start();
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
    public GameController getController() {
        // TODO: implement save/load
        return new GameController();
    }

    public void drawBoard() {
        LinearLayout ll = inflatedView.findViewById(R.id.board);

        for (int x = 0; x < 8; x++) {
            // create row
            LinearLayout row = new LinearLayout(containerActivity);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(rp);
            row.setOrientation(LinearLayout.HORIZONTAL);
            ll.addView(row);

            for (int y = 0; y < 8; y++) {
                // create frame layout
                FrameLayout fl = new FrameLayout(containerActivity);

                // create piece view
                ImageView piece = new ImageView(containerActivity);

                // create cell view
                TextView cell = new TextView(containerActivity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        (int) getResources().getDimension(R.dimen.square_size),
                        (int) getResources().getDimension(R.dimen.square_size));
                cell.setLayoutParams(p);
                piece.setLayoutParams(p);

                // set cell color
                if ((y%2 != 0 && x%2 == 0) || (y%2 == 0 && x%2 != 0))
                    cell.setBackgroundColor(getResources().getColor(R.color.gray));
                else cell.setBackgroundColor(getResources().getColor(R.color.white));

                // add to frame layout, row, and drawnBoard
                fl.addView(cell);
                fl.addView(piece);
                row.addView(fl);
                drawnBoard[y][x] = new View[]{piece, cell};
            }
        }
        updateBoard();
    }

    public void updateBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                ImageView piece = (ImageView) drawnBoard[y][x][0];
                String pieceName = controller.getPieceName(y,x);
                if (pieceName != null) {
                    piece.setImageResource(pieceMap.get(pieceName));
                }
            }
        }
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

        // TODO: Might be able to use this TextView to display checkmate/check
        // Sets correct player text
        TextView playerText = inflatedView.findViewById(R.id.current_turn);
        String player = gc.getCurrentPlayer();
        int playerStringId = player.equals(GameController.WHITE) ? R.string.white_turn : R.string.black_turn;
        playerText.setText(getText(playerStringId));
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
    protected ArrayList<HashMap<String,Object>> getBoardList(GameController gc, HashMap<String,Integer> pm) {
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
    protected SimpleAdapter getAdapter(GameController gc, HashMap<String,Integer> pm, AppCompatActivity containerActivity) {
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
                int color = getResources().getColor(R.color.gray);
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
