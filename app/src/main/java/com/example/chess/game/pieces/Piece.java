package com.example.chess.game.pieces;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;

public abstract class Piece {
    protected String name;
    protected Player owner;
    protected int weight;
    private static int id = 0;

    private boolean hasMoved = false;
    

    public Piece(String name, Player owner, int weight) {
        this.name = name;
        this.owner = owner;
        this.weight = weight;
    }


    public Move[] getPotentialMoves() {
        // TODO: Implement this
        return null;
    }

    public String getName() {
        return owner + name;
    }

    public String getPlayer() {
        return owner.toString();
    }

    protected int getNextID() {
        return id++;
    }

    /**
     * Returns if the Piece has moved
     * @return Boolean which is if the piece moved.
     */
    public boolean hasMoved() { 
        return this.hasMoved;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public String toString() {
        return name;
    }

}