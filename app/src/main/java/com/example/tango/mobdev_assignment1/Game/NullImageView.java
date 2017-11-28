package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;
import android.graphics.Canvas;

import com.example.tango.mobdev_assignment1.R;

/**
 * Represents a cell in the gameboard that does not contain a PlanetBlockImageView
 * Created by tango on 21/11/2017.
 */

public class NullImageView extends android.support.v7.widget.AppCompatImageView {

    /**
     * Constructor for a NullPlanetBlockImageView object.
     * @param context
     */
    public NullImageView(Context context)
    {
        super(context);
    }

    /**
     * Does nothing.
     * @param canvas the canvas.
     */
    @Override
    public void onDraw(Canvas canvas){
        // Do nothing
    }

}
