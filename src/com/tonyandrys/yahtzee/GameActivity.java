package com.tonyandrys.yahtzee;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class GameActivity extends Activity {

    ArrayList<ImageView> diceViews;
    Board board;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // Initialize game board and dice view holder
        board = new Board(this);
        diceViews.add((ImageView)findViewById(R.id.die_1_imageview));
        diceViews.add((ImageView)findViewById(R.id.die_2_imageview));
        diceViews.add((ImageView)findViewById(R.id.die_3_imageview));
        diceViews.add((ImageView)findViewById(R.id.die_4_imageview));
        diceViews.add((ImageView)findViewById(R.id.die_5_imageview));

        // Initialize button listeners
        // Roll dice button
        Button rollDiceButton = (Button)findViewById(R.id.roll_dice_button);
        rollDiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTurn();
            }
        });
    }

    /**
     * Starts the next turn by rolling all unheld dice and updating their face values.
     */
    public void nextTurn() {

        // Roll all unheld dice
        board.rollDice();

        // Get each die face and apply them to the views on screen
        updateDiceFaces();

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
}
