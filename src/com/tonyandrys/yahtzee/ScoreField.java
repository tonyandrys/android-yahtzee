package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee - ScoreField.java
 *
 * @author Tony Andrys
 *         Created: 12/28/2013
 *         (C) 2013 - Tony Andrys
 */

/**
 * Each individual field on the ScoreCard is a ScoreField.
 */
public class ScoreField {

    int value;
    boolean enabled;
    String label;
    int key;

    /**
     * Allows construction of a new enabled ScoreField with an initial value of 0.
     * @param label Label to be displayed next to this ScoreField
     * @param key Unique key which is used to map the value of this ScoreField to the scores array stored in the player's ScoreCard
     */
    public ScoreField(String label, int key) {
        this.label = label;
        this.key = key;
        this.enabled = true;
        this.value = 0;
    }

}
