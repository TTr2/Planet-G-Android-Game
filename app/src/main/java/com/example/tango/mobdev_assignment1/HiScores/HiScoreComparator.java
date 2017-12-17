package com.example.tango.mobdev_assignment1.HiScores;

import java.util.Comparator;

/**
 * Comparator for Hi Score objects based on the score, or level if tied.
 * Created by tango on 01/12/2017.
 */
public class HiScoreComparator implements Comparator {

    /**
     * Compares two hi scores based on the score, or level if tied, for sorting in descending order.
     * @param o1 the first hi score.
     * @param o2 the 2nd hi score.
     * @return the comparator integer result.
     */
    @Override
    public int compare(Object o1, Object o2) {
        HiScore hs1 = (HiScore) o1;
        HiScore hs2 = (HiScore) o2;

        return hs1.getScore()
                < hs2.getScore()
                ? 1
                : hs1.getScore() > hs2.getScore()
                    ? -1
                    : hs1.getLevel() < hs2.getLevel()
                        ? 1
                        : hs1.getLevel() == hs2.getLevel()
                            ? 0
                            : -1;
    }
}
