package com.example.pdfviewerusingjs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

public class ZoomRelative extends RelativeLayout implements GestureDetector.OnGestureListener {

    //ScalingFactor i.e. Amount of Zoom
    static float mScaleFactor = 1.0f;

    // Maximum and Minimum Zoom
    private static float MIN_ZOOM = 1.0f;
    private static float MAX_ZOOM = 3.0f;

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private Mode mode1 = Mode.NONE;

    //Different Operation to be used
    private final int NONE_OPERATION=0;
    private final int ZOOM_OPERATION=2;
    private float mWidth= 1280;
    private float mHeight=800;

    // Mode to select the operation
    private int mode;

    //Track X and Y coordinate of the finger when it first touches the screen
    private float mInitialX = 0f;
    private float mInitialY = 0f;

    private float dx = 0f;
    private float dy = 0f;

    private float prevX = 0f;
    private float preyY = 0f;

    // Track the Bound of the Image after zoom to calculate the offset
    static Rect mClipBound;

    // mDetector to detect the scaleGesture for the pinch Zoom
    private ScaleGestureDetector mDetector;

    //Pivot point for Scaling
    static float gx=0,gy=0;


    /*
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    *
    *  @description : Constructor is called when used via XML
    *
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    */

    public ZoomRelative(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        mClipBound = new Rect();
        // Intialize ScaleGestureDetector
        mDetector = new ScaleGestureDetector(getContext(), new ZoomListener());
    }



    /*
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    *
    *  @description : Constructor is called when used via code
    *
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    */
    public ZoomRelative(Context context) {
        super(context);
        setWillNotDraw(false);
        mClipBound = new Rect();
        // Intialize ScaleGestureDetector
        mDetector = new ScaleGestureDetector(getContext(), new ZoomListener());
    }

    /*
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    *
    *  @description : OnTouchEvent of the layout which handles all type of motion-events possible
    *  @ Returns : true  - we are handling the touchEvent.
    *
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Handles all type of motion-events possible
        switch(event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:

                if (mScaleFactor > MIN_ZOOM) {
                    mode1 = Mode.NONE;
                    mInitialX = event.getX() - prevX;
                    mInitialY = event.getY() - preyY;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode1 == Mode.DRAG) {
                    mInitialX = event.getX() - prevX;
                    mInitialY = event.getY() - preyY;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                //Event occurs when the second finger is pressed down

                Log.d("Print", "Event: Action_Pointer_Down " );
                // If second finger is pressed on the screen with the first set the Mode to Zoom operation

                mode1 = Mode.ZOOM;
                break;

            case MotionEvent.ACTION_UP:
                //Event occurs when all the finger are taken of the screen
                Log.d("Print", "Event: Action_UP " );
                //If all the fingers are taken up there will be no operation
                mode1 = Mode.NONE;

                if (mScaleFactor > 1)
                {
                    gx = gx - (event.getX() - mInitialX);
                    gy = gy - (event.getY() - mInitialY);
                    invalidate();
                    requestLayout();
                }


                break;
        }

        // Give the event to the mDetector to get the scaling Factor
        mDetector.onTouchEvent(event);

        if ((mode1 == Mode.DRAG && mScaleFactor >= MIN_ZOOM) || mode1 == Mode.ZOOM) {
            getParent().requestDisallowInterceptTouchEvent(true);
            float maxDx = (child().getWidth() - (child().getWidth() / mScaleFactor)) / 2 * mScaleFactor;
            float maxDy = (child().getHeight() - (child().getHeight() / mScaleFactor))/ 2 * mScaleFactor;
            prevX = Math.min(Math.max(prevX, -maxDx), maxDx);
            preyY = Math.min(Math.max(preyY, -maxDy), maxDy);

            applyScaleAndTranslation();
        }

        return true;
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(mScaleFactor);
        child().setScaleY(mScaleFactor);
        child().setTranslationX(prevX);
        child().setTranslationY(preyY);
    }

    private View child() {
        return getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);

    }

    @Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        return super.invalidateChildInParent(location, dirty);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        int count = getChildCount();
        for(int i=0;i<count;i++){
            View child = getChildAt(i);
            if(child.getVisibility()!=GONE){
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)child.getLayoutParams();
                child.layout(
                        (int)(params.leftMargin ),
                        (int)(params.topMargin ),
                        (int)((params.leftMargin + child.getMeasuredWidth()) ),
                        (int)((params.topMargin + child.getMeasuredHeight()))
                );
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        //Save the canvas to set the scaling factor returned from detector
//        canvas.save(Canvas.MATRIX_SAVE_FLAG);

        canvas.scale(mScaleFactor, mScaleFactor,gx,gy);

        super.dispatchDraw(canvas);

        mClipBound = canvas.getClipBounds();

        canvas.restore();
    }

    private class ZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // getting the scaleFactor from the detector
            mScaleFactor *= detector.getScaleFactor(); // Gives the scaling factor from the previous scaling to the current
            //    Log.d("Print", "detector scaling Factor" + mScaleFactor);

            gx = detector.getFocusX();
            gy = detector.getFocusY();
            // Limit the scale factor in the MIN and MAX bound
            mScaleFactor= Math.max(Math.min(mScaleFactor, MAX_ZOOM),MIN_ZOOM);
            //    Log.d("Print", "Bounded scaling Factor" + mScaleFactor);

            /*//Force canvas to redraw itself only if the one event is to happen (say Zooming only ) else do not invalidate here for multi operations
               As what we de for scrolling or panning will not reflect here. So we will add this in onDraw method
            invalidate();*/
            // Here we are only zooming so invalidate has to be done
            invalidate();
            requestLayout();

            // we have handle the onScale
            return true;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {
        return false;
    }


    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
