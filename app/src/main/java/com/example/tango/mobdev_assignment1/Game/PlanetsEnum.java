package com.example.tango.mobdev_assignment1.Game;

import com.example.tango.mobdev_assignment1.R;

import java.io.Serializable;

/**
 * Enum for planet statistics.
 * Created by tango on 19/11/2017.
 */

public enum PlanetsEnum implements Serializable {

    NULL_PLANET(0, R.drawable.null_image, 1, 1, 1, 1, 1),
    MERCURY(1, R.drawable.mercury, 1, 1, 1, 1, 1),
    VENUS(2, R.drawable.venus, 1, 1, 1, 1, 1),
    EARTH(3, R.drawable.earth, 1, 1, 1, 1, 1),
    MARS(4, R.drawable.mars, 1, 1, 1, 1, 1),
    JUPITER(5, R.drawable.jupiter, 1, 1, 1, 1, 1),
    SATURN(6, R.drawable.saturn, 1, 1, 1, 1, 1),
    URANUS(7, R.drawable.uranus, 1, 1, 1, 1, 1),
    NEPTUNE(8, R.drawable.neptune, 1, 1, 1, 1, 1);

    private int order;
    private int drawableId;
    private float mass;
    private float gravity;
    private long diameter;
    private long distanceFromSun;
    private long distanceFromEarth;

    PlanetsEnum(int order, int drawableId, float mass, float gravity, long diameter, long distanceFromSun,
                long distanceFromEarth)
    {
        this.order = order;
        this.drawableId = drawableId;
        this.mass = mass;
        this.gravity = gravity;
        this.diameter = diameter;
        this.distanceFromSun = distanceFromSun;
        this.distanceFromEarth = distanceFromEarth;
    }

    /**
     * Default constructor for serializing.
     */
    PlanetsEnum()
    {
    }

    public int getOrder() { return order; }
    public int getImageResource() { return drawableId; }
    public float getMass() { return mass; }
    public float getGravity() { return gravity; }
    public long getDiameter() { return diameter; }
    public long getDistanceFromSun() { return distanceFromSun; }
    public long getDistanceFromEarth() { return distanceFromEarth; }
    public static PlanetsEnum getPlanet(int index)
    {
        if (index > PlanetsEnum.values().length)
        {
            index = 1;
        }

        PlanetsEnum planet = PlanetsEnum.EARTH;
        for (PlanetsEnum p : PlanetsEnum.values())
        {
            if (p.order == index)
            {
                planet = p;
                break;
            }
        }
        return planet;
    }


}
