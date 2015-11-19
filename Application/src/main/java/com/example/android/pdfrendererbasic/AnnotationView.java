package com.example.android.pdfrendererbasic;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class AnnotationView extends View {

    /**
     * Key that is used to save the state of our parameters in a bundle
     */
    private static final String STATE_PARAMETERS = "state_parameters";

    /**
     * {@link FreeDrawView} that is used to make new annotations
     */
    private FreeDrawView freeDrawView;

    /**
     * The annotations that have been made on the document
     */
    private ArrayList<Annotation> annotations = new ArrayList<>();

    /**
     * The parameter object that packages serializable member variables
     */
    private Parameters params = new Parameters();


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
    }

    public void saveState(Bundle bundle) {
        
        bundle.putSerializable(STATE_PARAMETERS, params);

        if (freeDrawView != null) {
            freeDrawView.saveState(bundle);
        }
    }

    public void loadState(Bundle savedInstanceState) {
        params = (Parameters)savedInstanceState.getSerializable(STATE_PARAMETERS);

        if (freeDrawView != null) {
            freeDrawView.loadState(savedInstanceState);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setDocumentName(String name) {
        params.docName = name;
    }

    public void setPage(int num) {
        params.pageNumber = num;
    }

    public void setFreeDrawView(FreeDrawView freeDrawView) {
        this.freeDrawView = freeDrawView;
    }

    public void startAnnotation() {
        freeDrawView.enable();
    }

    public void finishAnnotation() {
        Annotation newAnnot = freeDrawView.disable();
        if (!newAnnot.isEmpty()) {
            annotations.add(newAnnot);
        }
    }


    /************************ Nested Classes ************************/

    private static class Parameters implements Serializable {

        /**
         * The displayed document's name
         */
        public String docName = "";

        /**
         * The current page number in the document
         */
        public int pageNumber = 0;
    }
}
