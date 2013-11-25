package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */

import android.util.Log;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Handles score calculation and recording into a ScoreCard object
 */
public class ScoreManager {

    private final String TAG = ScoreManager.class.getName();

    // Dice counts necessary to score certain combinations
    final static public int COUNT_THREE_OF_A_KIND = 3;
    final static public int COUNT_FOUR_OF_A_KIND = 4;
    final static public int COUNT_YAHTZEE = 5;
    final static public int COUNT_PAIR = 2;

    // TreeSet integer constants for checking if straights exist
    final static public int TREE_COUNT_LARGE_STRAIGHT = 5;
    final static public int TREE_COUNT_SMALL_STRAIGHT = 4;
    final static public int TREE_SMALL_STRAIGHT_DIFFERENCE = 3;

    ScoreCard playerScoreCard;


    /**
     * On construction, generate a blank ScoreCard for the player.
     */
    public ScoreManager() {
        playerScoreCard = new ScoreCard();
    }

    /**
     * Given a set of dice values, this method enumerates the dice and determines the possible scores for every category.
     * @param diceValues integer values of dice
     * @return ScoreCard object with every score line item set to the value it is worth with this hand.
     */
    public ScoreCard calculateHand(int[] diceValues) {

        // Construct a new ScoreCard that represents the values for this hand
        ScoreCard scoreCard = new ScoreCard();

        // Enumerate the dice in the hand and sort them by their values
        ArrayList<Integer> countList = new ArrayList<Integer>();
        countList = enumerateHand(diceValues);

        // Calculate the top half score
        scoreCard = tabulateTopHalf(countList, scoreCard);

        // Calculate straights if they exist
        scoreCard = tabulateStraights(diceValues, scoreCard);

        // Calculate the bottom half score
        scoreCard = tabulateBottomHalf(countList, scoreCard);

        // Return the calculated ScoreCard
        return new ScoreCard();
    }

    /**
     * Enumerates the diceValues by sorting them into groups. Then returns an integer list that contains values which
     * represent the cardinality of each numerical value in the passed set of diceValues. Since this is a zero indexed
     * array, each value is mapped to the (diceVal-1th) index of the returned array.
     *
     * Example: The returned arrayList on the set of dice [1] [3] [5] [5] [6] would be [1, 0, 1, 0, 2, 1].
     * @param diceValues Set of integer values to process
     * @return ArrayList<Integer> containing cardinality of each possible dice value (1-6).
     */
    private ArrayList<Integer> enumerateHand(int[] diceValues) {

        int[] diceCount = {0, 0, 0, 0, 0, 0};

        // Sort the list of diceValues
        for (int i=0; i<diceValues.length; i++) {
            // Get the next value from the array
            int val = diceValues[i];

            // Get the stored count of this integer and increment it.
            diceCount[val-1]++;
        }

        // Once all the dice counts are updated, write the cardinality list and return.
        Log.v(TAG, "Hand Processed." );
        Log.v(TAG, "Dice counts - Ones: " + diceCount[0] + " Twos: " + diceCount[1] + " Threes: " + diceCount[2] + " Fours: " + diceCount[3] + " Fives: " + diceCount[4] + "S ixes: " + diceCount[5]);

        // Convert to ArrayList to ease checking set membership when tabulating score.
        ArrayList<Integer> countList = new ArrayList<Integer>();
        for (int i=0; i<diceCount.length; i++) {
            countList.add(i, diceCount[i]);
        }

        return countList;
    }

    /**
     * Tabulates the top half scores based on the passed list of dice counts and the player's current scores.
     * @param countList Integer list representing dice counts.
     * @param scoreCard ScoreCard object to write calculations to.
     * @return Returns the passed ScoreCard object with the top half scores written to it.
     */
    public ScoreCard tabulateTopHalf(ArrayList<Integer> countList, ScoreCard scoreCard) {

        /* Check if each top half score combination is available. If it has not been used to score a previous hand,
         * calculate the score and write it to the passed ScoreCard.
         * Top Half Scoring is trivial: score = |diceVal| * diceVal
         */
        Log.v(TAG, "Calculating Top Half Values for this hand...");

        // Ones
        if (playerScoreCard.getOnes() == 0) {
            int ones = countList.get(0);
            scoreCard.setOnes(ones);
            Log.v(TAG, "Ones: " + scoreCard.getOnes());
        }

        // Twos
        if (playerScoreCard.getTwos() == 0) {
            scoreCard.setTwos(countList.get(1) * 2);
            Log.v(TAG, "Twos: " + scoreCard.getTwos());
        }

        // Threes
        if (playerScoreCard.getThrees() == 0) {
            scoreCard.setThrees(countList.get(2) * 3);
            Log.v(TAG, "Threes: " + scoreCard.getThrees());
        }

        // Fours
        if (playerScoreCard.getFours() == 0) {
            scoreCard.setFours(countList.get(3) * 4);
            Log.v(TAG, "Fours: " + scoreCard.getFours());
        }

        // Fives
        if (playerScoreCard.getFives() == 0) {
            scoreCard.setFives(countList.get(4) * 5);
            Log.v(TAG, "Fives: " + scoreCard.getFives());
        }

        // Sixes
        if (playerScoreCard.getSixes() == 0) {
            scoreCard.setSixes(countList.get(5) * 6);
            Log.v(TAG, "Sixes: " + scoreCard.getSixes());
        }

        return scoreCard;
    }

