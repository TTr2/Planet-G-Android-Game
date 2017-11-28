package com.example.tango.mobdev_assignment1;

import android.content.Context;
import android.test.ServiceTestCase;

import com.example.tango.mobdev_assignment1.Game.GameBoard;
import com.example.tango.mobdev_assignment1.Game.PlanetBlockImageView;
import com.example.tango.mobdev_assignment1.Game.PlanetsEnum;

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

        PlanetBlockImageView activeBlockImage = new PlanetBlockImageView(context, PlanetsEnum.EARTH);
        float velocity = 5;

        // implement
        gameboard.addNewBlock(activeBlockImage);
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