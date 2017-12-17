package com.example.tango.mobdev_assignment1.HiScores;

import android.support.annotation.NonNull;

/**
 * An object representing a high score.
 * Created by tango on 01/12/2017.
 */

public class HiScore implements Comparable{

    private long score;
    private int level;
    private String name;


    /**
     * Constructor for Hi Score object.
     * @param score the hi score.
     * @param level the level.
     * @param name the player name.
     */
    public HiScore(long score, int level, String name) {
        this.score = score;
        this.level = level;
        this.name = name;
    }



    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }

    protected long getScore() { return score; }
    protected int getLevel() { return level; }
    protected String getName() { return name; }

}
