package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The GameController class which manages the game state.
 * Created by tango on 20/11/2017.
 */
public class GameController implements Runnable {

    private final int targetFrameRate = 60;
    private final int blocksPerLevel = 4;
    private float gameVelocity;
    private int blocksFallenCount;
    private boolean isGameLoopRunning;
    private boolean isPaused;
    private boolean onResume;
    private Object gameLoopThreadLock;
    private GameViewModel viewModel;
    private Context context;
    private GameBoard gameBoard;
    private PlanetsEnum startingPlanet = PlanetsEnum.EARTH;
    private GameSession gameSession;

    /**
     * Constructor for the GameController class.
     * @param viewModel the view model that abstracts the view implementation.
     */
    public GameController(GameViewModel viewModel)
    {
        this.viewModel = viewModel;
        this.context = this.viewModel.getGameLayoutContext();
        this.isGameLoopRunning = true;
        this.isPaused = false;
        this.onResume = false;
        SerializableSessionState serializedSessionState = this.viewModel.loadSessionState();
        int columns = serializedSessionState == null
                ? this.viewModel.getNumberOfColumnsSetting()
                : serializedSessionState.getPlanets().length;
        this.gameBoard = new GameBoard(this.context, columns);
        this.viewModel.addGameBoardToLayout(this.gameBoard);
        this.gameSession = new GameSession(this.gameBoard);

        if (serializedSessionState != null)
        {
            this.populateGameSessionFromSerializedValues(serializedSessionState);
        }
        this.gameVelocity = 5;
        this.gameLoopThreadLock = new Object();
        Thread gameLoopThread = new Thread(this);
        gameLoopThread.start();
        Log.i("GameController", "Game Manager constructed.");
    }

