package com.example.android.pdfrendererbasic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class DocumentView extends View {

    /**
     * {@link AnnotationView} that is used to make new annotations
     */
    private AnnotationView annotationView;

    /**
     * The displayed document's name
     */
    private String docName;

    /**
     * The current page number in the document
     */
    private int pageNumber;

    /**
     * The bitmap used to display the current document page
     */
    private Bitmap pageBitmap;

    /**
     * Current annotation bitmaps
     */
    private ArrayList<Bitmap> annotations = new ArrayList<>();

    /**
     * Scale to draw the bitmap
     */
    private float scale = 1f;

    /**
     * X margin of the document
     */

    /**
     * Y margin of the document
     */

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

        if (annotationView != null) {
            annotationView.saveState(bundle);
        }
    }

    public void loadState(Bundle savedInstanceState) {

        if (annotationView != null) {
            annotationView.loadState(savedInstanceState);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(pageBitmap, 0, 0, null);
    }

    public void setDocumentName(String name) {
        this.docName = name;
    }

    public void setPage(Bitmap page, int num) {
        this.pageBitmap = page;
        this.pageNumber = num;
    }

    public void setAnnotationView(AnnotationView annotationView) {
        this.annotationView = annotationView;
    }
}
