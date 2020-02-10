package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;

import org.corpitech.vozera.R;

import pl.droidsonroids.gif.GifImageView;

public class OverlayView extends View {
    private Paint paint;
    private float xRatio, yRatio;
    GifImageView faceDetectionBound;
    private Bitmap tlPanel, trPanel;

    public void adjustRatio(Size previewSize) {
        this.xRatio = 1.0f * this.getWidth() / previewSize.getWidth();
        this.yRatio = 1.0f * this.getHeight() / previewSize.getHeight();
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    }

    public void setFaceDetectionBound(GifImageView gifImageView) {
        this.faceDetectionBound = gifImageView;
    }

    public void setTLPanel(Bitmap tlPanel) {
        this.tlPanel = tlPanel;
    }

    public void setTRPanel(Bitmap trPanel) {
        this.trPanel = trPanel;
    }

    public void startFaceBoundAnimation(Rect faceBox) {

        if (faceBox != null) {
            if (faceDetectionBound.getVisibility() == GONE) {
                this.post(() -> faceDetectionBound.setVisibility(VISIBLE));
                Runnable runnable = () -> this.faceDetectionBound.setVisibility(GONE);
                this.postDelayed(runnable, 2000);
            }
        }

    }


    public void updateFaceBox(Rect faceBox) {
        if (faceDetectionBound.getVisibility() == VISIBLE) {
            Rect scaledFaceBox = scaleBox(faceBox);
            int diameter = (int) Math.sqrt(Math.pow(scaledFaceBox.height(), 2) + Math.pow(scaledFaceBox.width(), 2));
            float circleLeft = scaledFaceBox.left - (diameter - scaledFaceBox.width()) / 2.0f;
            float circleTop = scaledFaceBox.top - (diameter - scaledFaceBox.height()) / 2.0f;

            this.post(() -> {
                faceDetectionBound.setMaxWidth(diameter);
                faceDetectionBound.setMinimumWidth(diameter);
                faceDetectionBound.setMaxHeight(diameter);
                faceDetectionBound.setMaxWidth(diameter);
                faceDetectionBound.setX(circleLeft);
                faceDetectionBound.setY(circleTop);
            });
        }

    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(tlPanel, 10, 5, paint);
        canvas.drawBitmap(trPanel, getWidth() - trPanel.getWidth() - 10, 5, paint);
    }

    private Rect scaleBox(Rect box) {
        int left = (int) (box.left * this.xRatio);
        int top = (int) (box.top * this.yRatio);
        int right = (int) (box.right * this.xRatio);
        int bottom = (int) (box.bottom * yRatio);

        return new Rect(left, top, right, bottom);
    }

}