    /**
     * Drives the game loop. Borrowed the frame adjusting timer algorithm from StackOverflow user
     * Lee Fogg @ https://stackoverflow.com/questions/17847930/custom-vsync-algorithm/18070011#18070011
     */
    @Override
    public void run()
    {
        Log.i("GameController", "Game loop started.");

        int msPerFrame = 1000 / targetFrameRate;
        long currentTime = System.currentTimeMillis();
        long targetTime = currentTime + msPerFrame;
        boolean isBlockFalling, hasChangedColumn;
        PlanetsEnum nextPlanet = this.startingPlanet;
        float inputX, inputY = 0, velocity = this.gameVelocity;
        GameBoardCell tmpCell;
        this.viewModel.playBackgroundMusic();

        while (this.isGameLoopRunning)
        {
            try {

                if (this.onResume) // Don't do if resuming
                {
                    targetTime = currentTime + msPerFrame;
                    this.onResume = false;
                }
                else
                {
                    // Create new active block
                    this.gameBoard.addToGameBoard(nextPlanet);

                    // Set next planet and display
                    nextPlanet = this.getRandomPlanet();
                    this.gameSession.setNextPlanet(nextPlanet);
                    this.viewModel.setNextPlanet(nextPlanet);
                }

                this.viewModel.updateCellOnUiThread(this.gameBoard.getActiveCell());

                isBlockFalling = true;
                this.gameBoard.resetDirection();

                while (isBlockFalling && !this.isPaused)
                {
                    currentTime = System.currentTimeMillis();
                    if (currentTime >= targetTime)
                    {
                        targetTime = (currentTime + msPerFrame) - (currentTime-targetTime);
                        inputX = this.viewModel.getHorizontalMotion();

                        // Move block on Y and X axis, store current active cell for refresh on UI thread.
                        tmpCell = this.gameBoard.getActiveCell();
                        isBlockFalling = this.gameBoard.moveBlockY(tmpCell, velocity + tmpCell.getPlanet().getGravity());
                        this.viewModel.updateCellOnUiThread(tmpCell);

                        tmpCell = this.gameBoard.getActiveCell();
                        hasChangedColumn = this.gameBoard.moveBlockX(inputX);
                        if (hasChangedColumn)
                        {
                            this.viewModel.vibrate(25);
                            this.viewModel.updateCellOnUiThread(tmpCell);
                            tmpCell = this.gameBoard.getActiveCell();
                        }
                        isBlockFalling = this.gameBoard.moveBlockY(this.gameBoard.getActiveCell(), 0);

                        this.viewModel.updateCellOnUiThread(tmpCell);
                    }
                }

                if (!this.isPaused)
                {
                    // Calculate points for landing block and add to session score
                    int pointsForLanding = this.gameBoard.calculateNewBlockScore(this.gameSession.getLevel());
                    this.viewModel.makeToastForPoints(pointsForLanding);
                    this.gameSession.addPointsToScore(pointsForLanding);
                    this.viewModel.setScoreText(this.gameSession.getScore());

                    // Calculate points if blocks destroyed.
                    long points =  this.gameBoard.calculateMultiplierScore(this.gameBoard.getActiveCell())
                            * this.gameSession.getLevel();
                    while (points > 0)
                    {
                        boolean isGameBoardInFlux = true;
                        this.viewModel.makeToastForPoints(points);
                        this.viewModel.playActionSound();
                        this.gameSession.addPointsToScore(points);
                        this.viewModel.setScoreText(this.gameSession.getScore());
                        points = 0;
                        while (isGameBoardInFlux)
                        {
                            this.viewModel.vibrate(200);
                            isGameBoardInFlux = this.destroyCells(1);
                        }
                        // Check game board for new combinations
                        GameBoardCell cell;
                        for (int row = 0; row < this.gameBoard.getRows(); row++)
                        {
                            for (int column = 0; column < this.gameBoard.getColumns(); column++) {
                                cell = this.gameBoard.getCell(column, row);
                                if (cell.isOccupied())
                                {
                                    points += this.gameBoard.calculateMultiplierScore(cell) * this.gameSession.getLevel();
                                }
                            }
                        }
                    }

                    // Check whether to increment the game level
                    if (++this.blocksFallenCount % this.blocksPerLevel == 0)
                    {
                        this.levelUp();
                    }

                    if (this.gameBoard.isGameOver())
                    {
                        this.isGameLoopRunning = false;
                        this.viewModel.gameOverRoutine(this.gameSession.getScore(),
                                this.gameSession.getLevel());
                        Log.i("GameController", "Game Over!");
                    }
                }

                // Wait if game loop has been paused
                synchronized (this.gameLoopThreadLock)
                {
                    while (this.isPaused)
                    {
                        try
                        {
                            this.gameLoopThreadLock.wait();
                        }
                        catch (InterruptedException e) {}
                    }
                }
            }
            catch(Exception e)
            {
                GameBoardCell cell = this.gameBoard.getActiveCell();
                if (cell != null)
                {
                    cell.setDestroyed(true);
                    this.destroyCell(cell);
                }
                Log.e("GameController", "Exception Handled and active cell destroyed! " + e.getMessage(), e);
            }
        }

        Log.i("GameController", "Game loop ended.");
    }

    /**
     * Called by activity onResume(), if game was paused then repopulates the from paused state.
     */
    public void resumeGameLoop()
    {
        this.onResume = true;
        SerializableSessionState serializedSessionState = this.viewModel.loadSessionState();
        if (serializedSessionState != null)
        {
            this.populateGameSessionFromSerializedValues(serializedSessionState);
        }
        synchronized (gameLoopThreadLock)
        {
            this.isPaused = false;
            this.gameLoopThreadLock.notifyAll();
        }
    }

    /**
     * Called by activity onPause(), pauses the game loop and saves session state to internal storage.
     */
    public void pauseGameLoop()
    {
        synchronized (gameLoopThreadLock)
        {
            this.isPaused = true;
            this.viewModel.pauseBackgroundMusic();
            if (this.isGameLoopRunning)
            {
                try
                {
                    SerializableSessionState sessionState = new SerializableSessionState(this.gameSession);
                    Gson gson = new Gson();
                    String sessionStateString = gson.toJson(sessionState, SerializableSessionState.class);
                    this.viewModel.saveSessionState(sessionStateString);
                    this.viewModel.wipeGameBoardClean(this.gameBoard);
                }
                catch(Exception e)
                {
                    Log.d("GameController", "Failed to save game state.", e);
                }
            }
        }
    }

