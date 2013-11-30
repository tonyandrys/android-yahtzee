package com.tonyandrys.yahtzee;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

public class GameActivity extends Activity {

    public final String TAG = GameActivity.class.getName();
    SoundManager soundManager;
    ScoreManager scoreManager;
    ArrayList<ImageView> diceViews;
    Board board;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // Initialize game board, SoundManager, and dice view holder
        board = new Board(this, new Random());
        soundManager = new SoundManager(this);
        diceViews = new ArrayList<ImageView>();

        // Initialize ScoreManager to set score to zero
        scoreManager = new ScoreManager();

        // Apply dieTouchListener to Dice ImageViews and add to master list
        int[] resIds = {R.id.die_1_imageview, R.id.die_2_imageview, R.id.die_3_imageview, R.id.die_4_imageview, R.id.die_5_imageview};
        for (int i=0; i<=4; i++) {
            ImageView die = (ImageView)findViewById(resIds[i]);
            die.setOnClickListener(new dieTouchListener());
            diceViews.add(die);
        }

        // Apply scoreTouchListener to Score TextViews
        int[] scoreResIDs = {R.id.ones_value_textview, R.id.twos_value_textview, R.id.threes_value_textview, R.id.fours_value_textview, R.id.fives_value_textview, R.id.sixes_value_textview, R.id.upper_bonus_value_textview, R.id.three_of_a_kind_value_textview, R.id.four_of_a_kind_value_textview, R.id.full_house_value_textview, R.id.sm_straight_value_textview, R.id.lg_straight_value_textview, R.id.yahtzee_value_textview, R.id.bonus_yahtzee_value_textview, R.id.chance_value_textview, R.id.grand_total_value_textview};
        for (int i=0; i<scoreResIDs.length; i++) {
            TextView tv = (TextView)findViewById(scoreResIDs[i]);
            tv.setOnClickListener(new scoreTouchListener());
        }

        // Initialize button listeners
        // Roll dice button
        Button rollDiceButton = (Button)findViewById(R.id.roll_dice_button);
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTurn();
            }
        });

        // Die click listener
        ImageView die = (ImageView)findViewById(R.id.die_1_imageview);
        die.setOnClickListener(new dieTouchListener());
    }

    /**
     * Starts the next turn by rolling all un-held dice and updating their face values.
     */
    public void nextTurn() {

        // Roll all unheld dice
        board.rollDice();
        soundManager.playRollEffect();

        // Get each die face and apply them to the views on screen
        updateDiceFaces();

        // Calculate score of this hand
        ScoreCard scoreCard = scoreManager.calculateHand(board.getDiceValues());

        // Apply calculated scores to the Scorepad UI
        updateScorepadDisplay(scoreCard);

    }

    public void endRound() {

    }

    /**
     * Updates the on-screen Scorepad to the values stored in an ArrayScoreCard
     * @param scoreCard ArrayScoreCard object used to update Scorepad
     */
    public void updateScorepadDisplay(ScoreCard scoreCard) {
        // Get TextViewResID -> Integer Score Map and extract keys
        HashMap<Integer, Integer> map = scoreCard.getScoreMap();
        Set<Integer> keys = map.keySet();

        // Get the textview resources that must be updated out of the keyset and apply its enclosed value.
        Iterator<Integer> iterator = keys.iterator();
        while (iterator.hasNext()) {
            // Get next resource ID in set
            int resId = iterator.next();

            // Get the associated value
            int value = map.get(resId);

            // Apply value to the resource ID
            TextView tv = (TextView)findViewById(resId);
            tv.setText(Integer.toString(value));
        }
    }

    /**
     * Gets the drawable associated with each dice and updates the on-screen Imageviews.
     */
    private void updateDiceFaces() {
        for (int i=0; i<=4; i++) {
            Drawable d = board.getDieFace(i);
            diceViews.get(i).setImageDrawable(d);
        }
    }

    /**
     * Toggles the lock display next to a die on the UI by its dieIndex.
     * All UX logic, such as displaying the lock and sound effects are taken care of here.
     * @param dieIndex Die to toggle from 0-4
     * @param isHeld true to show the lock, false to disable.
     */
    public void toggleDiceLock(int dieIndex, boolean isHeld) {

        // Play the toggle sound effect
        soundManager.playToggleEffect();

        // Get the reference to the lock associated with the passed dieIndex
        ImageView lock = (ImageView)findViewById(R.id.game_container_framelayout).findViewWithTag(Integer.toString(dieIndex) + "L");

        // Toggle the lock visual.
        if (isHeld) {
            lock.setVisibility(View.VISIBLE);
        } else {
            lock.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Listener applied to all dice and fired when it is touched by the player.
     * Toggles held/released state between rolls.
     */
    private class dieTouchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Extract the tag of this die to determine its index
            int tag = Integer.parseInt(v.getTag().toString());

            // Toggle the state of the die
            if (board.isDieHeld(tag)) {
                // Die is held, release it.
                board.holdDie(tag, false);
                toggleDiceLock(tag, false);
            } else {
                // Die is free, hold it.
                board.holdDie(tag, true);
                toggleDiceLock(tag, true);
            }
        }
    }

    /**
     * Touch listeners applied to all score fields. User interacts with these fields to finish a round and record a score.
     */
    private class scoreTouchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // Get the value contained in this TextView
            TextView tv = (TextView) v;
            int value = Integer.parseInt(tv.getText().toString());

            // If this field is available, write the score to the player's ScoreCard and end this round.
            if (value == ScoreCard.AVAILABLE_SCORE) {
                int tag = Integer.parseInt(v.getTag().toString()); // Get the tag of this TextView
                scoreManager.writeScore(tag, value); // Write the score and end this round.
                endRound();
            } else {
                // Do nothing! Can't write to the same score field twice.
                // FIXME: Add some indicator this field can't be written to here... a sound clip, small toast or something like that?
            }
        }
    }
}
