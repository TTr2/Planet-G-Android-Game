package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import java.io.Serializable;

/**
 * the game board class represents the playable game area, composed of game board cells that contain
 * details about the planet image to display and its position, as well as all operations for
 * manipulating the game board.
 * Created by tango on 21/11/2017.
 */

public class GameBoard implements Serializable {

    private static int viewWidth;
    private static int viewHeight;
    private int cellSize;
    private int columns = 7;
    private int rows;
    private int startingColumn;
    private int startingRow;
    private float direction;
    private GameBoardCell[][] gameBoardCells;
    private GameBoardCell activeCell;
    private ImageView nullImageView;

    /**
     * Constructor for GameBoard object, it is instantiated by the GameManager after the
     * GameSurfaceView's onSizeChanged() event is called when activity is created.
     * @param context the GameSurfaceView context.
     */
    public GameBoard(Context context)
    {
        if (viewWidth == 0 && viewHeight == 0)
        {
            viewWidth = 1080;
            viewHeight = 1920;
        }
        this.cellSize = GameBoard.viewWidth / this.columns;
        this.rows = GameBoard.viewHeight / this.cellSize;
        this.startingColumn = 3; // half of 7, zero indexed
        this.startingRow = this.rows - 1; // zero indexed
        this.nullImageView= new NullImageView(context);
        this.CreateEmptyGameBoard();
    }

    /**
     * Generates and returns a new 2D array of GameBoardCells, configured using the fields in the
     * GameBoard class.
     * @return a new 2D array of GameBoardCells.
     */
    protected void CreateEmptyGameBoard()
    {
        GameBoardCell[][] newGameBoard = new GameBoardCell [this.columns][this.rows];

        // Populate game board with empty game board cells.
        int x = 0, y;
        for (int column = 0; column < this.columns; column++) {
            y = GameBoard.viewHeight - this.cellSize;

            for (int row = 0; row < this.rows; row++) {
                newGameBoard[column][row] = new GameBoardCell(column, row, this.nullImageView,
                        this.cellSize, x, y);

                // Set the y position to use for the next cell upwards.
                y -= this.cellSize;
            }

            // Set the x position to use for the next cells to the right.
            x += this.cellSize;
        }

        for (int column = 0; column < this.columns; column++)
        {
            for (int row = 0; row < this.rows; row++)
            {
                if (column == 0)
                    newGameBoard[column][row].setCellLeft(null);
                else
                    newGameBoard[column][row].setCellLeft(newGameBoard[column-1][row]);

                if (column == this.columns - 1)
                    newGameBoard[column][row].setCellRight(null);
                else
                    newGameBoard[column][row].setCellRight(newGameBoard[column+1][row]);

                if (row == 0)
                    newGameBoard[column][row].setCellDown(null);
                else
                    newGameBoard[column][row].setCellDown(newGameBoard[column][row-1]);

                if (row == this.rows - 1)
                    newGameBoard[column][row].setCellUp(null);
                else
                    newGameBoard[column][row].setCellUp(newGameBoard[column][row+1]);
            }
        }

        this.gameBoardCells = newGameBoard;
    }

    /**
     * Adds a new block to the game board and assigns as the active block.
     * @param activeImage the active planet ImageView.
     */
    public void addNewBlock(ImageView activeImage)
    {
        this.activeCell = this.getCell(startingColumn, startingRow);
        this.activeCell.setImageView(activeImage);
        this.activeCell.setOccupied(true);
        Log.d("GameBoard", "Added new block: active cell=" + this.activeCell.getColumn() + "," + this.activeCell.getRow() + ", image pos= " + this.activeCell.getImageX() + "," + this.activeCell.getImageY() + ".");
    }

    /**
     * Moves the active planet block downwards on the Y axis, checking for occupied blocks below.
     * @param velocity the pixels to move the block downwards.
     * @return whether the block is still falling and not obstructed.
     */
    public boolean moveBlockY(GameBoardCell cell, float velocity)
    {
        float targetTopEdgeY = cell.getImageY() + velocity;
        float targetBottomEdgeY = targetTopEdgeY + this.cellSize;
        int activeColumn = cell.getColumn();
        GameBoardCell blockingCell = this.getCell(activeColumn, 0);
        int stoppingPointY = blockingCell.getCellY();
        boolean isBlockFalling;

        // Starting from the top row down find next occupied row
        for (int row = cell.getRow() - 1; row >= 0; row--)
        {
            blockingCell = this.getCell(activeColumn, row);
            if (blockingCell.isOccupied())
            {
                stoppingPointY = blockingCell.getCellY();
                break;
            }
        }

        // If poss, velocity is applied and Y is altered
        if (targetBottomEdgeY < stoppingPointY)
        {
            // Safe to set new Y position
            cell.setImageY(targetTopEdgeY);

            // Check if in a new row, starting from last known row
            for (int row = cell.getRow() - 1; row >blockingCell.getRow(); row--)
            {
                GameBoardCell tmpCell = this.getCell(activeColumn, row);
                if (targetTopEdgeY < tmpCell.getCellY()
                        && targetBottomEdgeY > tmpCell.getCellY()
                        && cell.getRow() != tmpCell.getRow())
                {
                    this.switchActiveCell(activeColumn, row,
                            cell.getImageX(), cell.getImageY());
                    break;
                }
            }

            isBlockFalling = true;
        }
        else if (targetTopEdgeY < stoppingPointY)
        {
            // Safe to set new Y position
            cell.setImageY(targetTopEdgeY);
            isBlockFalling = true;
        }
        else
        {
            if (cell.getRow() != this.startingRow)
            {
                cell.setImageY(stoppingPointY);
            }

            isBlockFalling = false;
        }

        return isBlockFalling;
    }


