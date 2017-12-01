package com.example.tango.mobdev_assignment1;

import android.content.Context;
import android.test.ServiceTestCase;
import android.widget.ImageView;

import com.example.tango.mobdev_assignment1.Game.GameBoard;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class gameboardUnitTest {



    @Test
    public void moveYSwitchesCell() throws Exception {


        // setup
        Context context = getTestContext();
        GameBoard gameboard = new GameBoard(context);
        int viewWidth = 1080;
        int viewHeight = 1920;
        int columns = 7;
        int cellSize = viewWidth / columns;
        int rows = viewHeight / cellSize;
        int startingColumn = 3; // half of 7, zero indexed
        int startingRow = rows - 1; // zero indexed

        ImageView activeBlockImage = new ImageView(context);
        float velocity = 5;

        // implement
        gameboard.addToGameBoard(activeBlockImage);
        assertEquals(gameboard.getActiveCell().getRow(), startingRow);
        assertEquals(gameboard.getActiveImage().getY(), gameboard.getActiveCell().getCellY());
        gameboard.moveBlockY(velocity);

        // assert

        assertEquals(gameboard.getActiveCell().getRow(), startingRow -1);
        assertEquals(gameboard.getActiveImage().getY(), gameboard.getActiveCell().getCellY() + velocity);
    }

    /**
     * @return The {@link Context} of the test project.
     */
    private Context getTestContext()
    {
        try
        {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

}
        /*
         * Checks the game board for three adjacent matching planet blocks.
         * @param cell the starting cell.
         * @return whether the cell has three matching adjacent planet blocks.
         */
    /*
    protected int checkForThreeMatchingPlanets(GameBoardCell cell)
    {
        return cell.traverseForMatches(cell.getPlanet(), 0);
    }

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
*/