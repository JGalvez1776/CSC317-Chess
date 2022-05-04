/*
 * @author: Jaygee Galvez
 * @description: Provides an interface to allow the constructor for a piece to be stored.
 */
package com.example.chess.game.pieces;

import com.example.chess.game.components.Player;

public interface Placeable {
    public Piece create(Player player);
}
