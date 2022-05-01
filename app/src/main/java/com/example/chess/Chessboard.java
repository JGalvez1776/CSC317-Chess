package com.example.chess;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.example.chess.game.GameController;
import com.example.chess.game.PuzzleGameController;

import java.util.HashMap;

public class Chessboard {
    // board ui
    View[][][] drawnBoard = new View[8][8][2];
    int[] selected = new int[2];

    protected AppCompatActivity containerActivity;
    protected View inflatedView;
    protected GameController controller;
    protected HashMap<String,Integer> pieceMap;

    int squareSize; int animSpeed;
    int colorDark; int colorLight; int colorHighlight;

    public Chessboard(AppCompatActivity containerActivity, View inflatedView, GameController controller) {
        this.containerActivity = containerActivity;
        this.inflatedView = inflatedView;
        this.controller = controller;

        squareSize = Math.min(getWidthInPixels(),getHeightInPixels())/8;

        colorDark = getThemeColor("colorPrimary");
        colorLight = getThemeColor("colorSecondary");
        colorHighlight = getThemeColor("colorTertiary");;

        pieceMap = createPieceMap();
        animSpeed = 500;
    }

    public int getThemeColor(String name){
        TypedValue outValue = new TypedValue();
        int colorAttr = containerActivity.getResources().getIdentifier
                (name, "attr", containerActivity.getPackageName());
        containerActivity.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public void drawBoard() {
        RelativeLayout rl = inflatedView.findViewById(R.id.board);
        FrameLayout[][] fls = new FrameLayout[8][8];

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // create frame layout
                FrameLayout fl = new FrameLayout(containerActivity);
                setListener(fl,x,y);

                // create piece view
                ImageView piece = new ImageView(containerActivity);
                piece.setImageTintMode(android.graphics.PorterDuff.Mode.MULTIPLY);
                ImageViewCompat.setImageTintList
                        (piece, ColorStateList.valueOf(getThemeColor("colorSecondary")));

                // create cell view
                TextView cell = new TextView(containerActivity);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(squareSize, squareSize);
                cell.setLayoutParams(p);
                piece.setLayoutParams(p);

                // set cell color
                cell.setBackgroundColor(getCellColor(x,y));

                // add to frame layout, row, and drawnBoard
                fl.addView(cell);
                fl.addView(piece);
                fl.setClipChildren(false);
                fl.setId(View.generateViewId());
                fls[x][y] = fl;
                drawnBoard[x][y] = new View[]{piece, cell};

                // add frame layout to relative layout
                if (x == 0 && y == 0) rl.addView(fl);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(squareSize, squareSize);
                if (x > 0) lp.addRule(RelativeLayout.RIGHT_OF, fls[x-1][y].getId());
                if (y > 0) lp.addRule(RelativeLayout.BELOW, fls[x][y-1].getId());
                if(fl.getParent() != null) {
                    ((ViewGroup)fl.getParent()).removeView(fl);
                }
                rl.addView(fl, lp);
            }
        }
        updateBoard();
    }

    public int getCellColor(int x, int y) {
        if ((y%2 != 0 && x%2 == 0) || (y%2 == 0 && x%2 != 0))
            return colorDark;
        else return colorLight;
    }

    protected void setListener(View view, int x, int y) {
        view.setOnClickListener(v -> {
            int result = controller.select(x, y);

            switch (result) {
                case GameController.NOTHING_SELECTED:
                    setHighlight(selected[0],selected[1],false);
                    break;
                case GameController.PIECE_SELECTED:
                    setHighlight(selected[0],selected[1],false);
                    selected[0] = x; selected[1] = y;
                    setHighlight(selected[0],selected[1],true);
                    break;
                case GameController.PIECE_MOVED:
                    SharedPreferences sharedPref = containerActivity.getPreferences(Context.MODE_PRIVATE);
                    if (sharedPref.getInt("animate",1) == 1) {
                        if (controller instanceof PuzzleGameController)
                            animatePiece(selected[0],selected[1],x,y,true);
                        else animatePiece(selected[0],selected[1],x,y,false);
                    } else updateBoard();
                    break;
            }
        });
    }

    private void animatePiece(int startX, int startY, int endX, int endY, boolean comp) {
        System.out.println("animation start "+startX+" "+startY+" to "+endX+" "+endY);
        ImageView piece = (ImageView) drawnBoard[startX][startY][0];
        FrameLayout fl = (FrameLayout) piece.getParent();
        fl.bringToFront();
        int moveX = endX-startX;
        int moveY = endY-startY;

        PropertyValuesHolder pX = PropertyValuesHolder.ofFloat("translationX",
                piece.getTranslationX()+(moveX*(squareSize)));
        PropertyValuesHolder pY = PropertyValuesHolder.ofFloat("translationY",
                piece.getTranslationY()+(moveY*squareSize));
        ObjectAnimator pieceMove = ObjectAnimator.ofPropertyValuesHolder(piece,pX,pY);
        pieceMove.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                // updates board
                System.out.println("animation end");
                updateBoard();
                if (comp) {
                    if (controller instanceof PuzzleGameController) {
                        int[] compMove = ((PuzzleGameController) controller).doComputerMove();
                        System.out.println(compMove[0]+" "+compMove[1]+" "+compMove[2]+" "+compMove[3]);
                        animatePiece(compMove[0],compMove[1],compMove[2],compMove[3],false);
                    }
                }
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        pieceMove.setDuration(animSpeed);
        pieceMove.start();
    }

    public void setHighlight(int x, int y, boolean highlight) {
        TextView cell = (TextView) drawnBoard[x][y][1];
        if (highlight) {
            cell.setBackgroundColor(colorHighlight);
        } else {
            cell.setBackgroundColor(getCellColor(x,y));
        }
    }

    public void updateBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // set piece at location
                ImageView piece = (ImageView) drawnBoard[x][y][0];
                piece.animate().translationX(0).translationY(0);
                String pieceName = controller.getPieceName(x,y);
                if (pieceName != null) {
                    piece.setImageResource(pieceMap.get(pieceName));
                } else piece.setImageResource(R.color.transparent);

                // clear highlight
                drawnBoard[x][y][1].setBackgroundColor(getCellColor(x,y));
            }
        }

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
        if (winner != null) playerText.setText(winner+" wins!");

        // update move feedback
        if (controller instanceof PuzzleGameController) {
            PuzzleGameController puzzleController = (PuzzleGameController) controller;
            TextView feedbackText = inflatedView.findViewById(R.id.move_feedback);
            if (puzzleController.gameOver && !puzzleController.correct) {
                feedbackText.setText("That move is incorrect!");
            } else if (puzzleController.gameOver && puzzleController.correct) {
                playerText.setText("PUZZLE COMPLETED");
            } else feedbackText.setText("");
        }
        System.out.println("update finished");
    }

    /**
     * Creates a map of piece strings to their respective drawable ids.
     * @return Map of pieces.
     */
    public HashMap<String,Integer> createPieceMap() {
        HashMap<String,Integer> pMap = new HashMap<>();
        pMap.put("blackbishop",R.drawable.blackbishop); pMap.put("whitebishop",R.drawable.whitebishop);
        pMap.put("blackking",R.drawable.blackking); pMap.put("whiteking",R.drawable.whiteking);
        pMap.put("blackknight",R.drawable.blackknight); pMap.put("whiteknight",R.drawable.whiteknight);
        pMap.put("blackpawn",R.drawable.blackpawn); pMap.put("whitepawn",R.drawable.whitepawn);
        pMap.put("blackqueen",R.drawable.blackqueen); pMap.put("whitequeen",R.drawable.whitequeen);
        pMap.put("blackrook",R.drawable.blackrook); pMap.put("whiterook",R.drawable.whiterook);
        return pMap;
    }

    protected int getHeightInPixels() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        containerActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    protected int getWidthInPixels() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        containerActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

}
