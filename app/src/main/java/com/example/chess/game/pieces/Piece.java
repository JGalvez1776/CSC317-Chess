/*
 * @author: Jaygee Galvez
 * @description: Abstract class that implements functionality of
 *               a basic chess piece. Classes that implement
 *               must implement how a piece moves.
 */
package com.example.chess.game.pieces;

import com.example.chess.game.components.Move;
import com.example.chess.game.components.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    protected String name;
    protected Player owner;
    protected int weight;

    private boolean hasMoved = false;


    /**
     * Default constructor of a piece
     * @param name String name of the piece
     * @param owner Player who owns the piece
     * @param weight How many points the piece has
     */
    public Piece(String name, Player owner, int weight) {
        this.name = name;
        this.owner = owner;
        this.weight = weight;
    }


    /**
     * Returns all moves a piece can make
     * @return List of all moves the piece can make
     */
    public abstract List<Move> getPotentialMoves();

    /**
     * Getter for piece name
     * @return String piece name
     */
    public String getName() {
        return owner + name;
    }

    /**
     * Returns piece's owner
     * @return String piece's owner
     */
    public Player getPlayer() {
        return owner;
    }


    /**
     * Returns if the Piece has moved
     * @return Boolean which is if the piece moved.
     */
    public boolean hasMoved() { 
        return this.hasMoved;
    }

    /**
     * Sets that a piece has moved
     */
    public void setMoved() {
        this.hasMoved = true;
    }

    /**
     * Returns the name of a piece
     * @return String name of the piece (Type)
     */
    public String toString() {
        return name;
    }

    /**
     * Returns if an object is the same piece
     * @param o Object to compare
     * @return Boolean if the object is the same piece
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Piece)) return false;
        Piece piece = (Piece) o;
        if ((this.name.equals(piece.name))
        && (this.owner.equals(piece.owner))) {
            return true;
        }
        return false;
    }

}