    /**
     * Moves the falling block horizontally if possible.
     * @param inputX the direction received from the device sensor.
     * @param isBlockFalling whether the block is falling.
     * @return whether the block is still falling.
     */
    protected boolean moveBlockX(float inputX, boolean isBlockFalling)
    {
        int stoppingPointX;
        float projectedX;
        long waitTime = 500;
        long endTime;
        int activeColumn;
        int neighbouringColumn;
        float tolerance = 0.25f;
        float factor = 1.5f;

        // Ignore noise above/below tolerance threshold
        if (Math.abs(inputX) > tolerance)
        {
            /* Invert input direction so that tipping the screen moves blocks 'down' not 'up'
            and apply factor to increase sensitivity before adding to direction */
            this.direction += (inputX * -1) * factor;

            if (Math.abs(this.direction) > this.cellSize)
            {
                activeColumn = this.activeCell.getColumn();

                // Heading left to right
                if (direction > 0 && activeColumn < this.columns - 1)
                    neighbouringColumn = activeColumn + 1;
                // Heading right to left
                else if (direction < 0 && activeColumn > 0)
                    neighbouringColumn = activeColumn - 1;
                // Can't change column
                else
                    neighbouringColumn = activeColumn;

                // If block not in extreme left or right column
                if (neighbouringColumn != activeColumn)
                {
                    GameBoardCell neighbourCell = this.getCell(neighbouringColumn,
                            this.activeCell.getRow());

                    if (!neighbourCell.isOccupied())
                    {
                        if (this.switchActiveCell(neighbouringColumn, this.activeCell.getRow(),
                                neighbourCell.getCellX(), this.activeCell.getImageY()))
                        {
                            this.direction = 0;
                            isBlockFalling = this.moveBlockY(this.activeCell, 0);
                        }
                    }
                }
            }
        }

        return isBlockFalling;
    }


    /**
     * Switches the active cell.
     * @param column the new active cell column
     * @param row the new active cell row
     * @param imageX the X coordinate to set for the new active cell
     * @param imageY the Y coordinate to set for the new active cell
     * */
    private boolean switchActiveCell(int column, int row, float imageX, float imageY)
    {
        boolean switchedOK = false;
        GameBoardCell destinationCell = this.getCell(column, row);
        Log.d("GameBoard", "Switching cell: active cell=" + this.activeCell.getColumn() + "," + this.activeCell.getRow() + ", image pos= " + this.activeCell.getImageX() + "," + this.activeCell.getImageY() + ".");
        Log.d("GameBoard", "Switching cell: destination cell=" + destinationCell.getColumn() + "," + destinationCell.getRow() + ", image pos= " + destinationCell.getImageView().getX() + "," + destinationCell.getImageView().getY() + ".");
        if (!destinationCell.isOccupied())
        {
            // Update destination cell with active image and coordinates
            destinationCell.setImageView(this.activeCell.getImageView());
            destinationCell.setImageX(imageX);
            destinationCell.setImageY(imageY);
            destinationCell.setOccupied(true);
            destinationCell.setDestroyed(false);

            // Reset the soon to be inactive cell
            this.activeCell.setImageView(this.nullImageView);
            this.activeCell.setImageX(this.activeCell.getCellX());
            this.activeCell.setImageY(this.activeCell.getCellY());
            this.activeCell.setOccupied(false);
            this.activeCell.setDestroyed(false);

            // Update active cell
            this.activeCell = destinationCell;
            switchedOK = true;
        }

        Log.d("GameBoard", "Switched cells: new active cell=" + this.activeCell.getColumn() + "," + this.activeCell.getRow() + ", image pos= " + this.activeCell.getImageX() + "," + this.activeCell.getImageY() + ".");
        return switchedOK;
    }

    /**
     * Checks if fallen block has scored then calculates the score for any destroyed blocks.
     * @param cell the starting game board cell.
     * @return the calculated score.
     */
    protected long calculateScore(GameBoardCell cell)
    {
        boolean matchingPlanets = this.checkForThreeMatchingPlanets(cell);
        if (matchingPlanets)
        {
            return cell.traverse(cell, cell.getPointsPerBlock());
        }
        else
        {
            return 0;
        }
    }

