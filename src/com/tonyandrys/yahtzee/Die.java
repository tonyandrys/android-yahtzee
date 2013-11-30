package com.tonyandrys.yahtzee;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * com.tonyandrys.yahtzee - Die
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */
public class Die {

    Context context;
    int value;
    Drawable face;
    boolean held;

    public Die(Context context) {
        this.context = context;
        this.face = null;
        this.held = false;
        this.value = 0;
    }

    /**
     * "Rolls" this die by generating a new random value [1,6] for this dice and updating the reference to its face.
     * @param r Random number generator used to generate this integer.
     */
    public void roll(Random r) {

        // Ensure this die is NOT being held before rolling.
        if (!this.held) {

            // Generate a random number from [1-7).
            this.value = (r.nextInt(6)) + 1;

            // Get the new face for this die and store it.
            this.face = getFace();
        }
    }

    /**
     * Returns the current value of this die as an integer
     * @return integer value of face
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Retrieves the visual representation (face) for this dice based on its current value.
     * @return Drawable of die face
     */
    public Drawable getFace() {
        Drawable face = null;
        Resources res = context.getResources();
        switch(value) {
            case 1:
                face = res.getDrawable(R.drawable.diceface_1);
                break;
            case 2:
                face = res.getDrawable(R.drawable.diceface_2);
                break;
            case 3:
                face = res.getDrawable(R.drawable.diceface_3);
                break;
            case 4:
                face = res.getDrawable(R.drawable.diceface_4);
                break;
            case 5:
                face = res.getDrawable(R.drawable.diceface_5);
                break;
            case 6:
                face = res.getDrawable(R.drawable.diceface_6);
                break;
        }
        return face;
    }

    /**
     * Sets the held status for this die.
     * @param holdDie True to hold the die, set false to remove a hold.
     */
    public void setHeld(boolean holdDie) {
        this.held = holdDie;
    }

    /**
     * Returns the held status of this die.
     * @return true if die is held, false if it is released
     */
    public boolean isDieHeld() {
        return this.held;
    }

}
