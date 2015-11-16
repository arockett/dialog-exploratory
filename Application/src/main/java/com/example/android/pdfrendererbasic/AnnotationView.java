package com.example.android.pdfrendererbasic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Aaron Beckett on 11/15/2015.
 */
public class AnnotationView extends View {

    /**
     * Key string for saving the state of the enabled flag
     */
    private static final String STATE_ENABLED = "state_enabled";

    /**
     * Are we in the annotation mode? If so we should handle touch events
     */
    private boolean enabled = false;

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private Path mPath;
    private Paint mPaint;

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 2;

    public AnnotationView(Context context) {
        super(context);
        init(null, 0);
    }

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AnnotationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
    }

    public void saveState(Bundle bundle) {
        bundle.putBoolean(STATE_ENABLED, enabled);
    }

    public void loadState(Bundle savedInstanceState) {
        enabled = savedInstanceState.getBoolean(STATE_ENABLED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    public void disable() {
        enabled = false;
    }

    public void enable() {
        enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!enabled) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                return true;
        }
        return false;
    }

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }
}
