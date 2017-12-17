package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Represents a cell within the game board with predefined coordinates, as it extends ImageView it
 * contains manages the x, y and bitmap values of the image but also stores values position and
 * bitmap values for the cell that it represents, which can be modified on a separate thread
 * (ImageView values can only be edited on the main GUI thread)..
 * Created by tango on 21/11/2017.
 */
public class GameBoardCell extends android.support.v7.widget.AppCompatImageView {

    private final int size;
    private final int column;
    private final int row;
    private final int cellX;
    private final int cellY;
    private final int pointsPerBlock;
    private float imageX;
    private float imageY;
    private Bitmap cellBitmap;
    private GameBoardCell cellUp;
    private GameBoardCell cellLeft;
    private GameBoardCell cellDown;
    private GameBoardCell cellRight;
    private PlanetsEnum planet;
    private boolean occupied;
    private boolean destroyed;
    private boolean visited;

    public GameBoardCell(Context context) {
        super(context);
        this.setWillNotDraw(false);
        this.size = 150;
        this.column = 0;
        this.row = 0;
        this.cellY = 0;
        this.cellX = 0;
        this.pointsPerBlock = 10;
    }

    public GameBoardCell(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        this.size = 150;
        this.column = 0;
        this.row = 0;
        this.cellY = 0;
        this.cellX = 0;
        this.pointsPerBlock = 10;
    }

