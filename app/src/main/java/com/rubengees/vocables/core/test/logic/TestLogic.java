package com.rubengees.vocables.core.test.logic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.rubengees.vocables.core.Core;
import com.rubengees.vocables.core.test.TestAnswer;
import com.rubengees.vocables.core.test.TestResult;
import com.rubengees.vocables.core.testsettings.TestSettings;
import com.rubengees.vocables.pojo.Meaning;
import com.rubengees.vocables.pojo.Unit;
import com.rubengees.vocables.pojo.Vocable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by ruben on 28.04.15.
 */

public class TestLogic {

    private Context context;
    private List<Vocable> vocables;
    private int position = -1;
    private long currentTime;
    private TestSettings settings;
    private TestResult result;

    public TestLogic(final Context context, TestSettings settings) {
        this.context = context;
        this.settings = settings;
        this.vocables = new ArrayList<>();

        Map<Long, Unit> units = Core.getInstance((android.app.Activity) context).getVocableManager().getUnitMap();

        for (Long unitId : settings.getUnitIds()) {
            vocables.addAll(units.get(unitId).getVocables());
        }

        Collections.shuffle(vocables);
    }

    public TestLogic(Context context, Bundle savedInstanceState) {
        this.context = context;
        restoreSavedInstance(savedInstanceState);
    }

    private void restoreSavedInstance(final Bundle savedInstanceState) {
        vocables = savedInstanceState.getParcelableArrayList("vocables");
        settings = savedInstanceState.getParcelable("settings");
        position = savedInstanceState.getInt("position");
        currentTime = savedInstanceState.getLong("currentTime");
        result = savedInstanceState.getParcelable("result");
    }

    public boolean next() {
        position++;
        currentTime = System.currentTimeMillis();

        return true;
    }

    public Context getContext() {
        return context;
    }

    public final int getPosition() {
        return position;
    }

    public final Vocable getCurrentVocable() {
        return vocables.get(getAdjustedPosition());
    }

    public final int getAdjustedPosition() {
        int currentPos = position;
        int maxIndices = getAmount() - 1;

        if (currentPos > maxIndices) {
            currentPos = currentPos / maxIndices;
            return position - (currentPos * maxIndices);
        } else {
            return currentPos;
        }
    }

    public final int getAmount() {
        return vocables.size();
    }

    public final Vocable getVocableAtPos(int position) {
        return vocables.get(position);
    }

    public final List<Vocable> getSubList(int start, int end) {
        return vocables.subList(start, end);
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public TestSettings getSettings() {
        return settings;
    }

    final void processAnswer(Vocable vocable, Meaning question, Meaning answer, Meaning given, boolean correct) {
        int time = (int) (System.currentTimeMillis() - currentTime);
        TestAnswer testAnswer = new TestAnswer(question, answer, given, correct, time);

        result.addAnswer(testAnswer);
        vocable.processAnswer(correct);
        updateVocable(vocable);
    }

    final void updateVocable(Vocable vocable) {
        Core.getInstance((Activity) context).getVocableManager().updateVocableFast(vocable);
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("vocables", new ArrayList(vocables));
        outState.putParcelable("settings", settings);
        outState.putInt("position", position);
        outState.putLong("currentTime", currentTime);
        outState.putParcelable("result", result);
    }

    public final TestResult getResult() {
        return result;
    }
}