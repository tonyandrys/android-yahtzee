package com.tonyandrys.yahtzee;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * com.tonyandrys.yahtzee - SoundManager
 *
 * @author Tony Andrys
 *         Created: 11/24/2013
 *         (C) 2013 - Tony Andrys
 */

/**
 * Handles playback of all audio and sound effects.
 */
public class SoundManager {

    private Context context;
    MediaPlayer rollMp;
    MediaPlayer toggleMp;

    public SoundManager(Context context) {
        this.context = context;
        rollMp = MediaPlayer.create(context, R.raw.diceroll);
        toggleMp = MediaPlayer.create(context, R.raw.click);
    }

    public void playRollEffect() {
        rollMp.start();
    }

    public void playToggleEffect() {
        toggleMp.start();
    }

}
