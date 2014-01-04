package com.tonyandrys.yahtzee;

import android.app.Activity;
import android.util.Log;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/29/2013
 *         (C) 2013 - Tony Andrys
 */

// ScoreCard refactor for efficiency
public class ScoreCard implements Scorable {

    public static final String TAG = ScoreCard.class.getName();

    // Number of possible fields on this ScoreCard
    public static final int NUMBER_OF_FIELDS = 16;

    // SCORE_FIELD Constants
    public static final int SCORE_FIELD_ONES = 0;
    public static final int SCORE_FIELD_TWOS = 1;
    public static final int SCORE_FIELD_THREES = 2;
    public static final int SCORE_FIELD_FOURS = 3;
    public static final int SCORE_FIELD_FIVES = 4;
    public static final int SCORE_FIELD_SIXES = 5;
    public static final int SCORE_FIELD_3_OF_A_KIND = 6;
    public static final int SCORE_FIELD_4_OF_A_KIND = 7;
    public static final int SCORE_FIELD_FULL_HOUSE = 8;
    public static final int SCORE_FIELD_SM_STRAIGHT = 9;
    public static final int SCORE_FIELD_LG_STRAIGHT = 10;
    public static final int SCORE_FIELD_YAHTZEE = 11;
    public static final int SCORE_FIELD_CHANCE = 12;
    public static final int SCORE_FIELD_YAHTZEE_BONUS = 13;

    final public static int ZEROED_VALUE = -1;
    final public static int AVAILABLE_SCORE = 0;

    /* If the sum of all top half values >= BONUS_THRESHOLD, add VALUE_TOP_HALF_BONUS to the player's score. */
    final public static int BONUS_THRESHOLD = 63;

    // Scores that remain constant regardless of the value on the dice
    final static public int VALUE_FULL_HOUSE = 25;
    final static public int VALUE_UPPER_HALF_BONUS = 35;
    final static public int VALUE_YAHTZEE = 50;
    final static public int VALUE_YAHTZEE_BONUS = 100;
    final static public int VALUE_SM_STRAIGHT = 30;
    final static public int VALUE_LG_STRAIGHT = 40;

    /*
    *  A player's scores are stored as an ScoreField Array of length 13- the number of user editable fields on the scorecard. Format:
    *  [ones, twos, threes, fours, fives, sixes, 3/Kind, 4/Kind, Full House, Sm. Str, Lg. Str, Yahtzee, Chance, Bonus Yahtzee]
    */
    ScoreField[] scores;
    int upperTotal;
    int lowerTotal;
    int totalScore;

    boolean isBonusApplied;
    String playerName;


    public ScoreCard(Activity a) {
        // Create 13 scorefields, each representing a field on the scorecard.
        scores = new ScoreField[] {new ScoreField(this, SCORE_FIELD_ONES, a, a), new ScoreField(this, SCORE_FIELD_TWOS, a, a), new ScoreField(this, SCORE_FIELD_THREES, a, a), new ScoreField(this, SCORE_FIELD_FOURS, a, a), new ScoreField(this, SCORE_FIELD_FIVES, a, a), new ScoreField(this, SCORE_FIELD_SIXES, a, a), new ScoreField(this, SCORE_FIELD_3_OF_A_KIND, a, a), new ScoreField(this, SCORE_FIELD_4_OF_A_KIND, a, a), new ScoreField(this, SCORE_FIELD_FULL_HOUSE, a, a), new ScoreField(this, SCORE_FIELD_SM_STRAIGHT, a, a), new ScoreField(this, SCORE_FIELD_LG_STRAIGHT, a, a), new ScoreField(this, SCORE_FIELD_YAHTZEE, a, a), new ScoreField(this, SCORE_FIELD_CHANCE, a, a), new ScoreField(this, SCORE_FIELD_YAHTZEE_BONUS, a, a)};

        // Each player starts with zero points at the beginning of the game.
        totalScore = 0;
        isBonusApplied = false;

        // Set player name to default value for now
        playerName = "NAME";
    }

    /**
     * Returns a score from the a field of the ScoreCard.
     * @param SCORE_FIELD ScoreCard.SCORE_FIELD constant representing the field to return
     * @return returns value of desired score field as an integer
     */
    public int getPlayerScore(int SCORE_FIELD) {
        return scores[SCORE_FIELD].getPlayerScore();
    }

    /**
     * Sets the value of a ScoreCard field to an arbitrary value.
     * @param SCORE_FIELD ScoreCard.SCORE_FIELD constant representing to field to return
     * @param value New integer value for field
     */
    public void setPlayerScore(int SCORE_FIELD, int value) {
        scores[SCORE_FIELD].setPlayerScore(value);

        // After every score update, check to see if the upper bonus has been achieved.
        // If the sum of all upper section scores >= 63, the player receives 35 extra points.
        if (upperTotal >= BONUS_THRESHOLD) {
            upperTotal += VALUE_UPPER_HALF_BONUS;
            Log.v(TAG, "Bonus threshold has been reached! Player receives 35 extra points.");
        }
    }

    /**
     * Checks for the availability of a ScoreField.
     * @param SCORE_FIELD ScoreField ID
     * @return true if ScoreField has a set value, false if it is still available
     */
    public boolean isScoreFieldSet(int SCORE_FIELD) {
        return scores[SCORE_FIELD].isScoreSet();
    }

    /**
     * Returns the player's current score, which is the sum of the upper and lower total scores.
     * @return
     */
    public int getPlayerScore() {
        return upperTotal + lowerTotal;
    }

    /**
     * Increments the total score of the player's upper or lower section.
     * @param SECTION ScoreField.UPPER_SECTION to increment upper section, ScoreField.LOWER_SECTION to increment lower section
     * @param value int value to add to upper/lower section.
     * @throws java.lang.IllegalArgumentException if SECTION != ScoreField.UPPER_SECTION || ScoreField.LOWER_SECTION
     */
    public void incrementPlayerTotalScore(int SECTION, int value) {
        if (SECTION == ScoreField.UPPER_SECTION) {
            upperTotal += value;
            Log.v(TAG, "Upper Section Total is now " + upperTotal);
        } else if (SECTION == ScoreField.LOWER_SECTION) {
            lowerTotal += value;
            Log.v(TAG, "Lower Section Total is now " + lowerTotal);
        } else {
            throw new IllegalArgumentException("You must pass a valid SECTION to increment player score! Received: " + SECTION);
        }
    }

    /**
     * Applies temporary values calculated by ScoreManager to the ScoreFields in this ScoreCard.
     */
    public void applyHandScores(int[] handScores) {
        for (int i=0; i<handScores.length; i++) {
            // Get this ScoreField
            ScoreField sf = scores[i];

            // If this ScoreField does not yet have a permanent score set, it should display a temp score for the user.
            if (!sf.isScoreSet()) {
                sf.setTempScore(handScores[i]);
                Log.v(TAG, "ScoreField (key=" + sf.getKey() + ") temp value set to " + handScores[i]);
            } else {
                Log.v(TAG, "ScoreField (key=" + sf.getKey() + ") has a perm value, ignoring temp value.");
            }
        }
    }

    public String getPlayerName() {

        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


}
