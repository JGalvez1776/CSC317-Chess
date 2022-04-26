package com.example.chess;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chess.R;
import com.example.chess.game.GameController;

import java.util.HashMap;

public class Chessboard {
    // board ui
    View[][][] drawnBoard = new View[8][8][2];

    protected AppCompatActivity containerActivity;
    protected GameController controller;
    protected HashMap<String,Integer> pieceMap;

    int squareSize; int colorDark; int colorLight;

    public Chessboard(AppCompatActivity containerActivity, GameController controller) {
        this.containerActivity = containerActivity;
        this.controller = controller;
        squareSize = (int) containerActivity.getResources().getDimension(R.dimen.square_size);
        colorDark = containerActivity.getResources().getColor(R.color.gray);
        colorLight = containerActivity.getResources().getColor(R.color.white);
        pieceMap = createPieceMap();
    }

    public void drawBoard() {
        LinearLayout ll = containerActivity.findViewById(R.id.board);

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
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(squareSize, squareSize);
                cell.setLayoutParams(p);
                piece.setLayoutParams(p);

                // set cell color
                if ((y%2 != 0 && x%2 == 0) || (y%2 == 0 && x%2 != 0))
                    cell.setBackgroundColor(colorDark);
                else cell.setBackgroundColor(colorLight);

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

}
