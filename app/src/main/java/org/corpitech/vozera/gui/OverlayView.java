package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


import static org.corpitech.vozera.Utils.debug;
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
    private ImageView lBrainBack, rBrainBack;
    private float scoreBitmapY;


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
            lBrainBack.setVisibility(INVISIBLE);
            setViewSize(lBrainGif, brainGifRect.width(), brainGifRect.height());
            setViewSize(rBrainGif, brainGifRect.width(), brainGifRect.height());
            setViewSize(lBrainBack, brainGifRect.width(), brainGifRect.height());
            setViewSize(rBrainBack, brainGifRect.width(), brainGifRect.height());


            this.post(() -> {
                // we set positions in post as before that we don't know width of the View
                // and we calculate that after xml inflating
                int leftMargin =  (brainGifRect.width() - lBrainGif.getWidth()) / 2;
                float lx = LR_PANEL_MARGIN + bottomPanel.getWidth() - brainGifRect.width() + leftMargin;
                float y = TOP_PANEL_MARGIN + tlPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS +
                        (bottomPanel.getHeight() - lBrainGif.getHeight()) / 2.0f;
                float rx = getWidth() - LR_PANEL_MARGIN - brainGifRect.width() + leftMargin;

                scoreBitmapY = (float)(TOP_PANEL_MARGIN + tlPanel.getHeight() + VERTICAL_MARGIN_BTW_PANELS - lBrainGif.getHeight() * 0.1);

                lBrainGif.setX(lx);
                lBrainGif.setY(y);

                rBrainGif.setX(rx);
                rBrainGif.setY(y);

                lBrainBack.setX(lx);
                lBrainBack.setY(y);

                rBrainBack.setX(rx);
                rBrainBack.setY(y);

                lBrainGif.setVisibility(VISIBLE);
                rBrainGif.setVisibility(VISIBLE);
                lBrainBack.setVisibility(VISIBLE);
                rBrainBack.setVisibility(VISIBLE);
            });

        }
    }





    public void startFaceDetectionAnimation(Rect faceBox) {

        if (faceBox != null) {
            if (faceDetectionGif.getVisibility() == GONE) {
                updateFaceDetectionGif(faceBox);
                GifDrawable gifDrawable = (GifDrawable) faceDetectionGif.getDrawable();
                gifDrawable.reset();
                this.post(() -> faceDetectionGif.setVisibility(VISIBLE));
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
        setViewSize(faceDetectionGif, diameter, diameter);
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

    private void setViewSize(ImageView gif, int width, int height) {
        gif.setMaxWidth(width);
        gif.setMaxHeight(height);
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(tlPanel, LR_PANEL_MARGIN, TOP_PANEL_MARGIN, paint);
        canvas.drawBitmap(trPanel, getWidth() - trPanel.getWidth() - LR_PANEL_MARGIN, TOP_PANEL_MARGIN, paint);


        canvas.drawBitmap(chatterScore, LR_PANEL_MARGIN + bottomPanel.getGifCell().left, scoreBitmapY, paint);
        canvas.drawBitmap(userScore, getWidth() - LR_PANEL_MARGIN - bottomPanel.getWidth() + bottomPanel.getGifCell().left,
                scoreBitmapY, paint);


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


    public void setlBrainBack(ImageView brainBack) {
        lBrainBack = brainBack;
    }

    public void setrBrainBack(ImageView brainBack) {
        rBrainBack = brainBack;
    }
}
