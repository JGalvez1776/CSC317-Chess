/*
 * @author: Min Tran
 * @description: This fragment handles the drawing of the chessboard.
 */

package com.example.chess;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import com.example.chess.game.GameController;
import com.example.chess.game.PuzzleGameController;

import java.util.HashMap;

public class Chessboard {
    // chessboard views, with cell and piece view
    View[][][] drawnBoard = new View[8][8][2];

    // currently selected square
    int[] selected = new int[2];

    // map of piece names to drawable
    private static final HashMap<String, Integer> drawMap;
    static {
        drawMap = new HashMap<>();
        drawMap.put("blackbishop",R.drawable.blackbishop);
        drawMap.put("whitebishop",R.drawable.whitebishop);
        drawMap.put("blackking",R.drawable.blackking);
        drawMap.put("whiteking",R.drawable.whiteking);
        drawMap.put("blackknight",R.drawable.blackknight);
        drawMap.put("whiteknight",R.drawable.whiteknight);
        drawMap.put("blackpawn",R.drawable.blackpawn);
        drawMap.put("whitepawn",R.drawable.whitepawn);
        drawMap.put("blackqueen",R.drawable.blackqueen);
        drawMap.put("whitequeen",R.drawable.whitequeen);
        drawMap.put("blackrook",R.drawable.blackrook);
        drawMap.put("whiterook",R.drawable.whiterook);
    }

    // fragment and game variables
    protected AppCompatActivity containerActivity;
    protected View inflatedView;
    protected GameController controller;

    // drawing and animation variables
    public static final int SELECT = 0;
    public static final int AVAILABLE = 1;
    public static final int ANIMATION_SPEED = 500;
    boolean animate; int squareSize;
    int colorDark; int colorLight; int colorSelect;
    int colorHighlight; int colorHighlightDark;

    /**
     * Chessboard constructor.
     * @param containerActivity activity view is contained in
     * @param inflatedView view to draw on
     * @param controller game controller
     */
    public Chessboard(AppCompatActivity containerActivity, View inflatedView,
                      GameController controller) {
        // set fragment and game variables
        this.containerActivity = containerActivity;
        this.inflatedView = inflatedView;
        this.controller = controller;

        // set animation preference
        SharedPreferences sharedPref =
                containerActivity.getPreferences(Context.MODE_PRIVATE);
        animate = sharedPref.getInt("animate", 1) == 1;

        // get drawing and animation variables
        squareSize = Math.min(getWidthInPixels(),
                (int) (getHeightInPixels()-(getHeightInPixels()*0.30)))/8;
        colorDark = getThemeColor("colorPrimaryDark");
        colorLight = getThemeColor("colorPrimary");
        colorSelect = getThemeColor("colorSecondary");
        colorHighlight = getThemeColor("colorTertiary");
        colorHighlightDark = getThemeColor("colorOnTertiary");
    }

