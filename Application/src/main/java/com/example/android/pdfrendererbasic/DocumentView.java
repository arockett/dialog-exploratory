package com.example.android.pdfrendererbasic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class DocumentView extends View {

    /**
     * Key that is used to save the state of our parameters in a bundle
     */
    private static final String STATE_PARAMETERS = "state_parameters";

    /**
     * The bitmap used to display the current document page
     */
    public Bitmap pageBitmap = null;

    /**
     * {@link AnnotationView} that is used to make new annotations
     */
    private AnnotationView annotationView;

    /**
     * Current annotation bitmaps
     */
    public ArrayList<Bitmap> annotations = new ArrayList<>();

    /**
     * The parameter object that packages serializable member variables
     */
    private Parameters params = new Parameters();

    /**
     * Touch objects for tracking scrolling and scaling
     */
    private Touch touch1 = new Touch();
    private Touch touch2 = new Touch();


    public DocumentView(Context context) {
        super(context);
        init(null, 0);
    }

    public DocumentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DocumentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    public void saveState(Bundle bundle) {
        params.initialized = false;
        
        bundle.putSerializable(STATE_PARAMETERS, params);

        if (annotationView != null) {
            annotationView.saveState(bundle);
        }
    }

    public void loadState(Bundle savedInstanceState) {
        params = (Parameters)savedInstanceState.getSerializable(STATE_PARAMETERS);

        if (annotationView != null) {
            annotationView.loadState(savedInstanceState);
        }
    }

    /**
     * Implement this method to handle touch screen motion events.
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                if(touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    touch2.copyToLast();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1.id = -1;
                touch2.id = -1;
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                if(id == touch2.id) {
                    touch2.id = -1;
                } else if(id == touch1.id) {
                    // Swap the touch objects
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                move();
                return false;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Get the positions for the two touches and put them
     * into the appropriate touch objects.
     * @param event the motion event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = event.getX(i);
                touch1.y = event.getY(i);
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = event.getX(i);
                touch2.y = event.getY(i);
            }
        }

        invalidate();
    }

    /**
     * Handle movement of the touches
     */
    private void move() {
        // If no touch1, we have nothing to do
        // This should not happen, but it never hurts
        // to check.
        if(touch1.id < 0) {
            return;
        }

        if(touch1.id >= 0) {
            // At least one touch
            // We are moving
            touch1.computeDeltas();

            params.marginX += touch1.dX;
            params.marginY += touch1.dY;
        }

        if(touch2.id >= 0) {
            // Two touches
            /*
             * Scaling
             */
            float distance1 = distance(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
            float distance2 = distance(touch1.x, touch1.y, touch2.x, touch2.y);
            float ra = distance2 / distance1;
            scale(ra);
        }
    }

    /**
     * Scale the image around the point x1, y1
     * @param ratio Ratio to scale the image by
     */
    private void scale(float ratio) {
        // Store starting dimensions
        float width1 = pageBitmap.getWidth() * params.scale;
        float height1 = pageBitmap.getHeight() * params.scale;

        // Change hat scale
        params.scale *= ratio;

        // Store final dimensions
        float width2 = pageBitmap.getWidth() * params.scale;
        float height2 = pageBitmap.getHeight() * params.scale;

        // Adjust hat location to make hat stay more within your fingers
        // while you scale it (not necessary but looks better to me)
        params.marginX -= (width2 - width1) / 2;
        params.marginY -= (height2 - height1) / 2;
    }

    /**
     * Determine the distance between two points
     * @param x1 Touch 1 x
     * @param y1 Touch 1 y
     * @param x2 Touch 2 x
     * @param y2 Touch 2 y
     * @return computed distance in pixels
     */
    private float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.hypot(x2 - x1, y2 - y1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!params.initialized) {
            float scaleV = canvas.getHeight() / pageBitmap.getHeight();
            float scaleH = canvas.getWidth() / pageBitmap.getWidth();
            params.scale = scaleV < scaleH ? scaleV : scaleH;
            params.marginX = (canvas.getWidth() - (pageBitmap.getWidth() * params.scale)) / 2;
            params.marginY = (canvas.getHeight() - (pageBitmap.getHeight() * params.scale)) / 2;

            params.initialized = true;
        }

        canvas.save();
        canvas.translate(params.marginX, params.marginY);
        canvas.scale(params.scale, params.scale);
        canvas.drawBitmap(pageBitmap, 0, 0, null);
        canvas.restore();
    }

    public void setDocumentName(String name) {
        params.docName = name;
    }

    public void setPage(Bitmap page, int num) {
        pageBitmap = page;
        params.pageNumber = num;

        invalidate();
    }

    public void setAnnotationView(AnnotationView annotationView) {
        this.annotationView = annotationView;
    }


    /************************ Nested Classes ************************/

    private static class Parameters implements Serializable {

        /**
         * Track whether we've initialized the margins and scale
         */
        public boolean initialized = false;

        /**
         * The displayed document's name
         */
        public String docName = "";

        /**
         * The current page number in the document
         */
        public int pageNumber = 0;

        /**
         * Scale to draw the bitmap
         */
        public float scale = 1f;

        /**
         * X margin of the document
         */
        public float marginX = 0f;

        /**
         * Y margin of the document
         */
        public float marginY = 0f;
    }

    /**
     * Local class to handle the touch status for one touch.
     * We will have one object of this type for each of the
     * two possible touches.
     */
    private class Touch {
        /**
         * Touch id
         */
        public int id = -1;

        /**
         * Current x location
         */
        public float x = 0;

        /**
         * Current y location
         */
        public float y = 0;

        /**
         * Previous x location
         */
        public float lastX = 0;

        /**
         * Previous y location
         */
        public float lastY = 0;

        /**
         * Change in x value from previous
         */
        public float dX = 0;

        /**
         * Change in y value from previous
         */
        public float dY = 0;

        /**
         * Copy the current values to the previous values
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }

        /**
         * Compute the values of dX and dY
         */
        public void computeDeltas() {
            dX = x - lastX;
            dY = y - lastY;
        }
    }
}
