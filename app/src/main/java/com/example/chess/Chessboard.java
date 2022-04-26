package com.example.chess;

import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
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
    protected View inflatedView;
    protected GameController controller;
    protected HashMap<String,Integer> pieceMap;

    int squareSize; int colorDark; int colorLight; int colorHighlight;

    public Chessboard(AppCompatActivity containerActivity, View inflatedView, GameController controller) {
        this.containerActivity = containerActivity;
        this.inflatedView = inflatedView;
        this.controller = controller;
        squareSize = (int) containerActivity.getResources().getDimension(R.dimen.square_size);
        colorDark = containerActivity.getResources().getColor(R.color.gray);
        colorLight = containerActivity.getResources().getColor(R.color.white);
        colorHighlight = containerActivity.getResources().getColor(R.color.yellow);
        pieceMap = createPieceMap();
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
            row.setOrientation(LinearLayout.VERTICAL);
            ll.addView(row);

            for (int y = 0; y < 8; y++) {
                // create frame layout
                FrameLayout fl = new FrameLayout(containerActivity);
                setListener(fl,x,y);

                // create piece view
                ImageView piece = new ImageView(containerActivity);

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
                row.addView(fl);
                drawnBoard[x][y] = new View[]{piece, cell};
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(x+" "+y);
                View square[] = drawnBoard[x][y];
                String name = controller.getPieceName(x, y);
                System.out.println(name);
                int result = controller.select(x, y);

                switch (result) {
                    case GameController.NOTHING_SELECTED:
                        updateBoard();
                        break;
                    case GameController.PIECE_SELECTED:
                        updateBoard();
                        square[1].setBackgroundColor(colorHighlight);
                        break;
                    case GameController.PIECE_MOVED:

                        // TODO: Handle game logistics (Check, game over, update turn string)
                        updateBoard();
                        break;
                }
            }
        });
    }

    public void updateBoard() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                // set piece at location
                ImageView piece = (ImageView) drawnBoard[x][y][0];
                String pieceName = controller.getPieceName(x,y);
                if (pieceName != null) {
                    piece.setImageResource(pieceMap.get(pieceName));
                } else piece.setImageResource(R.color.transparent);

                // clear highlight
                TextView cell = (TextView) drawnBoard[x][y][1];
                cell.setBackgroundColor(getCellColor(x,y));
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
