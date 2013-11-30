package com.tonyandrys.yahtzee;

import java.util.HashMap;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/29/2013
 *         (C) 2013 - Tony Andrys
 */

// ScoreCard refactor for efficiency
public class ScoreCard {

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
    * end of the round with a hand that assigned it no points. It is therefore unavailable to be used again. */
    final public static int ZEROED_VALUE = -1;
    final public static int AVAILABLE_SCORE = 0;

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
    *  Scores are stored as an integer array, where each index represents a field on the ScorePad.
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
        // Reserve 16 indices to represent each field on the scorecard, set all scores to zero to create a blank scorecard
        scores = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Set player name to passed value
        this.playerName = playerName;
    }

    /**
     * Returns a score from the field represented by SCORE_FIELD
     * @param SCORE_FIELD SCORE_FIELD integer constant
     * @return
     */
    public int getScore(int SCORE_FIELD) {
        return scores[SCORE_FIELD];
    }

    /**
     * Updates the score represented by SCORE_FIELD to `value`
     * @param SCORE_FIELD Score field integer constant
     * @param value desired value for SCORE_FIELD
     */
    public void setScore(int SCORE_FIELD, int value) {
        scores[SCORE_FIELD] = value;
    }

    /**
     * Compiles the values in this ScoreCard and builds a TextViewResID -> Score Value map to be used in updating the Game UI.
     */
    public HashMap<Integer, Integer> getScoreMap() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        /* Resource IDs of TextViews which hold score values. */
        int[] resIds = {R.id.ones_value_textview, R.id.twos_value_textview, R.id.threes_value_textview, R.id.fours_value_textview, R.id.fives_value_textview, R.id.sixes_value_textview, R.id.upper_bonus_value_textview, R.id.three_of_a_kind_value_textview, R.id.four_of_a_kind_value_textview, R.id.full_house_value_textview, R.id.sm_straight_value_textview, R.id.lg_straight_value_textview, R.id.yahtzee_value_textview, R.id.bonus_yahtzee_value_textview, R.id.chance_value_textview, R.id.grand_total_value_textview};

        // Compile both sets into a ResID -> Value HashMap
        for (int i=0; i<resIds.length; i++) {
            map.put(resIds[i], scores[i]);
        }
        return map;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getOnes() {
        return scores[SCORE_FIELD_ONES];
    }

    public int getTwos() {
        return scores[SCORE_FIELD_TWOS];
    }

    public int getThrees() {
        return scores[SCORE_FIELD_THREES];
    }

    public int getFours() {
        return scores[SCORE_FIELD_FOURS];
    }

    public int getFives() {
        return scores[SCORE_FIELD_FIVES];
    }

    public int getSixes() {
        return scores[SCORE_FIELD_SIXES];
    }

    public int getBonus() {
        return scores[SCORE_FIELD_BONUS];
    }

    public int get3OfAKind() {
        return scores[SCORE_FIELD_3_OF_A_KIND];
    }

    public int get4OfAKind() {
        return scores[SCORE_FIELD_4_OF_A_KIND];
    }

    public int getFullHouse() {
        return scores[SCORE_FIELD_FULL_HOUSE];
    }

    public int getSmStraight() {
        return scores[SCORE_FIELD_SM_STRAIGHT];
    }

    public int getLgStraight() {
        return scores[SCORE_FIELD_LG_STRAIGHT];
    }

    public int getYahtzee() {
        return scores[SCORE_FIELD_YAHTZEE];
    }

    public int getBonusYahtzee() {
        return scores[SCORE_FIELD_YAHTZEE_BONUS];
    }

    public int getChance() {
        return scores[SCORE_FIELD_CHANCE];
    }

    public int getTotal() {
        return scores[SCORE_FIELD_TOTAL];
    }
}
