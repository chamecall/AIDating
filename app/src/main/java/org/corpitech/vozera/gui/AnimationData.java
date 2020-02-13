package org.corpitech.vozera.gui;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Size;

import static android.graphics.Bitmap.createScaledBitmap;

class AnimationData {
    private Point startPoint, axisDiff;
    private Size startSize, sizeDiff;
    private int stepsNum;
    private int curStepNum = 1;
    private Bitmap faceBitmap;

    public AnimationData(Bitmap faceBitmap, Point startPoint, Point endPoint, Size startSize, Size endSize, int stepsNum) {
        this.faceBitmap = faceBitmap;
        this.startPoint = startPoint;
        this.startSize = startSize;
        this.stepsNum = stepsNum;

        sizeDiff = new Size(endSize.getWidth() - startSize.getWidth(), endSize.getHeight() - startSize.getHeight());
        axisDiff = new Point(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
    }

    public boolean stepsLeft() {
        return curStepNum <= stepsNum;
    }

    private void incStep() {
        curStepNum++;
    }

    private Size getNewSize() {
        return new Size(startSize.getWidth() + sizeDiff.getWidth() * curStepNum / stepsNum,
                startSize.getHeight() + sizeDiff.getHeight() * curStepNum / stepsNum);
    }

    private Point getNewPos() {
        return new Point(startPoint.x + axisDiff.x * curStepNum / stepsNum,
                startPoint.y + axisDiff.y * curStepNum / stepsNum);
    }

    public LocatedBitmap getNewLocatedBitmap(){

        Size newSize = getNewSize();
        Point newPos = getNewPos();
        Bitmap scaledBitmap = createScaledBitmap(faceBitmap, newSize.getWidth(), newSize.getHeight(), false);
        incStep();
        return new LocatedBitmap(scaledBitmap, newPos);
    }



}
