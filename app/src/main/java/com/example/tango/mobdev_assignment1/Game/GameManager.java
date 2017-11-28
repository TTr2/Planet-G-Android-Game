package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

/**
 * The GameManager class which manages the game state.
 * Created by tango on 20/11/2017.
 */
public class GameManager implements Runnable {

    private float gameVelocity = 5;
    private float direction = 0;
    private boolean isGameLoopRunning;
    private int blocksFallenCount;
    private int blocksPerLevel = 10;
    private Thread gameLoopThread;
    private GameViewModel viewModel;
    private Context gameBoardContext;
    private GameBoard gameBoard;
    private PlanetsEnum startingPlanet = PlanetsEnum.EARTH;
    private GameSession gameSession;


    /**
     * Constructor for the GameManager class.
     * @param viewModel the view model that abstracts the view implementation.
     */
    public GameManager(GameViewModel viewModel)
    {
        this.viewModel = viewModel;
        this.gameBoard = new GameBoard(this.viewModel.getGameBoardLayoutContext());
        this.gameSession = new GameSession(this.gameBoard, this.startingPlanet);
        this.gameBoardContext = this.viewModel.getGameBoardLayoutContext();
        this.gameLoopThread = new Thread(this);
        this.gameLoopThread.start();
        Log.i("GameManager", "Game Manager constructed.");
        //TODO: resume game session
    }

    /**
     * Drives the game loop. Borrowed the frame adjusting timer algorithm from StackOverflow user
     * Lee Fogg @ https://stackoverflow.com/questions/17847930/custom-vsync-algorithm/18070011#18070011
     */
    @Override
    public void run()
    {
        Log.i("GameManager", "Game loop started.");
        int targetFrameRate = 60;
        int msPerFrame = 1000 / targetFrameRate;
        long currentTime = System.currentTimeMillis();
        long targetTime = currentTime + msPerFrame;

        boolean isBlockFalling;
        ImageView activeBlockImage;
        PlanetsEnum nextPlanet = startingPlanet;
        float inputX, inputY, calcVelocity, descent, lastYPosition;

        isGameLoopRunning = true;
        while (isGameLoopRunning)
        {
            try {
                // Create new active block
                activeBlockImage = new PlanetBlockImageView(this.gameBoardContext, nextPlanet);

                // Add new block to Game Board and game layout
                this.gameBoard.addNewBlock(activeBlockImage);
                this.viewModel.addImageViewToGameLayout(activeBlockImage);
                lastYPosition = activeBlockImage.getY();
                descent = 0;

                // Set next planet and display
                nextPlanet = this.getRandomPlanet();
                this.viewModel.setNextPlanet(nextPlanet);

                isBlockFalling = true;
                while (isBlockFalling)
                {
                    currentTime = System.currentTimeMillis();
                    if (currentTime >= targetTime)
                    {
                        targetTime = (currentTime + msPerFrame) - (currentTime-targetTime);
                        inputX = this.viewModel.getHorizontalMotion();
                        inputY = this.viewModel.getVerticalMotion();

                        //TODO: decide if going to use inputY at all!
                        calcVelocity = this.calculateVelocity(inputY); // take gameVelocity / planet / inputY
                        calcVelocity = this.gameVelocity; //TODO: delete once velocity calculation done
                        isBlockFalling = this.gameBoard.moveBlockY(this.gameBoard.getActiveCell(), calcVelocity);
                        isBlockFalling = this.gameBoard.moveBlockX(inputX, isBlockFalling);
                        // TODO: consider reducing number of horizontal moves per second
                            // TODO: calculate whether gravity guess is correct (+/-)
                    }

                    this.viewModel.drawIndividualImage(activeBlockImage, this.gameBoard.getActiveCell());
                }

                // Calculate points scored and add to session score
                long points = this.gameBoard.calculateScore(this.gameBoard.getActiveCell())
                        * this.gameSession.getLevel();
                while (points > 0)
                {
                    boolean isGameBoardInFlux = true;
                    this.gameSession.addPointsToScore(points);
                    this.viewModel.setScoreText(this.gameSession.getScore());
                    this.gameBoard.destroyCells();
                    isGameBoardInFlux = true;
                    while (isGameBoardInFlux)
                    {
                        isGameBoardInFlux = this.gameBoard.repositionRemainingBlocks(this.gameVelocity);
                    }
                    this.viewModel.drawGameBoard(this.gameBoard);

                    //TODO: nightmare to check all combinations of new scores without activecell as anchor
                    // points = this.gameBoard.calculateScore();
                    points = 0; // TODO: see above re scoring for fallen blocks, maybe once blocks have settled.
                }

                // Check whether to increment the game level
                if (++this.blocksFallenCount % this.blocksPerLevel == 0)
                {
                    this.levelUp();
                }

                if (this.gameBoard.isGameOver())
                {
                    // TODO: last block not always showing in top position
                    isGameLoopRunning = false;
                    Log.i("GameManager", "Game Over!");
                    //TODO: save score to xml
                    //TODO: navigate to HiScores
                }
            }
            catch(Exception e)
            {
                Log.e("GameManager", "Exception Handled! " + e.getMessage(), e);
            }
        }

        Log.i("GameManager", "Game loop ended.");
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
        Log.d("GameManager", "Calculated velocity: " + calcVelocity + " from current planet G=" + this.gameSession.getCurrentPlanet().getGravity() + ", active planet G=" + this.gameBoard.getActiveCell().getPlanet().getGravity() + ", level=" + gameVelocity + ", inputY=" + inputY +".");
        return calcVelocity;
    }

    /**
     * Creates a new game session.
     */
    protected void createNewGameSession()
    {
        // TODO: Save old session / scores first?
        this.gameBoard.CreateEmptyGameBoard();
        this.gameSession = new GameSession(this.gameBoard, this.startingPlanet);
    }

    /**
     * Increments the game level and updates the display.
     */
    private void levelUp()
    {
        this.gameSession.incrementLevel();
        this.gameVelocity += 1;
        this.viewModel.setLevelText(Integer.toString(this.gameSession.getLevel()));

        // Select a new current planet
        PlanetsEnum nextPlanet = this.getNextPlanet(this.gameSession.getCurrentPlanet());
        this.gameSession.setCurrentPlanet(nextPlanet);
        this.viewModel.setNextPlanet(nextPlanet);

        Log.i("GameManager", "Levelled up: " + this.gameSession.getLevel() + ".");
        Log.i("GameManager", "Next planet: " + nextPlanet.toString() + ".");
    }

    /* Getters and Setters */

    /**
     * Returns a random planet from PlanetsEnum.
     * @return a random planet from PlanetsEnum;
     */
    private PlanetsEnum getRandomPlanet() {
        return PlanetsEnum.getPlanet(ThreadLocalRandom.current().nextInt(1,
                PlanetsEnum.values().length +1));
    }

    /**
     * Gets the next planet in order of distance from the sun. If index is greater than the number
     * of planets than the value loops back to 1 (Mercury).
     * @return the next planet after the current Planet in order of distance from the sun.
     */
    private PlanetsEnum getNextPlanet(PlanetsEnum currentPlanet)
    {
        return PlanetsEnum.getPlanet(currentPlanet.getOrder()+1);
    }

    /**
     * Stops the game loop.
     */
    public void stopGameLoop()
    {
        this.isGameLoopRunning = false;
        Log.i("GameManager", "Stop game loop called.");
        //TODO: save session to xml.
    }
}