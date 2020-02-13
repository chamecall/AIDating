package org.corpitech.vozera.gui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;

import pl.droidsonroids.gif.GifImageView;


import static org.corpitech.vozera.gui.PanelsView.LR_PANEL_MARGIN;
import static org.corpitech.vozera.gui.PanelsView.TOP_PANEL_MARGIN;
import static org.corpitech.vozera.gui.PanelsView.VERTICAL_MARGIN_BTW_PANELS;

public class OverlayView extends View {
    private Paint paint;
    private float xRatio, yRatio;
    GifImageView faceDetectionGif;
    private Bitmap tlPanel, trPanel, chatterScore, userScore;
    private GifImageView lBrainGif, rBrainGif;
    private TopPanel topPanel;
    private BottomPanel bottomPanel;



    public void setlBrainGif(GifImageView lBrainGif) {
        this.lBrainGif = lBrainGif;
    }

    public void setrBrainGif(GifImageView rBrainGif) {
        this.rBrainGif = rBrainGif;
    }


    public void adjustRatio(Size previewSize) {
        this.xRatio = 1.0f * this.getWidth() / previewSize.getWidth();
        this.yRatio = 1.0f * this.getHeight() / previewSize.getHeight();
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    }

    public void setFaceDetectionGif(GifImageView gifImageView) {
        this.faceDetectionGif = gifImageView;
    }


    public void updateTLPanel(float [] values) {
        this.tlPanel = topPanel.generatePanel(values);
    }

    public void updateTRPanel(float [] values) {
        this.trPanel = topPanel.generatePanel(values);
    }

    public void updateChatterScore(int totalScore) {
        this.chatterScore = bottomPanel.generateScoreBitmap(totalScore);
    }

    public void updateUserScore(int totalScore) {
        this.userScore = bottomPanel.generateScoreBitmap(totalScore);
    }

    public void setBrainGifPositions(Rect brainGifRect) {
        if (lBrainGif != null &&  rBrainGif != null) {

            lBrainGif.setVisibility(INVISIBLE);
            rBrainGif.setVisibility(INVISIBLE);
            setGifSize(lBrainGif, brainGifRect.width(), brainGifRect.height());
            setGifSize(rBrainGif, brainGifRect.width(), brainGifRect.height());

            this.post(() -> {

                setBackgroundTintMode(PorterDuff.Mode.OVERLAY);

                lBrainGif.setX(LR_PANEL_MARGIN + bottomPanel.getWidth() - brainGifRect.width());
                lBrainGif.setY(TOP_PANEL_MARGIN + tlPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS +
                        ((bottomPanel.getHeight() - lBrainGif.getHeight()) >> 1));
                rBrainGif.setY(TOP_PANEL_MARGIN + trPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS +
                        ((bottomPanel.getHeight() - rBrainGif.getHeight()) >> 1));
                // we setX position in post cause before that we don't know width of the View
                // and we calculate that after xml inflating
                rBrainGif.setX(getWidth() - LR_PANEL_MARGIN - brainGifRect.width());
                lBrainGif.setVisibility(VISIBLE);
                rBrainGif.setVisibility(VISIBLE);
            });

        }
    }





    public void startFaceDetectionAnimation(Rect faceBox) {

        if (faceBox != null) {
            if (faceDetectionGif.getVisibility() == GONE) {
                updateFaceDetectionGif(faceBox);
                this.post(() -> faceDetectionGif.setVisibility(VISIBLE));
                Runnable runnable = () -> this.faceDetectionGif.setVisibility(GONE);
                this.postDelayed(runnable, 2000);
            }
        }

    }

    public void stopFaceDetectionAnimation() {
        this.post(() -> faceDetectionGif.setVisibility(GONE));
    }

    private void updateFaceDetectionGif(Rect faceBox) {
        Rect scaledFaceBox = scaleBox(faceBox);
        int diameter = (int) Math.sqrt(Math.pow(scaledFaceBox.height(), 2) + Math.pow(scaledFaceBox.width(), 2));
        float circleLeft = scaledFaceBox.left - (diameter - scaledFaceBox.width()) / 2.0f;
        float circleTop = scaledFaceBox.top - (diameter - scaledFaceBox.height()) / 2.0f;
        setGifSize(faceDetectionGif, diameter, diameter);
        this.post(() -> {
            faceDetectionGif.setX(circleLeft);
            faceDetectionGif.setY(circleTop);
        });

    }


    public void updateFaceBox(Rect faceBox) {
        if (faceDetectionGif.getVisibility() == VISIBLE) {
            updateFaceDetectionGif(faceBox);
        }


    }

    private void setGifSize(GifImageView gif, int width, int height) {
        gif.setMaxWidth(width);
        gif.setMaxHeight(height);
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(tlPanel, LR_PANEL_MARGIN, TOP_PANEL_MARGIN, paint);
        canvas.drawBitmap(trPanel, getWidth() - trPanel.getWidth() - LR_PANEL_MARGIN, TOP_PANEL_MARGIN, paint);

        canvas.drawBitmap(chatterScore, LR_PANEL_MARGIN + bottomPanel.getGifCell().left, TOP_PANEL_MARGIN + tlPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS, paint);
        canvas.drawBitmap(userScore, getWidth() - LR_PANEL_MARGIN - bottomPanel.getWidth() + bottomPanel.getGifCell().left,
                TOP_PANEL_MARGIN + trPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS, paint);


    }

    private Rect scaleBox(Rect box) {
        int left = (int) (box.left * this.xRatio);
        int top = (int) (box.top * this.yRatio);
        int right = (int) (box.right * this.xRatio);
        int bottom = (int) (box.bottom * yRatio);

        return new Rect(left, top, right, bottom);
    }



    public void setTopPanel(TopPanel topPanel) {
        this.topPanel = topPanel;
    }

    public void setBottomPanel(BottomPanel bottomPanel) {
        this.bottomPanel = bottomPanel;
    }





}
