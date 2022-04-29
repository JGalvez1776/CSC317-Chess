package com.example.chess.game.pieces;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;

import java.util.ArrayList;
import java.util.List;

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


    public abstract List<Move> getPotentialMoves();

    public String getName() {
        return owner + name;
    }

    public Player getPlayer() {
        return owner;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Piece)) return false;
        Piece piece = (Piece) o;
        if ((this.name.equals(piece.name))
        && (this.owner.equals(piece.owner))) {
            return true;
        }
        return false;
    }

}