    public GameBoardCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        this.size = 150;
        this.column = 0;
        this.row = 0;
        this.cellY = 0;
        this.cellX = 0;
        this.pointsPerBlock = 10;
    }

    /**
     * Constructor for a game board cell.
     *
     * @param column    the column on the game board.
     * @param row       the row on the game board.
     * @param size      the width and height of this cell.
     * @param x         the cellX coordinate of this cell.
     * @param y         the cellY coordinate of this cell.
     * @param bmp       the Bitmap to set in this cell.
     * @param planet    the planet occupying this cell.
     */
    public GameBoardCell(Context context, int column, int row, int size, int x, int y, Bitmap bmp, PlanetsEnum planet)
    {
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
        this.column = column;
        this.row = row;
        this.size = size;
        this.cellX = x;
        this.cellY = y;
        this.cellBitmap = bmp;
        this.planet = planet;
        this.imageX = x;
        this.imageY = y;
        this.pointsPerBlock = 10;
        this.occupied = false;
        this.destroyed = false;
        this.visited = false;
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawBitmap(cellBitmap, this.imageX, this.imageY, null);
        Log.d("GameBoardCell", "DRAW! " + this.toString());
    }

    @Override
    public String toString()
    {
        return "CELL: column=" + this.column + ", row=" + this.row +", planet=" + this.planet + ", image x,y= " + this.imageX + "," + this.imageY + "super x,y" + this.getX() + "," + this.getY() + ", occupied=" + this.occupied + ", destroyed= " + this.destroyed + ", visited=" +this.visited;
    }

    /**
     * Navigates through the game board to find adjacent matching planets, destroys the block
     * and multiplies the points achieved.
     * @param points the accumulated points.
     * @return the accumulated points.
     */
    protected long traverseForPoints(PlanetsEnum planet, long points)
    {
        if (!this.occupied | this.planet != planet | this.destroyed)
        {
            return points;
        }
        else
        {
            this.destroyed = true;
            points *= this.pointsPerBlock;
            points = this.cellDown != null ? this.cellDown.traverseForPoints(planet, points) : points;
            points = this.cellLeft != null ? this.cellLeft.traverseForPoints(planet, points) : points;
            points = this.cellUp != null ? this.cellUp.traverseForPoints(planet, points) : points;
            points = this.cellRight != null ? this.cellRight.traverseForPoints(planet, points) : points;
            Log.d("GameBoardCell", "Traversing cell " + this.getColumn() + "," + this.getRow() + ", planet=" + planet.toString() + " points=" + points);
            return points;
        }
    }

    /**
     * Navigates through the game board to find adjacent matching planets.
     * @param matches the number of adjacent matching planets.
     * @return the number of adjacent matching planets.
     */
    protected int traverseForMatches(PlanetsEnum planet, int matches)
    {
        if (!this.occupied | this.planet != planet | this.visited)
        {
            return matches;
        }
        else
        {
            this.visited = true;
            matches += 1;
            matches = this.cellDown != null ? this.cellDown.traverseForMatches(planet, matches) : matches;
            matches = this.cellLeft != null ? this.cellLeft.traverseForMatches(planet, matches) : matches;
            matches = this.cellUp != null ? this.cellUp.traverseForMatches(planet, matches) : matches;
            matches = this.cellRight != null ? this.cellRight.traverseForMatches(planet, matches) : matches;
            Log.d("GameBoardCell", "Traversing cell " + this.getColumn() + "," + this.getRow() + ", planet=" + this.getPlanet() + " matches=" + matches);
            return matches;
        }
    }

    /**
     * Sets the Bitmap in this cell.
     * @param bmp the Bitmap to put in this cell.
     */
    protected void setCellBitmap(Bitmap bmp) { this.cellBitmap = bmp; }

    /**
     * Sets the image X coordinate.
     * @param imageX the new image X coordinate.
     */
    protected void setImageX(float imageX)
    {
        this.imageX = imageX;
    }

    /**
     * Sets the image Y coordinate.
     * @param imageY the new image Y coordinate.
     */
    protected void setImageY(float imageY)
    {
        this.imageY = imageY;
    }

    /**
     * Sets whether this cell is occupied by a planet imageView.
     * @param occupied whether this cell is occupied by a planet imageView.
     */
    public void setOccupied(boolean occupied)
    {
        this.occupied = occupied;
    }

    /**
     * Sets whether this cell is marked for destruction.
     * @param markedForDestruction whether this cell is occupied by a planet imageView.
     */
    protected void setDestroyed(boolean markedForDestruction) { this.destroyed = markedForDestruction; }

    /**
     * Sets whether this cell has been traversed.
     */
    protected void setVisited(boolean visited) { this.visited = visited; }

    /**
     * Gets whether this cell is occupied by a planet imageView.
     * @return whether this cell is occupied by a planet imageView.
     */
    public boolean isOccupied()
    {
        return occupied;
    }

    /**
     * Gets the size (width and height) of this cell.
     * @return the size of this cell.
     */
    protected int getSize()
    {
        return this.size;
    }

    /**
     * Gets the cell's column index.
     * @return the cell's column index.
     */
    public int getColumn()
    {
        return this.column;
    }

    /**
     * Gets the cell's row index.
     * @return the cell's row index.
     */
    public int getRow()
    {
        return this.row;
    }

    /**
     * Gets the cellX coordinate of this cell.
     * @return the cellX coordinate of this cell.
     */
    public int getCellX()
    {
        return this.cellX;
    }

    /**
     * Gets the cellY coordinate of this cell.
     * @return the cellY coordinate of this cell.
     */
    public int getCellY()
    {
        return this.cellY;
    }

    /**
     * Gets the image X coordinate.
     * @return the image X coordinate.
     */
    protected float getImageX()
    {
        return this.imageX;
    }

    /**
     * Gets the image Y coordinate.
     * @return the image Y coordinate.
     */
    protected float getImageY()
    {
        return this.imageY;
    }

    /**
     * Gets this cell's Planets enum value.
     * @return the Planets enum value.
     */
    protected PlanetsEnum getPlanet() { return planet; }

    /**
     * Sets this cell's planet.
     * @param planet the new planet.
     */
    protected void setPlanet(PlanetsEnum planet) { this.planet = planet; }

    //TODO: comment the cell getters and setters

    public Bitmap getCellBitmap() { return this.cellBitmap; }

    protected int getPointsPerBlock() { return pointsPerBlock; }


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


    protected boolean isDestroyed()
    {
        return this.destroyed;
    }

    public boolean isVisited() { return this.visited; }

}
