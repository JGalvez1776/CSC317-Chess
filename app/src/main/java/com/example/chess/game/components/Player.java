package com.example.chess.game.components;

public class Player {
    private String name;

    public Player(String name) {
        this.name = name;
        // TODO: Store player's pieces here
    }

    @Override
    public String toString() {
        return name;
    }
}