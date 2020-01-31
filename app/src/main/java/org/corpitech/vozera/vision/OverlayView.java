package org.corpitech.vozera.vision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;

import org.corpitech.vozera.R;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static android.graphics.Bitmap.createScaledBitmap;
import static org.corpitech.vozera.Utils.adjustRectByBitmapSize;

public class OverlayView extends View {
    private Paint paint;
    private Size bitmapSize;
    private Size overlaySize;
    private float xRatio, yRatio;
    private ChatterFaceInfo chatterFaceInfo;
    private int initFaceBoundSizePercent = 50;
    private int curFaceBoundSizePercent = initFaceBoundSizePercent;
    private Animation faceBoundAnimation;


    public OverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(4.0f);
        paint.setTextSize(16 * getResources().getDisplayMetrics().density);
        paint.setStyle(Paint.Style.STROKE);
        Bitmap faceBoundBitmap = BitmapFactory.decodeResource(getResources(), R.raw.face_bound_with_heart);
        faceBoundAnimation = new Animation(faceBoundBitmap);

    }

    public void startAnimation() {
        faceBoundAnimation.setState(Animation.AWAIT);
    }

    class Animation {
        private int left, top;
        static final int INIT = 0, AWAIT = 1, READY = 2;
        private Bitmap bitmap, scaledBitmap;
        private AtomicInteger state;


        private void startTimer() {
                final int interval = 600;
                Handler handler = new Handler();
                Runnable runnable = () -> faceBoundAnimation.setState(INIT);
                handler.postDelayed(runnable, interval);
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

        public Animation(Bitmap bitmap) {
            this.state = new AtomicInteger(INIT);
            this.bitmap = bitmap;
        }

        public Bitmap getScaledBitmap() {
            return scaledBitmap;
        }

        public void setScaledBitmap(Bitmap scaledBitmap) {
            this.scaledBitmap = scaledBitmap;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }


    }

    public void setChatterFaceInfo(ChatterFaceInfo chatterFaceInfo) {
        this.chatterFaceInfo = chatterFaceInfo;

        if (overlaySize == null || bitmapSize == null) {
            this.overlaySize = new Size(this.getWidth(), this.getHeight());
            this.bitmapSize = chatterFaceInfo.bitmapSize;

            this.xRatio = 1.0f * this.overlaySize.getWidth() / this.bitmapSize.getWidth();
            this.yRatio = 1.0f * this.overlaySize.getHeight() / this.bitmapSize.getHeight();
        }

        //if (chatterFaceAnimation) invalidate();
    }



    public void updateOverlay() {
        if ((faceBoundAnimation.getState() == Animation.AWAIT ||
            faceBoundAnimation.getState() == Animation.READY)
            && chatterFaceInfo != null) {

            List<FaceInfo> facesInfo = chatterFaceInfo.facesInfo;
            for (int i = 0; i < facesInfo.size(); i++) {
                Rect box = facesInfo.get(i).box;
                System.out.println(box);
//                String emotion = facesInfo.get(i).emotion;
//                Float beautyScore = facesInfo.get(i).beautyScore;
                Rect scaledBox = adjustRectByBitmapSize(scaleBox(box), this.overlaySize);
                int initialSideLen = getScaledSquareRectSideLen(scaledBox, curFaceBoundSizePercent);
                curFaceBoundSizePercent += 10;
                Bitmap newBitmap = createScaledBitmap(faceBoundAnimation.getBitmap(), initialSideLen, initialSideLen, false);
                faceBoundAnimation.setScaledBitmap(newBitmap);
                faceBoundAnimation.setLeft(scaledBox.centerX() - (initialSideLen / 2));
                faceBoundAnimation.setTop(scaledBox.centerY() - (initialSideLen / 2));
                faceBoundAnimation.setState(Animation.READY);

                break;
            }
        }

        this.invalidate();
    }

    private int getScaledSquareRectSideLen(Rect target, int percentage) {
        int sideLen = target.width() > target.height() ? target.width() : target.height();
        return (int)(sideLen * (percentage / 100.0f));
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (faceBoundAnimation.getState() == Animation.READY) {
            canvas.drawBitmap(faceBoundAnimation.getScaledBitmap(), faceBoundAnimation.left,
                    faceBoundAnimation.top, paint);
        }

    }

    private Rect scaleBox(Rect box) {
        int left = (int) (box.left * this.xRatio);
        int top = (int) (box.top * this.yRatio);
        int right = (int) (box.right * this.xRatio);
        int bottom = (int) (box.bottom * yRatio);

        return new Rect(left, top, right, bottom);
    }

}
