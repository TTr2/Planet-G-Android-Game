package com.example.tango.mobdev_assignment1.Game;

import java.io.Serializable;

/**
 * A game session object for storing the state of an individual game session.
 * Created by tango on 20/11/2017.
 */

class GameSession implements Serializable {

    private GameBoard gameBoard;
    private PlanetsEnum currentPlanet;
    private int level;
    private long score;

    /**
     * Constructor for a com.example.tango.mobdev_assignment1.Game.GameSession, that stores the details for a single play session.
     * @param gameBoard the game board.
     * @param startingPlanet the starting planet.
     */
    protected GameSession(GameBoard gameBoard, PlanetsEnum startingPlanet){
        this.gameBoard = gameBoard;
        this.currentPlanet = startingPlanet;
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
     * Gets the session's game board.
     * @return the session's game board.
     */
    public GameBoard getGameBoard() { return gameBoard; }

    /**
     * Sets the session's game board when pausing and resuming.
     * @param gameBoard the game session's game board.
     */
    public void setGameBoard(GameBoard gameBoard) { this.gameBoard = gameBoard; }

    /**
     * Gets the current planet that new blocks compare their gravity to.
     * @return the current planet enum.
     */
    protected PlanetsEnum getCurrentPlanet() { return this.currentPlanet; }

    /**
     * Sets the current planet that new blocks compare their gravity to.
     * @param currentPlanet the new current planet.
     */
    protected void setCurrentPlanet(PlanetsEnum currentPlanet) {
        this.currentPlanet = currentPlanet;
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
}
