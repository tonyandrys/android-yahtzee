package com.tonyandrys.yahtzee;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
    HashSet<Integer> availableScoreIDs;
    Board board;
    int turnCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // Initialize game board, SoundManager, and dice view holder
        board = new Board(this, new Random());
        soundManager = new SoundManager(this);
        diceViews = new ArrayList<ImageView>();

        // Initialize ScoreManager to set score to zero and get list of available score fields
        scoreManager = new ScoreManager();
        availableScoreIDs = scoreManager.getAvailableScoreFields();

        // Apply dieTouchListener to Dice ImageViews and add to master list
        int[] resIds = {R.id.die_1_imageview, R.id.die_2_imageview, R.id.die_3_imageview, R.id.die_4_imageview, R.id.die_5_imageview};
        for (int i=0; i<=4; i++) {
            ImageView die = (ImageView)findViewById(resIds[i]);
            die.setOnClickListener(new dieTouchListener());
            diceViews.add(die);
        }

        // Apply scoreTouchListener to Score TextViews and set every resource ID as available as no scores are recorded yet.
        int[] scoreResIDs = {R.id.ones_value_textview, R.id.twos_value_textview, R.id.threes_value_textview, R.id.fours_value_textview, R.id.fives_value_textview, R.id.sixes_value_textview, R.id.upper_bonus_value_textview, R.id.three_of_a_kind_value_textview, R.id.four_of_a_kind_value_textview, R.id.full_house_value_textview, R.id.sm_straight_value_textview, R.id.lg_straight_value_textview, R.id.yahtzee_value_textview, R.id.bonus_yahtzee_value_textview, R.id.chance_value_textview, R.id.grand_total_value_textview};
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
        turnCount = 3;
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
        HashMap<Integer, Integer> map = scoreManager.getScoreDisplayMap(ScoreManager.MAP_TYPE_HAND_SCORES);
        updateScorepadDisplay(map);

        // Decrement turn counter
        turnCount--;

        // If we're out of turns, disable the roll button to force the player to score.
        if (turnCount == 0) {
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
        updateScorepadDisplay(scoreManager.getScoreDisplayMap(ScoreManager.MAP_TYPE_PLAYER_SCORES));

        // Reset turn count and hold status on all dice
        turnCount = 3;
        for (int i=0; i<5; i++) {
            board.holdDie(i, false);
            toggleDiceLock(i, false);
        }

        // Update Total
        updatePlayerTotal();

        Button rollButton = (Button)findViewById(R.id.roll_dice_button);
        rollButton.setEnabled(true);
    }

    /**
     * Updates the on-screen Scorepad to the values stored in an ArrayScoreCard
     */
    public void updateScorepadDisplay(HashMap<Integer, Integer> scoreMap) {
        // k := iterator over the keys of scoreMap (resIDs)
        Iterator k = scoreMap.keySet().iterator();

        // For every key in k, apply the contained value in its respective value
        while (k.hasNext()) {

            // Get next resource ID in set
            int resId = (Integer)k.next(); //possibly unsafe cast?

            // Check if the field is available. If it can't be set by the user because it contains a stored value or was zeroed, there's no point in updating the value.
            if (availableScoreIDs.contains(resId) && scoreMap.containsKey(resId)) {

                // Get the associated value
                int value = scoreMap.get(resId);

                // Apply appropriate value & color to the TextView
                TextView tv = (TextView)findViewById(resId);
                tv.setTextColor(R.color.available_scorepad_field);
                tv.setVisibility(View.VISIBLE);
                tv.setText(Integer.toString(value));
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

            Log.v(TAG, "scoreTouchListener fired! textview ID: " + tv.getId() + " & value: " + value);

            // If this field is available, write the score to the player's ScoreCard and end this round.
            if (availableScoreIDs.contains(tv.getId())) {
                int tag = Integer.parseInt(v.getTag().toString()); // Get the tag of this TextView
                scoreManager.writeScore(tag, value); // Write the score
                Log.v(TAG, "Wrote " + value + " to ScoreField ID " + tag);

                // Disable this score field from being used again during this game.
                tv.setTextColor(R.color.used_scorepad_field); // Set color
                availableScoreIDs.remove(tv.getId());

                // A Round is finished when a score is recorded, so start the next round.
                newRound();
            } else {
                // Do nothing! Can't write to the same score field twice.
                Log.e(TAG, "Cannot write to field! Already in use.");
            }
        }
    }
}

