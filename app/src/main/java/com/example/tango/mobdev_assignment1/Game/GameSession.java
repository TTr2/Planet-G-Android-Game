package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;

import java.io.Serializable;

/**
 * A game session object for storing the state of an individual game session.
 * Created by tango on 20/11/2017.
 */

public class GameSession implements Serializable {

    private GameBoard gameBoard;
    private PlanetsEnum nextPlanet;
    private int level;
    private long score;

    /**
     * Constructor for a com.example.tango.mobdev_assignment1.Game.GameSession, that stores the details for a single play session.
     * @param gameBoard the game board.
     */
    public GameSession(GameBoard gameBoard)
    {
        this.gameBoard = gameBoard;
        this.level = 1;
        this.score = 0;
    }

    /**
     * Add points to the current player score.
     * @param points the points to add to the current player score.
     */
    protected void addPointsToScore(long points) { this.score += points; }

    /**
     * Increments the current game level by 1, which affects the velocity of new blocks.
     */
    protected void incrementLevel() {
        this.level += 1;
    }

    /* Getters & Setters */

    /**
     * Sets the level.
     * @param level the level.
     */
    protected void setLevel(int level) { this.level = level; }

    /**
     * Gets the next planet to fall.
     * @return the next planet enum.
     */
    protected PlanetsEnum getNextPlanet() { return this.nextPlanet; }

    /**
     * Sets the next planet to fall.
     * @param nextPlanet the new next planet.
     */
    protected void setNextPlanet(PlanetsEnum nextPlanet) {
        this.nextPlanet = nextPlanet;
    }

    /**
     * Gets the current game level, which affects the velocity of new blocks.
     * @return the current game level.
     */
    protected int getLevel() { return this.level; }

    /**
     * Gets the current player score.
     * @return the current player score.
     */
    protected long getScore() { return this.score; }

    /**
     * gets the game board from the session.
      * @return the game board object.
     */
    public GameBoard getGameBoard() { return this.gameBoard; }
}
