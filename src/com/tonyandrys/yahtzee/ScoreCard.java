package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/29/2013
 *         (C) 2013 - Tony Andrys
 */

// ScoreCard refactor for efficiency
public class ScoreCard {

    // Number of possible fields on this ScoreCard
    public static final int NUMBER_OF_FIELDS = 16;

    // SCORE_FIELD Constants
    public static final int SCORE_FIELD_ONES = 0;
    public static final int SCORE_FIELD_TWOS = 1;
    public static final int SCORE_FIELD_THREES = 2;
    public static final int SCORE_FIELD_FOURS = 3;
    public static final int SCORE_FIELD_FIVES = 4;
    public static final int SCORE_FIELD_SIXES = 5;
    public static final int SCORE_FIELD_BONUS = 6;
    public static final int SCORE_FIELD_3_OF_A_KIND = 7;
    public static final int SCORE_FIELD_4_OF_A_KIND = 8;
    public static final int SCORE_FIELD_FULL_HOUSE = 9;
    public static final int SCORE_FIELD_SM_STRAIGHT = 10;
    public static final int SCORE_FIELD_LG_STRAIGHT = 11;
    public static final int SCORE_FIELD_YAHTZEE = 12;
    public static final int SCORE_FIELD_YAHTZEE_BONUS = 13;
    public static final int SCORE_FIELD_CHANCE = 14;
    public static final int SCORE_FIELD_TOTAL = 15;

    /* Each scoring combination is assumed available if it is set to 0.
    * If a scoring combination is > 0, it is unavailable as it has been used by the player.
    * If a scoring combination == ZEROED_VALUE, the player has "zeroed" it, or selected the combination at the
    * end of the round with a hand that assigned it no points. It is therefore unavailable to be used again.
    * Finally, if a scoring combination is -1, the space is available to be used by the player. */
    final public static int ZEROED_VALUE = 0;
    final public static int AVAILABLE_SCORE = -1;

    /* If the sum of all top half values >= BONUS_THRESHOLD, add VALUE_TOP_HALF_BONUS to the player's score. */
    final public static int BONUS_THRESHOLD = 63;

    // Scores that remain constant regardless of the value on the dice
    final static public int VALUE_FULL_HOUSE = 25;
    final static public int VALUE_TOP_HALF_BONUS = 35;
    final static public int VALUE_YAHTZEE = 50;
    final static public int VALUE_YAHTZEE_BONUS = 100;
    final static public int VALUE_SM_STRAIGHT = 30;
    final static public int VALUE_LG_STRAIGHT = 40;

    /*
    *  A player's scores are stored as an integer array, where each index represents a field on the ScorePad.
    *  [ones, twos, threes, fours, fives, sixes, bonus, 3/Kind, 4/Kind, Full House, Sm. Str, Lg. Str, Yahtzee, Bonus Yahtzee, Chance, Total]
    */
    int[] scores;
    String playerName;

    public ScoreCard() {
        // Reserve 16 indices to represent each field on the scorecard, set all scores to zero to create a blank scorecard
        scores = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Set player name to default value
        playerName = "NO NAME";
    }

    public ScoreCard(String playerName) {
        // Reserve 16 indices to represent each field on the scorecard, set all scores to zero to create a blank scorecard.
        scores = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Set player name to passed value
        this.playerName = playerName;
    }

    /**
     * Returns a score from the a field of the ScoreCard.
     * @param SCORE_FIELD ScoreCard.SCORE_FIELD constant representing the field to return
     * @return returns value of desired score field as an integer
     */
    public int getScore(int SCORE_FIELD) {
        return scores[SCORE_FIELD];
    }

    /**
     * Sets the value of a ScoreCard field to an arbitrary value.
     * @param SCORE_FIELD ScoreCard.SCORE_FIELD constant representing to field to return
     * @param value New integer value for field
     */
    public void setScore(int SCORE_FIELD, int value) {
        scores[SCORE_FIELD] = value;
    }

    /**
     * Returns the player's scores as an array.
     * @return int[] containing the player's scores in the format below:
     * [ones, twos, threes, fours, fives, sixes, bonus, 3/Kind, 4/Kind, Full House, Sm. Str, Lg. Str, Yahtzee, Bonus Yahtzee, Chance, Total]
     */
    public int[] getScoreArray() {
        return scores;
    }

    /**
     * Returns the player's current score, which is the sum of all values contained in fields that have been used.
     * @return
     */
    public int getPlayerScore() {
        return scores[SCORE_FIELD_TOTAL];
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


}
