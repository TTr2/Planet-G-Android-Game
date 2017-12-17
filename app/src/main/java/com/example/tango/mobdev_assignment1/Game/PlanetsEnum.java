package com.example.tango.mobdev_assignment1.Game;

import com.example.tango.mobdev_assignment1.R;

import java.io.Serializable;

/**
 * Enum for planet statistics.
 * Created by tango on 19/11/2017.
 */

public enum PlanetsEnum implements Serializable {

    NULL_PLANET(0, R.drawable.null_image, R.drawable.null_image, "1", 1, "1", "1", "1"),
    MERCURY(1, R.drawable.mercury, R.drawable.mercury_300x300,  "0.330", 0.38f, "4,878", "57,900,000", "88 days"),
    VENUS(2, R.drawable.venus, R.drawable.venus_300x300, "4.87", 0.9f, "12,104", "108,160,000", "224 days"),
    EARTH(3, R.drawable.earth, R.drawable.earth_300x300, "5.97", 1, "12,756", "149,600,000", "365.25 days"),
    MARS(4, R.drawable.mars, R.drawable.mars_300x300, "0.642", 0.38f, "6,794", "227,936,640", "687 days"),
    JUPITER(5, R.drawable.jupiter, R.drawable.jupiter_300x300, "1,898", 2.64f, "142,984", "778,369,000", "11.86 years"),
    SATURN(6, R.drawable.saturn, R.drawable.saturn_300x300, "568", 1.16f, "120,536", "1,427,034,000", "29 years"),
    URANUS(7, R.drawable.uranus, R.drawable.uranus_300x300, "86.8", 1.11f, "51,118", "2,870,658,186", "84 years"),
    NEPTUNE(8, R.drawable.neptune, R.drawable.neptune_300x300, "102", 1.21f, "49,532", "4,496,976,000", "164.8 years");

    private int order;
    private int smallDrawableId;
    private int largeDrawableId;
    private String mass;
    private float gravity;
    private String diameter;
    private String distanceFromSun;
    private String orbit;

    PlanetsEnum(int order, int smallDrawableId, int largeDrawableId, String mass, float gravity,
                String diameter, String distanceFromSun, String orbit)
    {
        this.order = order;
        this.smallDrawableId = smallDrawableId;
        this.largeDrawableId = largeDrawableId;
        this.mass = mass;
        this.gravity = gravity;
        this.diameter = diameter;
        this.distanceFromSun = distanceFromSun;
        this.orbit = orbit;
    }

    public int getOrder() { return order; }
    public int getSmallImageResource() { return smallDrawableId; }
    public int getLargeImageResource() { return largeDrawableId; }
    public String getMass() { return mass; }
    public float getGravity() { return gravity; }
    public String getDiameter() { return diameter; }
    public String getDistanceFromSun() { return distanceFromSun; }
    public String getOrbit() { return orbit; }
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
