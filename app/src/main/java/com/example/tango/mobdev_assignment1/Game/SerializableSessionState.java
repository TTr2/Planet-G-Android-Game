package com.example.tango.mobdev_assignment1.Game;

import java.io.Serializable;

/**
 * Simplifies the session state that should be persisted between game sessions in to primitive
 * values so that the game board can be serialized using GSON.
 * Created by tango on 29/11/2017.
 */
public class SerializableSessionState implements Serializable {

    private int currentPlanet;
    private int level;
    private long score;
    private int[][] planets;
    private int activeColumn;
    private int activeRow;
    private float activeImageX;
    private float activeImageY;

    /**
     * Constructor for SerializableSessionState object.
     * @param session
     */
    public SerializableSessionState(GameSession session)
    {
        GameBoard gameBoard = session.getGameBoard();
        GameBoardCell activeCell = gameBoard.getActiveCell();
        int columns = session.getGameBoard().getColumns();
        int rows = session.getGameBoard().getRows();

        this.currentPlanet = session.getCurrentPlanet().getOrder();
        this.level = session.getLevel();
        this.score = session.getScore();
        this.activeColumn = activeCell.getColumn();
        this.activeRow = activeCell.getRow();
        this.activeImageX = activeCell.getImageX();
        this.activeImageY = activeCell.getImageY();
        this.planets = new int[columns][rows];

        for (int column = 0; column < columns; column++)
        {
            for (int row = 0; row < rows; row++)
            {
                this.planets[column][row] = gameBoard.getCell(column, row).getPlanet().getOrder();
            }
        }
    }

    /**
     * Default constructor for Serializing purposes.
     */
    public SerializableSessionState(){}

    /* Getters */
    protected int getCurrentPlanet() { return currentPlanet; }
    protected int getLevel() { return level; }
    protected long getScore() { return score; }
    protected int[][] getPlanets() { return planets; }
    protected int getActiveColumn() { return activeColumn; }
    protected int getActiveRow() { return activeRow; }
    protected float getActiveImageX() { return activeImageX; }
    protected float getActiveImageY() { return activeImageY; }
}

