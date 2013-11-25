package com.tonyandrys.yahtzee;

import java.util.HashMap;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */

public class ScoreCard {

    /* Each scoring combination is assumed available if it is set to 0.
    * If a scoring combination is > 0, it is unavailable as it has been used by the player.
    * If a scoring combination == ZEROED_VALUE, the player has "zeroed" it, or selected the combination at the
    * end of the round with a hand that assigned it no points. It is therefore unavailable to be used again. */
    final public static int ZEROED_VALUE = -1;

    /* If the sum of all top half values >= BONUS_THRESHOLD, add VALUE_TOP_HALF_BONUS to the player's score. */
    final public static int BONUS_THRESHOLD = 63;

    // Scores that remain constant regardless of the value on the dice
    final static public int VALUE_FULL_HOUSE = 25;
    final static public int VALUE_TOP_HALF_BONUS = 35;
    final static public int VALUE_YAHTZEE = 50;
    final static public int VALUE_YAHTZEE_BONUS = 100;
    final static public int VALUE_SM_STRAIGHT = 30;
    final static public int VALUE_LG_STRAIGHT = 40;

    // A player's score is divided into two logical sections, the top half containing 1s, 2s, 3s, 4s, 5s, and 6s
    // and a bottom half containing more complex arrangements.
    int topHalfTotal;
    int bottomHalfTotal;

    // Top half values
    private int ones;
    private int twos;
    private int threes;
    private int fours;
    private int fives;
    private int sixes;
    private boolean applyBonus;

    // Bottom half values
    private int threeOfAKind;
    private int fourOfAKind;
    private int fullHouse;
    private int smStraight;
    private int lgStraight;
    private int yahtzee;
    private int chance;
    private int yahtzeeBonusCount;

    public ScoreCard() {
        this.topHalfTotal = 0;
        this.bottomHalfTotal = 0;
        this.ones = 0;
        this.twos = 0;
        this.threes = 0;
        this.fours = 0;
        this.fives = 0;
        this.sixes = 0;
        this.applyBonus = false;
        this.threeOfAKind = 0;
        this.fourOfAKind = 0;
        this.fullHouse = 0;
        this.smStraight = 0;
        this.lgStraight = 0;
        this.yahtzee = 0;
        this.chance = 0;
        this.yahtzeeBonusCount = 0;
    }

    /**
     * Converts this ScoreCard into an Textview Resource ID -> Value mapping, which maps a numerical value to its TextView for displaying on the UI.
     * @return HashMap<Integer, Integer>
     */
    public HashMap<Integer, Integer> toMap() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        /* Resource IDs of TextViews which hold score values.
        * Ones, Twos, Threes, Fours, Fives, Sixes, Upper Subtotal, Upper Bonus, Upper Total, 3/Kind, 4/Kind, Full House, Sm Straight, Lg Straight, Yahtzee, Bonus Yahtzee, Chance, Lower Total, Grand (Upper + Lower) Total */
        int[] resIds = {R.id.ones_value_textview, R.id.twos_value_textview, R.id.threes_value_textview, R.id.fours_value_textview, R.id.fives_value_textview, R.id.sixes_value_textview, R.id.upper_subtotal_value_textview, R.id.upper_bonus_value_textview, R.id.upper_total_value_textview, R.id.three_of_a_kind_value_textview, R.id.four_of_a_kind_value_textview, R.id.full_house_value_textview, R.id.sm_straight_value_textview, R.id.lg_straight_value_textview, R.id.yahtzee_value_textview, R.id.bonus_yahtzee_value_textview, R.id.chance_value_textview, R.id.lower_total_value_textview, R.id.grand_total_value_textview};
        // FIXME: Add Upper Bonus calculation! Right now it is always zero!
        int[] scoreValues = {this.ones, this.twos, this.threes, this.fours, this.fives, this.sixes, this.topHalfTotal, 0, this.topHalfTotal, this.threeOfAKind, this.fourOfAKind, this.fullHouse, this.smStraight, this.lgStraight, this.yahtzee, (this.yahtzee * this.yahtzeeBonusCount), this.chance, this.bottomHalfTotal, (this.topHalfTotal + this.bottomHalfTotal) };

        // Compile both sets into a ResID -> Value HashMap
        for (int i=0; i<resIds.length; i++) {
            map.put(resIds[i], scoreValues[i]);
        }

        return map;
    }

    public int getOnes() {
        return ones;
    }

    public void setOnes(int ones) {
        this.ones = ones;
    }

    public int getTwos() {
        return twos;
    }

    public void setTwos(int twos) {
        this.twos = twos;
    }

    public int getThrees() {
        return threes;
    }

    public void setThrees(int threes) {
        this.threes = threes;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public int getFives() {
        return fives;
    }

    public void setFives(int fives) {
        this.fives = fives;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public boolean isBonusApplied() {
        return applyBonus;
    }

    public void setApplyBonus(boolean applyBonus) {
        this.applyBonus = applyBonus;
    }

    public int getThreeOfAKind() {
        return threeOfAKind;
    }

    public void setThreeOfAKind(int threeOfAKind) {
        this.threeOfAKind = threeOfAKind;
    }

    public int getFourOfAKind() {
        return fourOfAKind;
    }

    public void setFourOfAKind(int fourOfAKind) {
        this.fourOfAKind = fourOfAKind;
    }

    public int getFullHouse() {
        return fullHouse;
    }

    public void setFullHouse(int fullHouse) {
        this.fullHouse = fullHouse;
    }

    public int getSmStraight() {
        return smStraight;
    }

    public void setSmStraight(int smStraight) {
        this.smStraight = smStraight;
    }

    public int getLgStraight() {
        return lgStraight;
    }

    public void setLgStraight(int lgStraight) {
        this.lgStraight = lgStraight;
    }

    public int getYahtzee() {
        return yahtzee;
    }

    public void setYahtzee(int yahtzee) {
        this.yahtzee = yahtzee;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public int getYahtzeeBonus() {
        return yahtzeeBonusCount * VALUE_YAHTZEE_BONUS;
    }

    public void incrementYahtzeeBonus() {
        this.yahtzeeBonusCount++;
    }

    public int getTopHalfTotal() {
        return topHalfTotal;
    }

    public int getBottomHalfTotal() {
        return bottomHalfTotal;
    }
}
