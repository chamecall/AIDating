package org.corpitech.vozera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;


import java.util.concurrent.atomic.AtomicInteger;

import static android.graphics.Bitmap.createScaledBitmap;

public class Animation {
    private int left, top;
    public static final int INIT = 0, AWAIT = 1, READY = 2;
    private Bitmap regularCircle, activeCircle, heartBmp;
    private AtomicInteger state;
    private int initFaceBoundSizePercent = 50;
    private int curFaceBoundSizePercent = initFaceBoundSizePercent;
    private final int incStepPercent = 10;
    private final int updateIntervalMSEC = 6000;


    public Animation() {
        this.state = new AtomicInteger(INIT);
    }

    public void updateAnimation(Rect box) {
        int initialSideLen = this.getScaledSquareRectSideLen(box);
        curFaceBoundSizePercent += incStepPercent;
       // Bitmap newBitmap = createScaledBitmap(this.getBitmap(), initialSideLen, initialSideLen, false);
       // this.setScaledBitmap(newBitmap);
        this.setLeft(box.centerX() - (initialSideLen / 2));
        this.setTop(box.centerY() - (initialSideLen / 2));
        this.setState(Animation.READY);
    }



    private void startTimer() {
        Handler handler = new Handler();
        Runnable runnable = () -> this.setState(INIT);
        handler.postDelayed(runnable, updateIntervalMSEC);
    }

    public int getScaledSquareRectSideLen(Rect target) {
        int sideLen = target.width() > target.height() ? target.width() : target.height();
        return (int)(sideLen * (this.curFaceBoundSizePercent / 100.0f));
    }

    public void setState(int newState) {

        if (newState == INIT) {
            curFaceBoundSizePercent = initFaceBoundSizePercent;
        }
        if (this.getState() == AWAIT && newState == READY) startTimer();
        this.state.set(newState);
    }


    public int getState() {
        return state.get();
    }


    public Bitmap getScaledBitmap() {
        return null;
    }

    public void setScaledBitmap(Bitmap scaledBitmap) {

    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }



    public void setLeft(int left) {
        this.left = left;
    }

    public void setTop(int top) {
        this.top = top;
    }




}