    /**
     * Checks the game board for three adjacent matching planet blocks.
     * @param cell the starting cell.
     * @return whether the cell has three matching adjacent planet blocks.
     */
    protected boolean checkForThreeMatchingPlanets(GameBoardCell cell)
    {
        PlanetsEnum planet = cell.getPlanet();
        int matchingPlanets = 0;
        GameBoardCell cell2, cell3;

        // 3 down
        cell2 = cell.getCellDown();
        if (cell2 != null)
        {
            cell3 = cell2.getCellDown();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // L top
        cell2 = cell.getCellDown();
        if (cell2 != null)
        {
            cell3 = cell2.getCellRight();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // L right
        cell2 = cell.getCellLeft();
        if (cell2 != null)
        {
            cell3 = cell2.getCellUp();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // rev L top
        cell2 = cell.getCellDown();
        if (cell2 != null)
        {
            cell3 = cell2.getCellLeft();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // rev L left
        cell2 = cell.getCellRight();
        if (cell2 != null)
        {
            cell3 = cell2.getCellUp();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // r left
        cell2 = cell.getCellDown();
        if (cell2 != null)
        {
            cell3 = cell.getCellRight();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // r right
        cell2 = cell.getCellLeft();
        if (cell2 != null)
        {
            cell3 = cell2.getCellDown();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // rev r left
        cell2 = cell.getCellRight();
        if (cell2 != null)
        {
            cell3 = cell2.getCellDown();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // rev r right
        cell2 = cell.getCellLeft();
        if (cell2 != null)
        {
            cell3 = cell.getCellDown();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // flat left
        cell2 = cell.getCellRight();
        if (cell2 != null)
        {
            cell3 = cell2.getCellRight();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // flat middle
        cell2 = cell.getCellLeft();
        if (cell2 != null)
        {
            cell3 = cell.getCellRight();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        // flat right
        cell2 = cell.getCellLeft();
        if (cell2 != null)
        {
            cell3 = cell2.getCellLeft();

            if (cell3 != null)
            {
                if (cell2.getPlanet() == planet && cell3.getPlanet() == planet)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Resets all destroyed cells to empty cells.
     */
    protected void destroyCells()
    {
        GameBoardCell cell;
        for (int row = 0; row < this.rows; row++)
        {
            for (int column = 0; column < this.columns; column++)
            {
                cell = this.getCell(column, row);
                if (cell.isDestroyed())
                {
                    cell.setImageView(this.nullImageView);
                    cell.setOccupied(false);
                    cell.setDestroyed(false);
                }
            }
        }

        Log.d("GameBoard", "Destroying cells.");
    }

    /**
     * Iterates through the game board and moves blocks with no block below down screen.
     * @return whether any block is still falling.
     */
    protected boolean repositionRemainingBlocks(float velocity) {
        boolean thisBlockIsStillFalling = false;
        boolean anyBlockIsStillFalling = false;
        GameBoardCell cell;
        for (int row = 0; row < this.rows; row++)
        {
            for (int column = 0; column < this.columns; column++)
            {
                cell = this.getCell(column, row);
                thisBlockIsStillFalling = this.moveBlockY(cell, velocity);
                anyBlockIsStillFalling = thisBlockIsStillFalling ? true : anyBlockIsStillFalling;
            }
        }

        return anyBlockIsStillFalling;
    }

    /**
     * Sets the dimensions of the gameboard, which is the width and height of the GameSurfaceView.
     * @param w the width of the GameSurfaceView.
     * @param h the height of the GameSurfaceView.
     */
    public static void setGameBoardDimensions(int w, int h)
    {
        GameBoard.viewWidth = w;
        GameBoard.viewHeight = h;
    }

    /**
     * Gets a GameBoardCell at the specified coordinate on the GameBoard.
     * @param column the horizontal index.
     * @param row the vertical index.
     * @return the GameBoardCell at the requested coordinate on the GameBoard.
     */
    protected GameBoardCell getCell(int column, int row)
    {
        return this.gameBoardCells[column][row];
    }

    /**
     * Gets the active cell on the game board.
     * @return the active cell on the game board.
     */
    public GameBoardCell getActiveCell()
    {
        return this.activeCell;
    }

    /**
     * Gets the number of columns on the game board.
     * @return the number of columns on the game board.
     */
    protected int getColumns() { return columns; }

    /**
     * Gets the number of rows on the game board.
     * @return the number of rows on the game board.
     */
    protected int getRows() { return rows; }

    /**
     * Whether the last placed block is in the starting position and no more blocks can be placed.
     * @return Whether the last placed block is in the starting position and no more blocks can be
     * placed.
     */
    public boolean isGameOver()
    {
        return this.activeCell.getRow() == this.startingRow
                && this.activeCell.getColumn() == this.startingColumn;
    }

    /**
     * gets the size of a game cell
     * @return the size of a game cell.
     */
    public int getCellSize() { return cellSize; }
}
