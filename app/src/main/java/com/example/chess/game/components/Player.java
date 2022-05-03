/**
 * @author: Jaygee Galvez
 * @description: Class to contain information of a player of Chess
 */
package com.example.chess.game.components;

public class Player {
    private String name;

    /**
     * Default constructor
     * @param name String name of the player
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Returns the name of a player
     * @return String which is the player's name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns if an object is equal to the player
     * @param o Object to compare too
     * @return boolean if the object is the same player
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Player && this.toString().equals(o.toString());
    }
}