    /**
     * Iterates through the game board and moves blocks with no block below down screen.
     * @return whether any block is still falling.
     */
    private boolean destroyCells(float velocity)
    {
        boolean thisBlockIsStillFalling = false;
        boolean anyBlockIsStillFalling = false;
        GameBoardCell cell;
        for (int row = 0; row < this.gameBoard.getRows(); row++)
        {
            for (int column = 0; column < this.gameBoard.getColumns(); column++)
            {
                cell = this.gameBoard.getCell(column, row);

                if (cell.isDestroyed())
                {
                    this.destroyCell(cell);
                }

                if (cell.isOccupied())
                {
                    thisBlockIsStillFalling = this.gameBoard.moveBlockY(cell, velocity);
                    anyBlockIsStillFalling = thisBlockIsStillFalling ? true : anyBlockIsStillFalling;
                    this.viewModel.updateCellOnUiThread(cell);
                }
            }
        }

        return anyBlockIsStillFalling;
    }

    /**
     * Destroys a cell on the game board by replacing the image with the null image.
     * @param cell the cell to destroy.
     */
    private void destroyCell(GameBoardCell cell)
    {
        this.gameBoard.resetCell(cell);
        this.viewModel.updateCellOnUiThread(cell);
    }

    /**
     * Increments the game level and updates the display.
     */
    private void levelUp()
    {
        this.gameSession.incrementLevel();
        this.gameVelocity += this.gameSession.getLevel() % 5 == 0 ? 1 : 0;
        this.viewModel.setLevelText(Integer.toString(this.gameSession.getLevel()));
        Log.i("GameController", "Levelled up: " + this.gameSession.getLevel() + ".");
    }

    /**
     * Repopulates the game session and game board from serialized data.
     * @param serializedSession the simplified game session and gameboard values.
     */
    public void populateGameSessionFromSerializedValues(SerializableSessionState serializedSession)
    {
        this.gameSession.setNextPlanet(PlanetsEnum.getPlanet(serializedSession.getNextPlanet()));
        this.gameSession.addPointsToScore(serializedSession.getScore());
        this.gameSession.setLevel(serializedSession.getLevel());
        int[][] planets = serializedSession.getPlanets();

        this.viewModel.setLevelText(Integer.toString(this.gameSession.getLevel()));
        this.viewModel.setScoreText(this.gameSession.getScore());

        GameBoardCell cell;
        PlanetsEnum planet;
        for (int column = 0; column < planets.length; column++)
        {
            for (int row = 0; row < planets[0].length; row++)
            {
                cell = this.gameBoard.getCell(column, row);
                planet = PlanetsEnum.getPlanet(planets[column][row]);
                if (planet != PlanetsEnum.NULL_PLANET)
                {
                    cell.setOccupied(true);
                    cell.setPlanet(planet);
                    cell.setCellBitmap(this.gameBoard.getPlanetBitmap(planet));
                    this.viewModel.updateCellOnUiThread(cell);
                }
            }
        }

        GameBoardCell activeCell = this.gameBoard.getCell(serializedSession.getActiveColumn(), serializedSession.getActiveRow());
        activeCell.setImageX(serializedSession.getActiveImageX());
        activeCell.setImageY(serializedSession.getActiveImageY());
        this.viewModel.updateCellOnUiThread(activeCell);
        this.gameBoard.setActiveCell(activeCell.getColumn(), activeCell.getRow());
        this.onResume = true;
    }

    /* Getters and Setters */

    /**
     * Returns a random planet from PlanetsEnum (excludes NULL_PLANET).
     * @return a random planet from PlanetsEnum;
     */
    private PlanetsEnum getRandomPlanet() {
        return PlanetsEnum.getPlanet(ThreadLocalRandom.current().nextInt(1,
                PlanetsEnum.values().length +1));
    }

    /**
     * Gets the next planet in order of distance from the sun. If index is greater than the number
     * of planets than the value loops back to 1 (Mercury), excludes NULL_PLANET.
     * @return the next planet after the current Planet in order of distance from the sun.
     */
    private PlanetsEnum getNextPlanet(PlanetsEnum currentPlanet)
    {
        return PlanetsEnum.getPlanet(currentPlanet.getOrder()+1);
    }
}