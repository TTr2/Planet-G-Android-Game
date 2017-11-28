package com.example.tango.mobdev_assignment1.Game;

import android.content.Context;

import java.io.Serializable;

/**
 * A custom ImageView representing a game block that player has to place on the game board.
 * Blocks display a planetary image and move down screen at varying speeds depending on this
 * planet's gravity compared to the currently selected planet, the game level and the player's
 * application of the device's gyroscope.
 * Created by tango on 19/11/2017.
 */

public class PlanetBlockImageView extends android.support.v7.widget.AppCompatImageView
        implements Serializable{

    private PlanetsEnum planet;

    /**
     * Constructor for PlanetBlockImageView objects.
     * @param context the context of the com.example.tango.mobdev_assignment1.Game.GameSurfaceView
     * @param planet the planet enum.

     */
    public PlanetBlockImageView(Context context, PlanetsEnum planet) {
        super(context);
        this.planet = planet;
        this.setImageResource(planet.getImageResource());
    }

    /**
     * Gets the Planets enum value.
     * @return the Planets enum value.
     */
    protected PlanetsEnum getPlanet() { return planet; }
}
