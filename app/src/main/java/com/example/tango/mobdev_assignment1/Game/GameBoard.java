package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.tango.mobdev_assignment1.R;

/**
 * the game board class represents the playable game area, composed of game board cells that contain
 * details about the planet image to display and its position, as well as all operations for
 * manipulating the game board.
 * Created by tango on 21/11/2017.
 */

public class GameBoard {

    private Context context;
    private static int viewWidth;
    private static int viewHeight;
    private int cellSize;
    private int columns;
    private int rows;
    private int startingColumn;
    private int startingRow;
    private float direction;
    private GameBoardCell[][] gameBoardCells;
    private GameBoardCell activeCell;
    private Bitmap[] planetBitmaps;

    /**
     * Constructor for GameBoard object, it is instantiated by the GameController after the
     * GameSurfaceView's onSizeChanged() event is called when activity is created.
     * @param context the Game layout context.
     */
    public GameBoard(Context context, int columns)
    {
        this.context = context;
        this.columns = columns;
        if (viewWidth == 0 && viewHeight == 0)
        {
            viewWidth = 1080;
            viewHeight = 1920;
        }
        this.cellSize = GameBoard.viewWidth / this.columns;
        this.rows = GameBoard.viewHeight / this.cellSize;
        this.startingColumn = this.columns / 2; // half of columns, zero indexed
        this.startingRow = this.rows - 1; // zero indexed
        this.planetBitmaps = new Bitmap[]{
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.null_image),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.mercury),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.venus),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.earth),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.mars),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.jupiter),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.saturn),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.uranus),
                    this.cellSize, this.cellSize, true),
            Bitmap.createScaledBitmap(
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.neptune),
                    this.cellSize, this.cellSize, true)};
        this.CreateEmptyGameBoard();
        this.activeCell = this.getCell(startingColumn, startingRow);
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
                newGameBoard[column][row] = new GameBoardCell(this.context, column, row,
                        this.cellSize, x, y, this.planetBitmaps[0], PlanetsEnum.NULL_PLANET);

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
     * Sets the starting cell as the new block.
     * @param planet the active planet of the new block.
     */
    public void addToGameBoard(PlanetsEnum planet)
    {
        this.activeCell = this.getCell(startingColumn, startingRow);
        this.activeCell.setOccupied(true);
        this.activeCell.setPlanet(planet);
        this.activeCell.setCellBitmap(this.planetBitmaps[planet.getOrder()]);
        this.activeCell.setImageX(this.activeCell.getCellX());
        this.activeCell.setImageY(this.activeCell.getCellY());

        Log.d("GameBoard", "Added new block: active cell=" + this.activeCell.toString() +".");
    }

    /**
     * Moves the active planet image downwards on the Y axis, checking for occupied blocks below.
     * @param velocity the pixels to move the block downwards.
     * @return whether the block is still falling and not obstructed.
     */
    public boolean moveBlockY(GameBoardCell cell, float velocity)
    {
        float targetTopEdgeY = cell.getImageY() + velocity;
        float targetBottomEdgeY = targetTopEdgeY + this.cellSize;
        int activeColumn = cell.getColumn();
        boolean isBlockFalling = true;
        int stoppingPointY;
        GameBoardCell cellBelow = cell.getCellDown();

        if (cellBelow == null) // Bottom row edge case
        {
            stoppingPointY = this.getCell(activeColumn, 0).getCellY() + this.cellSize;
            if (targetBottomEdgeY < stoppingPointY)
            {
                cell.setImageY(targetTopEdgeY);
                isBlockFalling = true;
            }
            else
            {
                cell.setImageY(cell.getCellY());
                isBlockFalling = false;
            }
        }
        else // Not bottom row
        {
            stoppingPointY = cellBelow.getCellY();
            if (cellBelow.isOccupied())
            {
                if (targetBottomEdgeY < stoppingPointY)
                {
                    cell.setImageY(targetTopEdgeY);
                    isBlockFalling = true;
                }
                else
                {
                    cell.setImageY(cellBelow.getCellY() - this.cellSize);
                    isBlockFalling = false;
                }
            }
            else // Proceed but check if changed cell
            {
                cell.setImageY(targetTopEdgeY);
                isBlockFalling = true;

                // Only true when block first crosses lower boundary
                if (targetTopEdgeY < stoppingPointY
                        && targetBottomEdgeY > stoppingPointY)
                {
                    // Only assign to active cell if argument was the active cell
                    if (cell == this.activeCell)
                    {
                        this.activeCell = this.switchCells(cell, cellBelow);
                    }
                    // This is for after destroying planets
                    else
                    {
                        this.switchCells(cell, cellBelow);
                    }
                }
            }
        }

        return isBlockFalling;
    }

    /**
     * Moves the falling block horizontally if possible.
     * @param inputX the direction received from the device sensor.
     * @return whether the block has moved to a different column.
     */
    protected boolean moveBlockX(float inputX) {
        int activeColumn;
        int neighbouringColumn;
        boolean hasChangedColumn = false;

        // Filter out background signal and invert direction
        this.direction += ((inputX - 0.125) * -1);
        if (Math.abs(this.direction) >= this.cellSize / 2)
        {
            activeColumn = this.activeCell.getColumn();

            // Heading left to right
            if (this.direction > 0 && activeColumn < this.columns - 1)
            {
                neighbouringColumn = activeColumn + 1;
            }
            // Heading right to left
            else if (this.direction < 0 && activeColumn > 0)
            {
                neighbouringColumn = activeColumn - 1;
            }
            // Can't change column
            else
            {
                neighbouringColumn = activeColumn;
            }

            // If block not in extreme left or right column
            if (neighbouringColumn != activeColumn)
            {
                GameBoardCell neighbourCell = this.getCell(neighbouringColumn,
                        this.activeCell.getRow());

                if (!neighbourCell.isOccupied())
                {
                    this.activeCell = this.switchCells(this.activeCell, neighbourCell);

                    this.activeCell.setImageX(this.activeCell.getCellX());
                    this.resetDirection();
                    hasChangedColumn = true;
                }
            }
        }

         return hasChangedColumn;
    }

    /**
     * Switches the contents of two adjacent cells on the game board.
     * @param sourceCell        the source cell to extract cell details from.
     * @param destinationCell   the destiation cell to copy cell details to.
     * @return whether the switch was successfull.
     */
    private GameBoardCell switchCells(GameBoardCell sourceCell, GameBoardCell destinationCell)
    {
        try
        {
            if (!destinationCell.isOccupied())
            {
                Log.d("GameBoard", "Switching cell: active cell=" + sourceCell.getColumn() + ","
                        + sourceCell.getRow() + ", image pos= " + sourceCell.getImageX() + ","
                        + sourceCell.getImageY() + ".\n Destination cell="
                        + destinationCell.getColumn() + "," + destinationCell.getRow()
                        + ", image pos= " + destinationCell.getX() + ","
                        + destinationCell.getY() + ".");

                // Update destination cell with active image and coordinates
                destinationCell.setPlanet(sourceCell.getPlanet());
                destinationCell.setCellBitmap(sourceCell.getCellBitmap());
                destinationCell.setImageX(sourceCell.getImageX());
                destinationCell.setImageY(sourceCell.getImageY());
                destinationCell.setOccupied(true);
                destinationCell.setDestroyed(false);

                // Reset the soon to be inactive cell
                sourceCell.setPlanet(PlanetsEnum.NULL_PLANET);
                sourceCell.setCellBitmap(this.planetBitmaps[0]);
                sourceCell.setImageX(sourceCell.getCellX());
                sourceCell.setImageY(sourceCell.getCellY());
                sourceCell.setOccupied(false);
                sourceCell.setDestroyed(false);
            }
        }
        catch (NullPointerException e)
        {
            Log.e("GameBoard", "Null pointer exception switching cells: " + e.getMessage(), e);
        }

        return destinationCell;
    }

    /**
     * Calculates the points for landing a block based on the level.
     * @param level the game session level.
     * @return the calculated score for landing a block.
     */
    protected int calculateNewBlockScore(int level)
    {
        return this.activeCell.getPointsPerBlock() * level;
    }

    /**
     * Checks if fallen block has scored then calculates the score for any destroyed blocks.
     * @param cell the starting game board cell.
     * @return the calculated score.
     */
    protected long calculateMultiplierScore(GameBoardCell cell)
    {
        int matchingPlanets = cell.traverseForMatches(cell.getPlanet(), 0);
        if (matchingPlanets == 1)
        {
            cell.setVisited(false);
        }
        else
        {
            this.resetCellVisitedStatus();
        }

        if (matchingPlanets >= 3)
        {
            return cell.traverseForPoints(cell.getPlanet(), cell.getPointsPerBlock());
        }
        else
        {
            return 0;
        }
    }

    /**
     * Resets all cell's visited  to empty cells.
     */
    private void resetCellVisitedStatus()
    {
        GameBoardCell cell;
        for (int row = 0; row < this.rows; row++)
        {
            for (int column = 0; column < this.columns; column++)
            {
                cell = this.getCell(column, row);
                if (cell.isVisited())
                {
                    cell.setVisited(false);
                }
            }
        }
    }

    /**
     * Resets the provided cell to an unoccupied cell containing a transparent bitmap.
     * @param cell the cell to reset.
     */
    public void resetCell(GameBoardCell cell) {
        cell.setCellBitmap(this.planetBitmaps[0]);
        cell.setOccupied(false);
        cell.setVisited(false);
        cell.setDestroyed(false);
        cell.setPlanet(PlanetsEnum.NULL_PLANET);
        Log.d("GameBoard", "Reset Cell: " + cell.toString());
    }

    /**
     * Reset the cumulative direction to zero.
     */
    protected void resetDirection()
    {
        this.direction = 0;
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
     * Sets the active cell, typically after session saved from disk.
     * @param column the horizontal index.
     * @param row the vertical index.
     */
    protected void setActiveCell(int column, int row)
    {
        this.activeCell = this.getCell(column, row);
        Log.d("GameBoard", "SetActiveCell " + activeCell.toString());
    }

    /**
     * Gets the array of cells on the gameboard.
     * @return
     */
    public GameBoardCell[][] getGameBoardCells() {
        return gameBoardCells;
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
     * Gets the planets corresponding bitmap.
     * @param planet the planet.
     * @return the planets (scaled) bitmap.
     */
    public Bitmap getPlanetBitmap(PlanetsEnum planet) { return this.planetBitmaps[planet.getOrder()];}

    /**
     * Whether the last placed block is in the starting position and no more blocks can be placed.
     * @return Whether the last placed block is in the starting position and no more blocks can be
     * placed.
     */
    public boolean isGameOver()
    {
        return this.activeCell.getRow() == this.startingRow
                && this.activeCell.getColumn() == this.startingColumn
                && this.activeCell.getCellDown() != null
                && this.activeCell.getCellDown().isOccupied();
    }
}
