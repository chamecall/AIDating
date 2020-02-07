package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import org.corpitech.vozera.Animation;

import androidx.annotation.Nullable;

import org.corpitech.vozera.ChatterFaceInfo;
import org.corpitech.vozera.FaceInfo;
import org.corpitech.vozera.R;
import org.corpitech.vozera.gui.Panel;

import pl.droidsonroids.gif.GifImageView;

import static android.graphics.BitmapFactory.decodeResource;
import static org.corpitech.vozera.Utils.adjustRectByBitmapSize;

public class OverlayView extends View {
    private Paint paint;
    private Size bitmapSize;
    private Size overlaySize;
    private float xRatio, yRatio;
    private ChatterFaceInfo chatterFaceInfo;
    private final int circleRadius = 100;
    private Animation faceBoundAnimation;
    GifImageView faceDetectionBound;
    private Bitmap tlPanel, trPanel;

    public void setFaceDetectionBound(GifImageView gifImageView) {
        this.faceDetectionBound = gifImageView;
    }

    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        faceBoundAnimation = new Animation();
        Panel panel = new Panel(context, R.raw.top_panel);
        this.setTLPanel(panel.generatePanel(new float[]{0.92f, 0.24f, 0.82f, 0.42f}));
        this.setTRPanel(panel.generatePanel(new float[]{0.15f, 0.79f, 0.82f, 0.42f}));
    }


    public void setTLPanel(Bitmap tlPanel) {
        this.tlPanel = tlPanel;
    }

    public void setTRPanel(Bitmap trPanel) {
        this.trPanel = trPanel;
    }

    public void startFaceBoundAnimation() {
        //faceBoundAnimation.setState(Animation.AWAIT);

        if (chatterFaceInfo != null) {

            if (faceDetectionBound.getVisibility() == GONE) {
                this.post(() -> faceDetectionBound.setVisibility(VISIBLE));
                Runnable runnable = () -> this.faceDetectionBound.setVisibility(GONE);
                this.postDelayed(runnable, 2000);
            }
        }

    }


    public void setFaceInfo(ChatterFaceInfo chatterFaceInfo) {
        this.chatterFaceInfo = chatterFaceInfo;

        if (overlaySize == null || bitmapSize == null) {
            this.overlaySize = new Size(this.getWidth(), this.getHeight());
            this.bitmapSize = chatterFaceInfo.getBitmapSize();

            this.xRatio = 1.0f * this.overlaySize.getWidth() / this.bitmapSize.getWidth();
            this.yRatio = 1.0f * this.overlaySize.getHeight() / this.bitmapSize.getHeight();
        }

        updateOverlay();
    }


    public void calcFaceDetectionBoundPos() {
        Rect faceBox = scaleBox(chatterFaceInfo.getFaceInfo().getBox());
        int diameter = (int)Math.sqrt(Math.pow(faceBox.height(), 2) + Math.pow(faceBox.width(), 2));
        float circleLeft = faceBox.left - (diameter - faceBox.width()) / 2.0f;
        float circleTop = faceBox.top - (diameter - faceBox.height()) / 2.0f;

        this.post(() -> {
            faceDetectionBound.setMaxWidth(diameter);
            faceDetectionBound.setMinimumWidth(diameter);
            faceDetectionBound.setMaxHeight(diameter);
            faceDetectionBound.setMaxWidth(diameter);
            faceDetectionBound.setX(circleLeft);
            faceDetectionBound.setY(circleTop);

        });
    }


    public void updateOverlay() {
//        if ((faceBoundAnimation.getState() == Animation.AWAIT ||
//                faceBoundAnimation.getState() == Animation.READY)
//                && chatterFaceInfo != null) {
//
//
////                String emotion = facesInfo.get(i).emotion;
////                Float beautyScore = facesInfo.get(i).beautyScore;v
//            FaceInfo faceInfo = chatterFaceInfo.getFaceInfo();
//            Rect box = faceInfo.getBox();
//
//            Rect scaledBox = adjustRectByBitmapSize(scaleBox(box), this.overlaySize);
//            faceBoundAnimation.updateAnimation(scaledBox);
//
//
//        }
//

        calcFaceDetectionBoundPos();

        this.post(this::invalidate);

    }




    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (faceBoundAnimation.getState() == Animation.READY) {
//            canvas.drawBitmap(faceBoundAnimation.getScaledBitmap(), faceBoundAnimation.getLeft(),
//                    faceBoundAnimation.getTop(), paint);
//        }

        //canvas.drawCircle(100, 100, circleRadius, paint);
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
