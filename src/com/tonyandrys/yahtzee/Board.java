package com.tonyandrys.yahtzee;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */
public class Board {

    public final String TAG = this.getClass().getName();

    int playerScore;
    boolean isPlaying; // Main loop condition. False to end game.
    int handScore;
    Context context;

    // Dice are stored in a basic array from [0-4].
    private Die[] dice;

    public Board(Context context) {

        this.context = context;
        Log.v(TAG, "Board initialized! New game is starting now.");

        // Set new game parameters
        isPlaying = true;
        this.playerScore = 0;
        this.handScore = 0;

        // Construct five dice objects
        this.dice = new Die[] {new Die(context), new Die(context), new Die(context), new Die(context), new Die(context)};

        // Roll all 5 dice to generate the first hand.
        rollDice();
    }

    /**
     * Rolls all of the non-held dice on the field. Since dice are held using a Die member field, this array blindly rolls
     * all five dice for simplicity-- the Dice themselves will only roll if they are not being held.
     */
    public void rollDice() {
        for (int i=0; i<=4; i++) {
            dice[i].roll();
        }
    }

    /**
     * Returns the Drawable for the die at index dieIndex
     * @param dieIndex Die index from 0 to 4
     * @return Face drawable
     */
    public Drawable getDieFace(int dieIndex) {
        // Get the Die object at the requested index and return its face.
        return dice[dieIndex].getFace();
    }

    /**
     * Sets the hold status of a die on the board.
     * @param dieIndex Index of die to roll from 0-4.
     * @param holdDie True to hold this die, false to release it.
     */
    public void holdDie(int dieIndex, boolean holdDie) {
        dice[dieIndex].setHeld(holdDie);
        if (holdDie) {
            Log.v(TAG, "Dice " + dieIndex + " is now held.");
        } else {
            Log.v(TAG, "Dice " + dieIndex + " is NOT being held.");
        }
    }
}
