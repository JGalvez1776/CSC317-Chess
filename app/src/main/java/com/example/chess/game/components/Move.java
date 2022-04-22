package com.example.chess.game.components;

public class Move {
    private int xShift;
    private int yShift;
    private boolean isRepeatable;
    private boolean canCapture;

    public Move(int x, int y) {
        this(x, y, true, true);
    }

    public Move(int x, int y, boolean isRepeatable, boolean canCapture) {
        this.xShift = x;
        this.yShift = y;
        this.isRepeatable = isRepeatable;
        this.canCapture = canCapture;
    }

    public int getShiftX() {
        return xShift;
    }

    public int getShiftY() {
        return yShift;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public boolean canCapture() {
        return canCapture;
    }

    @Override
    public String toString() {
        return "X: " + xShift + " Y: " + yShift + " isRepeat: " +
                isRepeatable + " canCapture: " + canCapture;
    }

}