    /**
     * Checks for straights in this hand of diceValues and writes them to the passed ScoreCard.
     * @param diceValues Set of dice values as integers
     * @return Returns the passed ScoreCard object with straight scores written to it.
     */
    public ScoreCard tabulateStraights(int[] diceValues, ScoreCard scoreCard) {

        // Construct a TreeSet and add dice values to it. A TreeSet conveniently removes duplicates and we get
        // sorting for free. Thanks to Gabriel Negut of StackOverflow for this optimization!
        TreeSet<Integer> enumTree = new TreeSet<Integer>();
        for (int i=0; i<diceValues.length; i++) {
            enumTree.add(diceValues[i]);
        }

        /* A Large Straight is represented in the TreeSet if it contains five integers. This means the hand contains no
        duplicate values, and due to the number of possible combinations on a six sided die, the only arrangements
        which yield 5 unique values are in sequential order. */
        if (playerScoreCard.getLgStraight() == 0 && enumTree.size() == TREE_COUNT_LARGE_STRAIGHT) {
            scoreCard.setLgStraight(ScoreCard.VALUE_LG_STRAIGHT);
            Log.v(TAG, "---");
            Log.v(TAG, "LgStraight: " + scoreCard.getLgStraight());

        }

        /* A Small Straight is represented in the TreeSet if it contains four integers AND the difference between the
         * largest and smallest integers is 3. */
        if ((playerScoreCard.getSmStraight() == 0) && (enumTree.last() - enumTree.first() == TREE_SMALL_STRAIGHT_DIFFERENCE)) {
            scoreCard.setSmStraight(ScoreCard.VALUE_SM_STRAIGHT);
            Log.v(TAG, "SmStraight: " + scoreCard.getSmStraight());
            Log.v(TAG, "---");
        }

        return scoreCard;

    }

    /**
     * Tabulates the bottom half scores based on passed array of dice counts and the player's current scores.
     * @param countList List of dice counts.
     * @param scoreCard ScoreCard object to write calculated values to.
     * @Return Returns the passed ScoreCard object with the top half scores written to it.
     */
    public ScoreCard tabulateBottomHalf(ArrayList<Integer> countList, ScoreCard scoreCard) {

        // Bottom half scores often award points based on the sum of the hand, so calculate this now. FIXME: OPTIMIZATION! perform this only if necessary (i.e. a 3ofakind, 4ofakind, chance hasn't been used, etc)
        int diceSum = 0;
        for (int i=0; i<countList.size(); i++) {
            diceSum += (countList.get(i) * (i+1));
        }

        Log.v(TAG, "---");
        Log.v(TAG, "Sum of all dice is: " + Integer.toString(diceSum));

        // FIXME: Most of this can be optimized when confirmed it works! Ex - A yahtzee implies of 4 of a kind, a 4 of a kind implies of 3 of a kind, etc. No need to check and perform all of those linear scans.

        // Three of a Kind - Applies if three of the same valued dice exist in the hand.
        // Score == sum of all dice in the hand
        if (playerScoreCard.getThreeOfAKind() == 0 && countList.contains(COUNT_THREE_OF_A_KIND)) {
            scoreCard.setThreeOfAKind(diceSum);
            Log.v(TAG, "Three Of A Kind: " + scoreCard.getThreeOfAKind());
        }

        // Full House - Applies if the hand consists of a three of a kind and a pair.
        if (playerScoreCard.getFullHouse() == 0 && countList.contains(COUNT_THREE_OF_A_KIND) && countList.contains(COUNT_PAIR)) {
            scoreCard.setFullHouse(ScoreCard.VALUE_FULL_HOUSE);
            Log.v(TAG, "Full House: " + scoreCard.getFullHouse());
        }

        // Four of a Kind - Applies if four of the same valued dice exist in the hand.
        if (playerScoreCard.getFourOfAKind() == 0 && countList.contains(COUNT_FOUR_OF_A_KIND)) {
            scoreCard.setFourOfAKind(diceSum);
            Log.v(TAG, "Four Of A Kind: " + scoreCard.getFourOfAKind());
        }

        // Yahtzee - Applies if all five dice values are identical.
        // FIXME: Must add in the capability to score multiple yahtzees!
        if (playerScoreCard.getYahtzee() == 0 && countList.contains(COUNT_YAHTZEE)) {
            scoreCard.setYahtzee(ScoreCard.VALUE_YAHTZEE);
            Log.v(TAG, "Yahtzee: " + scoreCard.getYahtzee());
        }

        // Chance - The wildcard score is simply the sum of all dice in the hand.
        if (playerScoreCard.getChance() == 0) {
            scoreCard.setChance(diceSum);
            Log.v(TAG, "Chance: " + scoreCard.getChance());
        }
        Log.v(TAG, "---");

        return scoreCard;
    }

    public void getTopHalfScore() {

    }

    public void getTotalScore() {

    }

}
