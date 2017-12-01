package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.concurrent.ThreadLocalRandom;

/**
 * The GameController class which manages the game state.
 * Created by tango on 20/11/2017.
 */
public class GameController implements Runnable {

    private final int targetFrameRate = 60;
    private final int blocksPerLevel = 5;
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
        this.gameBoard = new GameBoard(this.context);
        this.gameSession = new GameSession(this.gameBoard, this.startingPlanet);
        SerializableSessionState serializedSessionState = this.viewModel.loadSessionState();
        if (serializedSessionState != null)
        {
            this.populateGameSessionFromSerializedValues(serializedSessionState);
        }
        this.isGameLoopRunning = true;
        this.isPaused = false;
        this.onResume = false;
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
        boolean isBlockFalling;
        ImageView activeImage = new ImageView(this.context);
        PlanetsEnum nextPlanet = this.startingPlanet;
        float inputX, inputY, calcVelocity;

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
                    activeImage = new ImageView(this.context);
                    activeImage.setImageResource(nextPlanet.getImageResource());
                    this.gameBoard.addToGameBoard(activeImage, nextPlanet);
                    this.viewModel.addImageViewToGameLayout(activeImage);

                    // Set next planet and display
                    nextPlanet = this.getRandomPlanet();
                    this.viewModel.setNextPlanet(nextPlanet);
                }

                isBlockFalling = true;
                while (isBlockFalling && !this.isPaused)
                {
                    currentTime = System.currentTimeMillis();
                    if (currentTime >= targetTime)
                    {
                        targetTime = (currentTime + msPerFrame) - (currentTime-targetTime);
                        inputX = this.viewModel.getHorizontalMotion();
                        // TODO: gotta incorporate gravity eitehr with sensor or G of planet
                        inputY = this.viewModel.getVerticalMotion();

                        //TODO: decide if going to use inputY at all!
                        calcVelocity = this.calculateVelocity(inputY); // take gameVelocity / planet / inputY
                        calcVelocity = this.gameVelocity; //TODO: delete once velocity calculation done
                        isBlockFalling = this.gameBoard.moveBlockY(this.gameBoard.getActiveCell(), calcVelocity);
                        isBlockFalling = this.gameBoard.moveBlockX(inputX, isBlockFalling);
                        // TODO: consider reducing number of horizontal moves per second
                            // TODO: calculate whether gravity guess is correct (+/-)
                    }

                    this.viewModel.drawIndividualImage(this.gameBoard.getActiveCell());
                }

                if (!this.isPaused)
                {
                    // Calculate points scored and add to session score
                    long points = this.gameBoard.calculateScore(this.gameBoard.getActiveCell())
                            * this.gameSession.getLevel();
                    while (points > 0)
                    {
                        boolean isGameBoardInFlux = true;
                        this.gameSession.addPointsToScore(points);
                        this.viewModel.setScoreText(this.gameSession.getScore());
                        points = 0;
                        while (isGameBoardInFlux)
                        {
                            isGameBoardInFlux = this.destroyCells(this.gameVelocity);
                        }
                        // Check game board for new combinations
                        GameBoardCell cell;
                        for (int row = 0; row < this.gameBoard.getRows(); row++)
                        {
                            for (int column = 0; column < this.gameBoard.getColumns(); column++) {
                                cell = this.gameBoard.getCell(column, row);
                                if (cell.isOccupied())
                                {
                                    points += this.gameBoard.calculateScore(cell) * this.gameSession.getLevel();
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
                        this.viewModel.displayGameOverImage();
                        Log.i("GameController", "Game Over!");
                        //TODO: save score to xml
                        //TODO: navigate to HiScores
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
     * Called by activity onResume(), if game was paused then restarts the game loop from paused state.
     */
    public void resumeGameLoop()
    {
        this.onResume = true;
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
            try
            {
                SerializableSessionState sessionState = new SerializableSessionState(this.gameSession);
                Gson gson = new Gson();
                String sessionStateString = gson.toJson(sessionState, SerializableSessionState.class);
                this.viewModel.saveSessionState(sessionStateString);
            }
            catch(Exception e)
            {
                Log.d("GameController", "Failed to save game state.", e);
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
                    this.viewModel.drawIndividualImage(cell);
                }
            }
        }

        return anyBlockIsStillFalling;
    }

    /**
     * Destroys a cell on the game board and removes the image from the game layout.
     * @param cell the cell to destroy.
     */
    private void destroyCell(GameBoardCell cell)
    {
        ImageView imageView = cell.getImageView();
        if (imageView != null)
        {
            this.viewModel.removeImageViewFromGameLayout(imageView);
        }

        this.gameBoard.resetCell(cell);
    }

    /**
     * Calculates the velocity of the falling block based on the gravity of the current and active
     * planets, the level and the input from the player.
     * @param inputY the input from the player.
     * @return the calculated velocity.
     */
    private float calculateVelocity(float inputY) {

        float calcVelocity = inputY;
        //TODO: calculate velocity. Consider some getters (pretty convoluted) or different class.
        // TODO: retain previous value to check for change?
        // TODO: need to calibrate/reset at some point
        return calcVelocity;
    }

    /**
     * Increments the game level and updates the display.
     */
    private void levelUp()
    {
        this.gameSession.incrementLevel();
        this.gameVelocity += this.gameSession.getLevel() % 5 == 0 ? 1 : 0;
        this.viewModel.setLevelText(Integer.toString(this.gameSession.getLevel()));

        // Select a new current planet
        PlanetsEnum nextPlanet = this.getNextPlanet(this.gameSession.getCurrentPlanet());
        this.gameSession.setCurrentPlanet(nextPlanet);
        this.viewModel.setNextPlanet(nextPlanet);

        Log.i("GameController", "Levelled up: " + this.gameSession.getLevel() + ", next planet: " + nextPlanet.toString() + ".");
    }

    /**
     * Repopuulates the game session and game board from serialized data.
     * @param serializedSession the simplified game session and gameboard values.
     */
    public void populateGameSessionFromSerializedValues(SerializableSessionState serializedSession)
    {
        this.gameSession.setCurrentPlanet(PlanetsEnum.getPlanet(serializedSession.getCurrentPlanet()));
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
                cell = gameBoard.getCell(column, row);
                planet = PlanetsEnum.getPlanet(planets[column][row]);
                if (planet != PlanetsEnum.NULL_PLANET)
                {
                    cell.setOccupied(true);
                    cell.setPlanet(planet);
                    ImageView imageView = new ImageView(this.context);
                    imageView.setImageResource(planet.getImageResource());
                    cell.setImageView(imageView);
                    this.viewModel.addImageViewToGameLayout(imageView);
                    this.viewModel.drawIndividualImage(cell);
                }
            }
        }

        GameBoardCell activeCell = this.gameBoard.getCell(serializedSession.getActiveColumn(), serializedSession.getActiveRow());
        activeCell.setImageX(serializedSession.getActiveImageX());
        activeCell.setImageY(serializedSession.getActiveImageY());
        this.gameBoard.setActiveCell(activeCell.getColumn(), activeCell.getRow());
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