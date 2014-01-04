package com.tonyandrys.yahtzee;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

public class GameActivity extends Activity {

    private final String TAG = GameActivity.class.getName();
    SoundManager soundManager;
    ScoreManager scoreManager;
    ArrayList<ImageView> diceViews;
    HashSet<Integer> availableScoreIDs;
    Board board;
    int rollCount;
    int roundCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // Initialize game board, SoundManager, and dice view holder
        board = new Board(this, new Random());
        soundManager = new SoundManager(this);
        diceViews = new ArrayList<ImageView>();

        // Initialize ScoreManager to set score to zero
        scoreManager = new ScoreManager(this);

        // Apply dieTouchListener to Dice ImageViews and add to master list
        int[] resIds = {R.id.die_1_imageview, R.id.die_2_imageview, R.id.die_3_imageview, R.id.die_4_imageview, R.id.die_5_imageview};
        for (int i=0; i<=4; i++) {
            ImageView die = (ImageView)findViewById(resIds[i]);
            die.setOnClickListener(new dieTouchListener());
            diceViews.add(die);
        }

        // Apply scoreTouchListener to Score TextViews and set every resource ID as available as no scores are recorded yet.
        int[] scoreResIDs = {R.id.ones_value_textview, R.id.twos_value_textview, R.id.threes_value_textview, R.id.fours_value_textview, R.id.fives_value_textview, R.id.sixes_value_textview, R.id.three_of_a_kind_value_textview, R.id.four_of_a_kind_value_textview, R.id.full_house_value_textview, R.id.sm_straight_value_textview, R.id.lg_straight_value_textview, R.id.yahtzee_value_textview, R.id.bonus_yahtzee_value_textview, R.id.chance_value_textview};
        for (int i=0; i<scoreResIDs.length; i++) {
            TextView tv = (TextView)findViewById(scoreResIDs[i]);
            tv.setOnClickListener(new scoreTouchListener());
            tv.setVisibility(View.INVISIBLE);
            //availableScoreIDs.add(scoreResIDs[i]);
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

        // Start the first round and set the turn count to 3.
        rollCount = 3;

        // A game of yahtzee consists of 13 rounds
        roundCount = 13;
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

        // Build score map of this hand and apply to UI
        scoreManager.calculateHand(board.getDiceValues());
        //HashMap<Integer, Integer> map = scoreManager.getScoreDisplayMap(ScoreManager.MAP_TYPE_HAND_SCORES);
        //updateScorepadDisplay(map, false);

        // Decrement roll counter and update roll counter on display
        rollCount--;
        updateRollCountDisplay();

        // If we're out of turns, disable the roll button to force the player to score.
        if (rollCount == 0) {
            Button rollButton = (Button)findViewById(R.id.roll_dice_button);
            rollButton.setEnabled(false);
        }
    }

    /**
     * Starts a new round by wiping the temp scores from the ScorePad, updating
     * the player's total score, and resetting the turn count back to 3.
     */
    public void newRound() {
        // When a new round starts, we remove the temporary scores from the display by getting player's scoreMap and updating the display again
        // updateScorepadDisplay(scoreManager.getScoreDisplayMap(ScoreManager.MAP_TYPE_PLAYER_SCORES), true);

        // Reset roll count and hold status on all dice
        rollCount = 3;
        for (int i=0; i<5; i++) {
            board.holdDie(i, false);
            toggleDiceLock(i, false);
        }

        // Update Total
        updatePlayerTotal();

        // Update turn count on UI
        updateRollCountDisplay();

        // Decrement a round
        roundCount--;

        // if this is the end of the game, it's game over!
        if (roundCount == 0) {
            FrameLayout gameOverPanel = (FrameLayout)findViewById(R.id.game_over_panel);
            gameOverPanel.setVisibility(View.VISIBLE);
        } else {
            Button rollButton = (Button)findViewById(R.id.roll_dice_button);
            rollButton.setEnabled(true);
        }
    }

    /**
     * Updates the on-screen Scorepad to the values stored in an ArrayScoreCard
     * @param finalized True if permanent scores are being displayed, false if hand scores are being displayed
     */
    public void updateScorepadDisplay(HashMap<Integer, Integer> scoreMap, boolean finalized) {
        // k := iterator over the keys of scoreMap (resIDs)
        Iterator k = scoreMap.keySet().iterator();
        Log.v(TAG, "Updating Score Display...");

        // For every key in k, apply the contained value in its respective value
        while (k.hasNext()) {

            // Get next resource ID in set
            int resId = (Integer)k.next(); //possibly unsafe cast?
            //Log.v(TAG, "Next ResID: " + resId);

            // Check if the field is available. If it can't be set by the user because it contains a stored value or was zeroed, there's no point in updating the value.
            if (availableScoreIDs.contains(resId) && scoreMap.containsKey(resId)) {
                //Log.v(TAG, resId + "is available AND in the scoreMap");

                // Get the associated value
                int value = scoreMap.get(resId);

                // Apply appropriate value
                TextView tv = (TextView)findViewById(resId);

                // Apply appropriate color to this item (black indicates player score, gray indicates a temporary value)
                tv.setTextColor(getResources().getColor(R.color.available_scorepad_field));
                tv.setVisibility(View.VISIBLE);
                tv.setText(Integer.toString(value));
                //Log.v(TAG, "Applied " + Integer.toString(value) + " to view " + resId);
            }
        }
    }

    /**
     * Gets the current total score of the player and updates the on-screen ScoreCard.
     */
    public void updatePlayerTotal() {

        // Get the player's total from ScoreManager
        String total = Integer.toString(scoreManager.getTotalScore());

        // Apply string converted total to TextView
        TextView totalTextView = (TextView)findViewById(R.id.grand_total_value_textview);
        totalTextView.setText(total);
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
     * Updates the displayed roll count based on the value of rollCount.
     */
    private void updateRollCountDisplay() {
        TextView rollCountTextView = (TextView)findViewById(R.id.roll_count_textview);
        rollCountTextView.setText(Integer.toString(rollCount));
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
            int tag = Integer.parseInt(tv.getTag().toString());
            int value = Integer.parseInt(tv.getText().toString());

            Log.v(TAG, "scoreTouchListener fired! textview ID: " + tv.getId() + " & value: " + value);

            // If this field is available, write the score to the player's ScoreCard and end this round.
            if (!scoreManager.isScoreFieldSet(tag)) {
                Log.v(TAG, "ScoreField " + tag + " is available! Locking this field...");

                // Write the score
                scoreManager.writeScore(tag, value);
                Log.v(TAG, "Wrote " + value + " to ScoreField ID " + tag);

                // A Round is finished when a score is recorded, so start the next round.
                newRound();
            } else {
                // Do nothing! Can't write to the same score field twice.
                Log.e(TAG, "Cannot write to field " + tag + "! Already in use.");
            }
        }
    }


}

