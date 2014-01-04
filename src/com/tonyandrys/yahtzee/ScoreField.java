package com.tonyandrys.yahtzee;

/**
 * com.tonyandrys.yahtzee - ScoreField.java
 *
 * @author Tony Andrys
 *         Created: 12/28/2013
 *         (C) 2013 - Tony Andrys
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * Each individual field of the ScoreCard that the player can interact with is a ScoreField.
 * ScoreFields control the UI of the fields on the scorepad during the game.
 */
public class ScoreField {

    final private String TAG = "ScoreField - Key=" + Integer.toString(this.key);

    // Upper vs Lower score selection contants
    public static final int UPPER_SECTION = 0;
    public static final int LOWER_SECTION = 1;

    // Upper Section consists of keys 0-5 (ones - sixes)
    public static final int UPPER_SECTION_MIN_KEY = 0;
    public static final int UPPER_SECTION_MAX_KEY = 5;

    // Lower Section consists of keys 6-13 (3/kind - yahtzee bonus)
    public static final int LOWER_SECTION_MIN_KEY = 6;
    public static final int LOWER_SECTION_MAX_KEY = 13;

    // Callback to modify player's scores
    ScoreCard sc;

    TextView tv;
    Context context;
    int score;
    int tempScore;
    boolean hasScore;
    int key;

    /**
     * Allows construction of a new enabled ScoreField with an initial value of 0.
     * @param key Unique key which is used to map the parameters of this ScoreField to a specific TextView visable to the user. These are constants defined in ScoreCard.
     */
    public ScoreField(ScoreCard callback, int key, Activity activity, Context context) {
        this.sc = callback;
        this.key = key;
        this.hasScore = false;
        this.score = 0;
        this.tempScore = 0;
        this.context = activity.getApplicationContext();

        // Find the TextView in the UI associated with this ScoreField
        TableLayout tl = (TableLayout)activity.findViewById(R.id.scorepad_container_tablelayout);
        Log.v(TAG, "TableLayout ID is "+ tl.getId());
        this.tv = (TextView)tl.findViewWithTag(this.key);
        assert(tl != null);
        assert(this.tv != null);
    }

    public int getKey() {
        return this.key;
    }

    public int getPlayerScore() {
        return this.score;
    }

    public int getTempScore() {
        return this.tempScore;
    }

    /**
     * Checks if the score of this ScoreField has been set by the player.
     * @return true if score has been set, false if it is still available to be used.
     */
    public boolean isScoreSet() {
        return hasScore;
    }

    /**
     * Sets the temporary score for this ScoreField
     * @param tempScore integer to store as temp value
     */
    public void setTempScore(int tempScore) {
        // if this ScoreField has no permanent score, it can still be selected by the player, so display this temporary score on its associated TextView.
        if (!hasScore) {
            this.tempScore = tempScore;
            refreshView();
        } else {
            Log.w(TAG, "Temp Score was ignored, ScoreField has a score for this round!");
        }
    }

    /**
     * Sets the permanent score for this ScoreField. This is called when a player wishes to lock in the tempValue of this field and end their round.
     * @param score integer to store as
     */
    public void setPlayerScore(int score) {
        // When a player's score is set, set the score and hasScore flags to ensure it cannot be changed until the end of the game.
        this.score = score;
        this.hasScore = true;

        // Now, update the player's total score stored in ScoreCard.
        if (this.key <= UPPER_SECTION_MAX_KEY) {
            // This ScoreField is in the upper section
            Log.v(TAG, "Updating upper section total...");
            sc.incrementPlayerTotalScore(UPPER_SECTION, score);
        } else if (this.key <= LOWER_SECTION_MAX_KEY) {
            // This ScoreField is in the lower section
            Log.v(TAG, "Updating lower section total...");
            sc.incrementPlayerTotalScore(LOWER_SECTION, score);
        } else {
            Log.w(TAG, "Check ScoreField Key, something is screwed up here! Key:" + this.key);
        }

        // After the model is updated, refresh the view.
        refreshView();
    }

    /**
     * Refreshes this ScoreField's display parameters. Calling this will display the appropriate value for this ScoreField.
     */
    public void refreshView() {
        // If this ScoreField has a saved score, it should display the stored value in black.
        if (hasScore) {
            this.tv.setTextColor(this.context.getResources().getColor(R.color.used_scorepad_field));
            this.tv.setText(this.score);
        } else {
            // If this ScoreField does not have a saved score, it should display its temporary value in gray.
            this.tv.setTextColor(this.context.getResources().getColor(R.color.available_scorepad_field));
            this.tv.setText(this.tempScore);
        }
    }
}
