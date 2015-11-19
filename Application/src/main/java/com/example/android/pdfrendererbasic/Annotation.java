package com.example.android.pdfrendererbasic;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by Aaron Beckett on 11/16/2015.
 */
public class Annotation {

    /**
     * The data structure that stores the raw point data for each mark
     */
    private ArrayList<ArrayList<PointF>> curves = new ArrayList<>();

    /**
     * We must keep track of the current curve being recorded
     */
    private int currentCurve = -1;

    public void startNewCurve(float x, float y) {
        currentCurve++;
        curves.add(new ArrayList<PointF>());
        addPointToCurrentCurve(x, y);
    }

    public void addPointToCurrentCurve(float x, float y) {
        curves.get(currentCurve).add(new PointF(x, y));
    }

    public boolean isEmpty() {
        return curves.size() == 0;
    }
}
