package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
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
    final static public int TREE_LARGE_STRAIGHT_DIFFERENCE = 4;

    private ScoreCard playerScoreCard;
    private HashSet availableScoreFields;


    // Each score field's possible value based off of the values of the dice rolled (hand values) are calculated and stored in this array
    // Format: [ones, twos, threes, fours, fives, sixes, 3/Kind, 4/Kind, Full House, Sm. Str, Lg. Str, Yahtzee, Chance]
    int[] handScores;

    /**
     * On construction, generate a blank ScoreCard for the player.
     */
    public ScoreManager(Activity activity) {
        playerScoreCard = new ScoreCard(activity);

        // Create a blank integer array to store calculated hand values
        handScores = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Helper Method to allow GameActivity to access player's total score
     * @return player's total score as an integer
     */
    public int getPlayerScore() {
        return playerScoreCard.getTotalScore();
    }

    // HEY LISTEN!
    // This nonsense temporarily disabled because I'm trying to get rid of all this stupid map passing all together

    /**
     * Builds a display map (TextView resId -> Value), which is a ScoreCard representation suitable for display to the user.
     * @param mapType ScoreManager.MAP_TYPE_PLAYER_SCORES for a map containing all fields of the player's ScoreCard, ScoreManager.MAP_TYPE_HAND_SCORES for the values of the current hand.
     * @throws IllegalArgumentException if mapType != ScoreManager.MAP_TYPE_HAND_SCORES or ScoreManager.MAP_TYPE_PLAYER_SCORES
     *
     * @return ScoreMap of type requested by `mapType`
     */
    /*public HashMap<Integer, Integer> getPlayerScoreDisplayMap(int mapType) {
        HashMap<Integer, Integer> map;

        if (mapType == MAP_TYPE_PLAYER_SCORES) {
            int[] scores = playerScoreCard.getPlayerScoreArray();
            map = buildDisplayMap(scores);
            Log.v(TAG, "Score Map Built:");
            Log.v(TAG, map.toString());
        } else if (mapType == MAP_TYPE_HAND_SCORES) {
            map = buildDisplayMap(handScores);
            Log.v(TAG, "Hand Map Built:");
            Log.v(TAG, map.toString());
        } else {
            throw new IllegalArgumentException("Invalid Map Type passed to buildDisplayMap()!");
        }
        return map;
    }*/

    /**
     * Builds a display map from an arbitrary array of size 16. (16 being the number of fields on the scorecard).
     * @return HashMap<Integer, Integer> map of (TextView resID[k] -> a[k])
     */
    /*private HashMap<Integer, Integer> buildDisplayMap(int[] a) {

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        // Compile player scores into a HashMap and return
        for (int i=0; i<resIds.length; i++) {
            map.put(resIds[i], a[i]);
        }
        return map;
    }*/


    /**
     * Given a set of dice values, this method enumerates the dice and determines the possible scores for every category.
     * @param diceValues integer values of dice
     */
    public void calculateHand(int[] diceValues) {

        // Clear handScores values from last turn.
        handScores = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        clearHandScores();

        // Enumerate the dice in the hand and sort them by their values. 
        ArrayList<Integer> countList = enumerateHand(diceValues);

        // The following 3 functions will populate their respective portions of `handScores`. When tabulateBottomHalf()
        // is complete, the array will be filled.
        tabulateTopHalf(countList); // Calculate the top half score
        tabulateStraights(diceValues, countList); // Calculate straights if they exist
        tabulateBottomHalf(countList); // Calculate the bottom half score

        // fill ScoreFields on screen with calculated temp scores
        playerScoreCard.applyHandScores(handScores);

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
        Log.v(TAG, "Dice counts - Ones: " + diceCount[0] + " Twos: " + diceCount[1] + " Threes: " + diceCount[2] + " Fours: " + diceCount[3] + " Fives: " + diceCount[4] + " Sixes: " + diceCount[5]);

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
     */
    private void tabulateTopHalf(ArrayList<Integer> countList) {

        /* Check if each top half score combination is available. If it has not been used to score a previous hand,
         * calculate the score and write it to the field's corresponding index in handScores.
         * Top Half Scoring is trivial: score = |diceVal| * diceVal
         */
        Log.v(TAG, "Calculating Top Half Values for this hand...");

        // Ones
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_ONES) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_ONES] = countList.get(0);
            Log.v(TAG, "Ones: " + handScores[ScoreCard.SCORE_FIELD_ONES]);
        }

        // Twos
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_TWOS) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_TWOS] = countList.get(1) * 2;
            Log.v(TAG, "Twos: " + handScores[ScoreCard.SCORE_FIELD_TWOS]);
        }

        // Threes
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_THREES) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_THREES] = countList.get(2) * 3;
            Log.v(TAG, "Threes: " + handScores[ScoreCard.SCORE_FIELD_THREES]);
        }

        // Fours
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_FOURS) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_FOURS] = countList.get(3) * 4;
            Log.v(TAG, "Fours: " + handScores[ScoreCard.SCORE_FIELD_FOURS]);
        }

        // Fives
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_FIVES) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_FIVES] = countList.get(4) * 5;
            Log.v(TAG, "Fives: " + handScores[ScoreCard.SCORE_FIELD_FIVES]);
        }

        // Sixes
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_SIXES) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_SIXES] = countList.get(5) * 6;
            Log.v(TAG, "Sixes: " + handScores[ScoreCard.SCORE_FIELD_SIXES]);
        }

    }

    /**
     * Checks for straights in this hand of diceValues and writes them to the passed ScoreCard.
     * @param diceValues Set of dice values as integers
     * @param diceCounts Set of dice counts as integers
     */
    private void tabulateStraights(int[] diceValues, ArrayList<Integer> diceCounts) {

        Log.v(TAG, "Calculating Straight Values for this hand...");

        // Construct a TreeSet and add dice values to it. A TreeSet conveniently removes duplicates and we get
        // sorting for free. Thanks to Gabriel Negut of StackOverflow for this optimization!
        TreeSet<Integer> enumTree = new TreeSet<Integer>();
        for (int i=0; i<diceValues.length; i++) {
            enumTree.add(diceValues[i]);
        }

        /* A Large Straight is represented in the TreeSet if it contains five integers AND the difference between the largest and smallest integer is 4. */
        if ((playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_LG_STRAIGHT) == ScoreCard.AVAILABLE_SCORE) && (enumTree.size() == TREE_COUNT_LARGE_STRAIGHT) && (enumTree.last() - enumTree.first() == TREE_LARGE_STRAIGHT_DIFFERENCE)) {
            handScores[ScoreCard.SCORE_FIELD_LG_STRAIGHT] = ScoreCard.VALUE_LG_STRAIGHT;
            Log.v(TAG, "LgStraight: " + handScores[ScoreCard.SCORE_FIELD_LG_STRAIGHT]);

        }

        // Disabling TreeSet calculation of Small Straight due to calculation bugs. Changing to a more naive approach for now.
        // 3 possible Small Straight arrangements exist - 1,2,3,4 or 2,3,4,5 or 3,4,5,6. Therefore, if diceVals show that those dice exist somewhere in the hand, a small straight exists in the set of dice.
        boolean smStrFound = false;

        // Check for {1,2,3,4}
        if ((diceCounts.get(0) >= 1) && (diceCounts.get(1) >= 1) && (diceCounts.get(2) >= 1) && (diceCounts.get(3) >= 1)) {
            Log.v(TAG, "SMSTR: {1,2,3,4} found!");
            smStrFound = true;
        }

        // Check for {2,3,4,5}
        else if ((diceCounts.get(1) >= 1) && (diceCounts.get(2) >= 1) && (diceCounts.get(3) >= 1) && (diceCounts.get(4) >= 1)) {
            Log.v(TAG, "SMSTR: {2,3,4,5} found!");
            smStrFound = true;
        }

        // Check for {3,4,5,6}
        else if ((diceCounts.get(2) >= 1) && (diceCounts.get(3) >= 1) && (diceCounts.get(4) >= 1) && (diceCounts.get(5) >= 1)) {
            Log.v(TAG, "SMSTR: {3,4,5,6} found!");
            smStrFound = true;
        }

        // Set result of small straight calculation to handScores field if it is available and a small straight was found above
        if ( (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_SM_STRAIGHT) == ScoreCard.AVAILABLE_SCORE) && (smStrFound) ) {
            handScores[ScoreCard.SCORE_FIELD_SM_STRAIGHT] = ScoreCard.VALUE_SM_STRAIGHT;
            Log.v(TAG, "SmStraight: " + handScores[ScoreCard.SCORE_FIELD_SM_STRAIGHT]);
        }

        /* A Small Straight is represented in the TreeSet if it contains four integers AND the difference between the
         * largest and smallest integers is 3. */
        //if ((playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_SM_STRAIGHT)) == ScoreCard.AVAILABLE_SCORE && (enumTree.size() == TREE_COUNT_SMALL_STRAIGHT) && (enumTree.last() - enumTree.first() == TREE_SMALL_STRAIGHT_DIFFERENCE)) {
        //    handScores[ScoreCard.SCORE_FIELD_SM_STRAIGHT] = ScoreCard.VALUE_SM_STRAIGHT;
        //    Log.v(TAG, "SmStraight: " + handScores[ScoreCard.SCORE_FIELD_SM_STRAIGHT]);
        //}

    }

    /**
     * Tabulates the bottom half scores based on passed array of dice counts and the player's current scores.
     * @param countList List of dice counts.
     */
    private void tabulateBottomHalf(ArrayList<Integer> countList) {

        // Bottom half scores often award points based on the sum of the hand, so calculate this now. FIXME: OPTIMIZATION! perform this only if necessary (i.e. a 3ofakind, 4ofakind, chance hasn't been used, etc)
        int diceSum = 0;
        for (int i=0; i<countList.size(); i++) {
            diceSum += (countList.get(i) * (i+1));
        }

        Log.v(TAG, "Calculating Bottom Half Values for this hand...");


        Log.v(TAG, "---");
        Log.v(TAG, "Sum of all dice is: " + Integer.toString(diceSum));

        // FIXME: Most of this can be optimized when confirmed it works! Ex - A yahtzee implies of 4 of a kind, a 4 of a kind implies of 3 of a kind, etc. No need to check and perform all of those linear scans.

        // Three of a Kind - Applies if three of the same valued dice exist in the hand.
        // Score == sum of all dice in the hand
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_3_OF_A_KIND) == 0 && countList.contains(COUNT_THREE_OF_A_KIND)) {
            handScores[ScoreCard.SCORE_FIELD_3_OF_A_KIND] = diceSum;
            Log.v(TAG, "Three Of A Kind: " + handScores[ScoreCard.SCORE_FIELD_3_OF_A_KIND]);
        } else {
            Log.v(TAG, "Three/Kind is unavailable! Value in ScoreCard is " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_3_OF_A_KIND));
        }

        // Full House - Applies if the hand consists of a three of a kind and a pair.
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_FULL_HOUSE) == ScoreCard.AVAILABLE_SCORE && countList.contains(COUNT_THREE_OF_A_KIND) && countList.contains(COUNT_PAIR)) {
            handScores[ScoreCard.SCORE_FIELD_FULL_HOUSE] = ScoreCard.VALUE_FULL_HOUSE;
            Log.v(TAG, "Full House: " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_FULL_HOUSE));
        } else {
            Log.v(TAG, "Full House is unavailable! Value in ScoreCard is " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_FULL_HOUSE));
        }

        // Four of a Kind - Applies if four of the same valued dice exist in the hand.
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_4_OF_A_KIND) == ScoreCard.AVAILABLE_SCORE && countList.contains(COUNT_FOUR_OF_A_KIND)) {
            handScores[ScoreCard.SCORE_FIELD_4_OF_A_KIND] = diceSum;
            Log.v(TAG, "Four Of A Kind: " + handScores[ScoreCard.SCORE_FIELD_4_OF_A_KIND]);
        } else {
            Log.v(TAG, "Four/Kind is unavailable! Value in ScoreCard is " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_4_OF_A_KIND));
        }

        // Yahtzee - Applies if all five dice values are identical.
        // FIXME: Must add in the capability to score multiple yahtzees!
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_YAHTZEE) == ScoreCard.AVAILABLE_SCORE && countList.contains(COUNT_YAHTZEE)) {
            handScores[ScoreCard.SCORE_FIELD_YAHTZEE] = ScoreCard.VALUE_YAHTZEE;
            Log.v(TAG, "Yahtzee: " + handScores[ScoreCard.SCORE_FIELD_YAHTZEE]);
        } else {
            Log.v(TAG, "Yahtzee is unavailable! Value in ScoreCard is " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_YAHTZEE));
        }

        // Chance - The wildcard score is simply the sum of all dice in the hand.
        if (playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_CHANCE) == ScoreCard.AVAILABLE_SCORE) {
            handScores[ScoreCard.SCORE_FIELD_CHANCE] = diceSum;
            Log.v(TAG, "Chance: " + handScores[ScoreCard.SCORE_FIELD_CHANCE]);
        } else {
            Log.v(TAG, "Chance is unavailable! Value in ScoreCard is " + playerScoreCard.getPlayerScore(ScoreCard.SCORE_FIELD_CHANCE));
        }

    }

    /**
     * Returns the TextView ResIDs that have not been used or "zeroed" as a Set.
     * HashSet is used for efficiency when checking for membership. HashSet.contains() runs in constant time.
     * @return HashSet<Integer> of all available ResIDs.
     */
    /*public HashSet<Integer> getAvailableScoreFields() {
        // Get the array of scores from ScoreCard
        int[] s = playerScoreCard.getPlayerScoreArray();

        // Grab all values that are available from s and map them to their ResIDs.
        HashSet<Integer> returnSet = new HashSet<Integer>();
        for (int i=0; i<s.length; i++) {
            // check if this field is available
            if (s[i] == ScoreCard.AVAILABLE_SCORE) {
                returnSet.add(resIds[i]); // add ResID
                Log.v(TAG, "Score Field " + i + " is available");
            }
        }

        return returnSet;
    }*/

    /**
     * Applies the temp values calculated in handScores to the ScoreFields in the player's ScoreCard.
     */
    public void applyHandScores() {
        playerScoreCard.applyHandScores(handScores);
    }

    /**
     * Checks if a specific ScoreField has a set value, or if it is still available to be used
     * @param scorefieldKey ScoreCard.SCORE_FIELD identifer to determine which field to check
     * @return true if field is set, false if field is still available.
     */
    public boolean isScoreFieldSet(int scorefieldKey) {
        return playerScoreCard.isScoreFieldSet(scorefieldKey);
    }

    /**
     * Checks the state of the bonus flag on the player's scorecard and returns its state.
     * @return true if bonus has been applied, false if it has not yet been applied.
     */
    public boolean isBonusApplied() {
        return playerScoreCard.isBonusApplied();
    }

    /**
     * Clears any information in the hand scores array to be ready for the next calculation.
     */
    public void clearHandScores() {
        int[] blank = new int[] {0,0,0,0,0,0,0,0,0,0,0,0,0};
        playerScoreCard.applyHandScores(blank);
    }

    /**
     * Retrieves the player's total score from their ScorePad.
     * This method is primarily used as an interface between GameActivity and the player's ScoreCard object.
     * @return Player's current total score (upper section + lower section + bonuses)
     */
    public int getTotalScore() {
        Log.v(TAG, "Total score is " + playerScoreCard.getTotalScore());
        return playerScoreCard.getTotalScore();
    }

    /**
     * Sets the value of an arbitrary field on the scorepad to the passed value.
     * This method is primarily used as an interface between GameActivity and the player's ScoreCard object.
     * @param SCORE_FIELD ScoreField.SCORE_FIELD key, representing one of the 13 user-writable sections of the scorepad
     * @param value Value to write to scorefield
     */
    public void writeScore(int SCORE_FIELD, int value) {
        playerScoreCard.setPlayerScore(SCORE_FIELD, value);
        Log.v(TAG, "Wrote " + value + " to score field " + SCORE_FIELD);
    }

}
