package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 01/03/2014
 *         (C) 2013 - Tony Andrys
 */

/**
 * Callback interface to allow ScoreFields to increment the total scores stored in ScoreCard
 */
public interface Scorable {
    void incrementPlayerTotalScore(int SECTION, int value);
}

