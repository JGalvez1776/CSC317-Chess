/*
 * @author: Jaygee Galvez
 * @description: Provides way to make types of moves for pieces
 */
package com.example.chess.game.components;

public class Move {
    private int xShift;
    private int yShift;
    private boolean isRepeatable;
    private boolean canCapture;

    /**
     * Creates a move that can move in a given direction repeatably
     * @param x Shift in x direction the piece moves
     * @param y Shift in y direction the piece moves
     */
    public Move(int x, int y) {
        this(x, y, true, true);
    }

    /**
     * Creates a move that can move in a given direction repeatably
     * @param x Shift in x direction the piece moves
     * @param y Shift in y direction the piece moves
     * @param isRepeatable If the move can repeat
     * @param canCapture If the move can capture while moving
     */
    public Move(int x, int y, boolean isRepeatable, boolean canCapture) {
        this.xShift = x;
        this.yShift = y;
        this.isRepeatable = isRepeatable;
        this.canCapture = canCapture;
    }

    /**
     * Getter for x shift
     * @return int x shift
     */
    public int getShiftX() {
        return xShift;
    }

    /**
     * Getter for y shift
     * @return int y shift
     */
    public int getShiftY() {
        return yShift;
    }

    /**
     * Getter for if the move is repeatable
     * @return Boolean if move is repeatable
     */
    public boolean isRepeatable() {
        return isRepeatable;
    }

    /**
     * Getter for if a move can capture
     * @return Boolean if move can capture
     */
    public boolean canCapture() {
        return canCapture;
    }

    /**
     * Returns string version of a move
     * @return String of the move
     */
    @Override
    public String toString() {
        return "X: " + xShift + " Y: " + yShift + " isRepeat: " +
                isRepeatable + " canCapture: " + canCapture;
    }

}