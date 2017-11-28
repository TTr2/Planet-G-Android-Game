package com.example.tango.mobdev_assignment1.Game;

import android.graphics.Canvas;
import android.util.Log;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Represents a cell within the game board with predefined coordinates, it can contain a
 * PlanetBlockViewImage.
 * Created by tango on 21/11/2017.
 */
public class GameBoardCell implements Serializable {

    private final int size;
    private final int column;
    private final int row;
    private final int cellX;
    private final int cellY;
    private final int pointsPerBlock;
    private float imageX;
    private float imageY;
    private GameBoardCell cellUp;
    private GameBoardCell cellLeft;
    private GameBoardCell cellDown;
    private GameBoardCell cellRight;
    private ImageView imageView;
    private boolean occupied;
    private boolean destroyed;

    /**
     * Constructor for a game board cell.
     *
     * @param imageView the ImageView in this cell
     * @param size      the width and height of this cell.
     * @param x         the cellX coordinate of this cell.
     * @param y         the cellY coordinate of this cell.
     */
    public GameBoardCell(int column, int row, ImageView imageView, int size, int x, int y)
    {
        this.column = column;
        this.row = row;
        this.imageView = imageView;
        this.size = size;
        this.cellX = x;
        this.cellY = y;
        this.imageX = x;
        this.imageY = y;
        this.pointsPerBlock = 10;
        this.occupied = false;
        this.destroyed = false;
    }

    /**
     * Draw method to access the cell's imageView drawable
     *
     * @param canvas the GameSurfaceView's canvas.
     */
    public void draw(Canvas canvas)
    {
        if (this.isOccupied())
        {
            this.imageView.getDrawable().draw(canvas);
        }
    }

    /**
     * Navigates  through the game board to find adjacent matching planets, destroys the block
     * and multiplies the points achieved.
     * @param points the accumulated points.
     * @return the accumulated points.
     */
    protected long traverse(GameBoardCell cell, long points)
    {
        Log.d("GameBoardCell", "Traversing cell " + this.getColumn() + "," + this.getRow() + ", planet=" + this.getPlanet() + " points=" + points);
        if (cell == null || cell.getPlanet() != this.getPlanet() || cell.isDestroyed()) {
            return points;
        }
        else
        {
            this.destroyed = true;
            points *= this.pointsPerBlock;
            points += this.traverse(this.cellDown, points);
            points += this.traverse(this.cellLeft, points);
            points += this.traverse(this.cellUp, points);
            points += this.traverse(this.cellRight, points);

            return points;
        }
    }



    /**
     * Gets the ImageView in this cell, can be null.
     *
     * @return the ImageView in this cell.
     */
    protected ImageView getImageView()
    {
        return imageView;
    }

    /**
     * Sets the ImageView in this cell.
     *
     * @param imageView the ImageView to put in this cell.
     */
    protected void setImageView(ImageView imageView)
    {
        this.imageView = imageView;
        this.imageView.setMaxWidth(this.size);
        this.imageView.setMaxHeight(this.size);
    }

    /**
     * Sets the image X coordinate.
     *
     * @param imageX the new image X coordinate.
     */
    protected void setImageX(float imageX)
    {
        this.imageX = imageX;
    }

    /**
     * Sets the image Y coordinate.
     *
     * @param imageY the new image Y coordinate.
     */
    protected void setImageY(float imageY)
    {
        this.imageY = imageY;
    }

    /**
     * Sets whether this cell is occupied by a planet imageView.
     *
     * @param occupied whether this cell is occupied by a planet imageView.
     */
    public void setOccupied(boolean occupied)
    {
        this.occupied = occupied;
    }

    /**
     * Gets whether this cell is occupied by a planet imageView.
     *
     * @return whether this cell is occupied by a planet imageView.
     */
    public boolean isOccupied()
    {
        return occupied;
    }

    /**
     * Gets the size (width and height) of this cell.
     *
     * @return the size of this cell.
     */
    protected int getSize()
    {
        return this.size;
    }

    /**
     * Gets the cell's column index.
     *
     * @return the cell's column index.
     */
    public int getColumn()
    {
        return this.column;
    }

    /**
     * Gets the cell's row index.
     *
     * @return the cell's row index.
     */
    public int getRow()
    {
        return this.row;
    }

    /**
     * Gets the cellX coordinate of this cell.
     *
     * @return the cellX coordinate of this cell.
     */
    public int getCellX()
    {
        return this.cellX;
    }

    /**
     * Gets the cellY coordinate of this cell.
     *
     * @return the cellY coordinate of this cell.
     */
    public int getCellY()
    {
        return this.cellY;
    }

    /**
     * Gets the image X coordinate.
     *
     * @return the image X coordinate.
     */
    protected float getImageX()
    {
        return this.imageX;
    }

    /**
     * Gets the image Y coordinate.
     *
     * @return the image Y coordinate.
     */
    protected float getImageY()
    {
        return this.imageY;
    }

    /**
     * Gets the planet associated with this cell.
     *
     * @return the planet associated with this cell.
     */
    protected PlanetsEnum getPlanet() {
        if (this.imageView instanceof PlanetBlockImageView) {
            return ((PlanetBlockImageView) this.getImageView()).getPlanet();
        }
        return PlanetsEnum.NULL_PLANET;
    }

    //TODO: comment the cell getters and setters

    public int getPointsPerBlock() { return pointsPerBlock; }


    protected void setCellUp(GameBoardCell cellUp) {
        this.cellUp = cellUp;
    }

    protected void setCellLeft(GameBoardCell cellLeft) {
        this.cellLeft = cellLeft;
    }

    protected void setCellDown(GameBoardCell cellDown) {
        this.cellDown = cellDown;
    }

    protected void setCellRight(GameBoardCell cellRight) {
        this.cellRight = cellRight;
    }

    protected GameBoardCell getCellUp() {
        return this.cellUp;
    }


    protected GameBoardCell getCellLeft() {
        return this.cellLeft;
    }


    protected GameBoardCell getCellDown() {
        return this.cellDown;
    }


    protected GameBoardCell getCellRight() {
        return this.cellRight;
    }


    public boolean isDestroyed()
    {
        return this.destroyed;
    }

    public void setDestroyed(boolean isDestroyed)
    {
        this.destroyed = isDestroyed;
    }
}
