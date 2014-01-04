package com.tonyandrys.yahtzee;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;

/**
 * com.tonyandrys.yahtzee -
 *
 * @author Tony Andrys
 *         Created: 01/04/2014
 *         (C) 2013 - Tony Andrys
 */

/**
 * Global UI Information is stored here
 */
public class UITable extends Application {

    private static final String TAG = UITable.class.getName();

    HashMap<Integer, Integer> scoreTable;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "UI Table has been built!");
    }

    public void setScoreTable(HashMap<Integer, Integer> scoreTable) {
        this.scoreTable = scoreTable;
    }

    public HashMap<Integer, Integer> getScoreTable() {
        return this.scoreTable;
    }
}