    /**
     * Returns the color id of a color given the color name.
     * @param name color name
     * @return Color id.
     */
    public int getThemeColor(String name){
        TypedValue outValue = new TypedValue();
        int colorAttr = containerActivity.getResources().getIdentifier
                (name, "attr", containerActivity.getPackageName());
        containerActivity.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    /**
     * Draws the board based on the current state of the game using FrameLayouts with a
     * TextView (cell) and ImageView (piece) within a RelativeLayout.
     */
    public void drawBoard() {
        // relative layout and frame layout grid
        RelativeLayout rl = inflatedView.findViewById(R.id.board);
        FrameLayout[][] fls = new FrameLayout[8][8];

        // creates frame layout for each square
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                FrameLayout fl = drawSquare(x,y);
                fls[x][y] = fl;

                // adds frame layout to relative layout
                if (x == 0 && y == 0) rl.addView(fl);
                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(squareSize, squareSize);
                if (x > 0) lp.addRule(RelativeLayout.RIGHT_OF, fls[x-1][y].getId());
                if (y > 0) lp.addRule(RelativeLayout.BELOW, fls[x][y-1].getId());
                if(fl.getParent() != null) ((ViewGroup)fl.getParent()).removeView(fl);
                rl.addView(fl, lp);
            }
        }
        updateBoard();
    }

    /**
     * Draws a single square on the chessboard.
     * @param x x coordinate of square to draw
     * @param y y coordinate of square to draw
     * @return FrameLayout with cell and piece views.
     */
    private FrameLayout drawSquare(int x, int y) {
        // create frame layout
        FrameLayout fl = new FrameLayout(containerActivity);
        setListener(fl,x,y);

        // create piece view
        ImageView piece = new ImageView(containerActivity);
        piece.setImageTintMode(android.graphics.PorterDuff.Mode.MULTIPLY);
        ImageViewCompat.setImageTintList
                (piece, ColorStateList.valueOf(getThemeColor("colorPrimary")));

        // create cell view
        TextView cell = new TextView(containerActivity);
        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(squareSize, squareSize);
        cell.setLayoutParams(p); piece.setLayoutParams(p);
        cell.setBackgroundColor(getCellColor(x,y));

        // add to frame layout, row, and drawnBoard
        fl.addView(cell); fl.addView(piece);
        fl.setClipChildren(false); fl.setId(View.generateViewId());
        drawnBoard[x][y] = new View[]{piece, cell};
        return fl;
    }

    /**
     * Gets appropriate cell color at given coordinates.
     * @param x x coordinate of square
     * @param y y coordinate of square
     * @return Color id.
     */
    public int getCellColor(int x, int y) {
        if ((y%2 != 0 && x%2 == 0) || (y%2 == 0 && x%2 != 0))
            return colorDark;
        else return colorLight;
    }

    /**
     * Sets listener for a view for the given coordinates.
     * @param view view to set listener
     * @param x x coordinate assigned to view
     * @param y y coordinate assigned to view
     */
    protected void setListener(View view, int x, int y) {
        view.setOnClickListener(v -> {
            int result = controller.select(x, y);
            switch (result) {
                case GameController.NOTHING_SELECTED:
                    // remove highlights
                    updateBoard();
                    break;
                case GameController.PIECE_SELECTED:
                    // remove and set highlight
                    updateBoard();
                    selected[0] = x; selected[1] = y;
                    setHighlight(selected[0],selected[1], SELECT);
                    // highlight available moves
                    for (int[] move : controller.getAvailableMoves()) {
                        setHighlight(move[0],move[1], AVAILABLE);
                    }
                    break;
                case GameController.PIECE_MOVED:
                    // animate piece (if applicable) and update board
                    if (animate) animatePiece(selected[0],selected[1],x,y,ANIMATION_SPEED,
                            controller instanceof PuzzleGameController);
                    else animatePiece(selected[0],selected[1],x,y,0,
                            controller instanceof PuzzleGameController);
                    break;
            }
        });
    }

    /**
     * Animates a piece from the starting position to the ending position.
     * Animates computer move (if applicable).
     * @param startX starting x coordinate
     * @param startY starting y coordinate
     * @param endX ending x coordinate
     * @param endY ending y coordinate
     * @param speed animation speed
     * @param comp whether or not to do computer move
     */
    private void animatePiece(int startX, int startY, int endX, int endY, int speed, boolean comp) {
        // get piece and bring to front
        ImageView piece = (ImageView) drawnBoard[startX][startY][0];
        FrameLayout fl = (FrameLayout) piece.getParent();
        fl.bringToFront();

        // setup object animator
        int moveX = endX-startX; int moveY = endY-startY;
        if (!animate) { moveX = 0; moveY = 0; }
        PropertyValuesHolder pX = PropertyValuesHolder.ofFloat("translationX",
                piece.getTranslationX()+(moveX*(squareSize)));
        PropertyValuesHolder pY = PropertyValuesHolder.ofFloat("translationY",
                piece.getTranslationY()+(moveY*squareSize));
        ObjectAnimator pieceMove = ObjectAnimator.ofPropertyValuesHolder(piece,pX,pY);
        pieceMove.setDuration(speed);
        setupAnimatorListener(pieceMove,comp);
        pieceMove.start();
    }

    /**
     * Sets up listener for animator which controls computer movement animation.
     * @param animator animator to add listener to
     * @param comp whether or not to do computer move
     */
    private void setupAnimatorListener(ObjectAnimator animator, boolean comp) {
        animator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                // updates board
                updateBoard();
                if (comp && controller instanceof PuzzleGameController && !controller.gameOver) {
                    PuzzleGameController puzzleGameController = (PuzzleGameController) controller;
                    int[] compMove = puzzleGameController
                            .getMoveAnimation(puzzleGameController.doComputerMove());
                    if (!animate) animatePiece(compMove[0], compMove[1], compMove[2], compMove[3],
                            500, false);
                    else animatePiece(compMove[0], compMove[1], compMove[2], compMove[3],
                            ANIMATION_SPEED, false);
                }
            }
        });
    }

    /**
     * Sets highlight of board square given its coordinates.
     * @param x x coordinate of square
     * @param y y coordinate of square
     * @param mode determines whether to highlight yellow or green
     */
    public void setHighlight(int x, int y, int mode) {
        TextView cell = (TextView) drawnBoard[x][y][1];
        if (mode == SELECT) {
            cell.setBackgroundColor(colorSelect);
        } else if (mode == AVAILABLE) {
            int id = getCellColor(x,y);
            if (id == colorDark) {
                cell.setBackgroundColor(colorHighlightDark);
            } else cell.setBackgroundColor(colorHighlight);
        }

    }

    /**
     * Updates the board according to the game controller.
     */
    @SuppressLint("SetTextI18n")
    @SuppressWarnings("ConstantConditions")
    public void updateBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // set piece at location
                ImageView piece = (ImageView) drawnBoard[x][y][0];
                piece.animate().translationX(0).translationY(0);
                String pieceName = controller.getPieceName(x,y);
                if (pieceName != null) {
                    piece.setImageResource(drawMap.get(pieceName));
                } else piece.setImageResource(R.color.transparent);

                // clear highlight
                drawnBoard[x][y][1].setBackgroundColor(getCellColor(x,y));
            }
        }
        updateState();
    }

    /**
     * Updates UI with state of the board.
     */
    @SuppressLint("SetTextI18n")
    protected void updateState() {
        // set current turn
        TextView playerText = inflatedView.findViewById(R.id.current_turn);
        String player = controller.getCurrentPlayer();
        int playerStringId = player.equals(GameController.WHITE) ? R.string.white_turn : R.string.black_turn;
        playerText.setText(playerStringId);

        // set current check
        TextView checkText = inflatedView.findViewById(R.id.current_check);
        String check = controller.getCurrentCheck();
        if (check != null) checkText.setText(check+" is in check!");
        else checkText.setText("");

        // check for winner
        String winner = controller.checkWinner();
        if (winner != null) {
            controller.endGame();
            playerText.setText(winner.toUpperCase()+" WINS");
        }

        // update move feedback and check if puzzle is completed
        if (controller instanceof PuzzleGameController) {
            PuzzleGameController puzzleController = (PuzzleGameController) controller;
            TextView feedbackText = inflatedView.findViewById(R.id.move_feedback);
            if (puzzleController.gameOver) {
                if (puzzleController.correct) playerText.setText("PUZZLE COMPLETED");
                else feedbackText.setText("That move is incorrect!");
            } else feedbackText.setText("");
        }
    }

    /**
     * Returns the height of the screen in pixels.
     * @return Height of screen in pixels.
     */
    protected int getHeightInPixels() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        containerActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Returns the width of the screen in pixels.
     * @return Width of screen in pixels.
     */
    protected int getWidthInPixels() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        containerActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}
