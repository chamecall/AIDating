package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Size;
import android.view.View;

import androidx.annotation.Nullable;


public class PanelsView extends View {
    private Paint paint;
    private TopPanel topPanel;
    public final static int TOP_PANEL_MARGIN = 5, LR_PANEL_MARGIN = 10, VERTICAL_MARGIN_BTW_PANELS = 5;
    private BottomPanel bottomPanel;
    private float xRatio, yRatio;
    private boolean moveFlag = false;
    private AnimationData animationData;
    private Bitmap chatterBottomPanelBitmap, userBottomPanelBitmap;


    public PanelsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTopPanel(TopPanel topPanel) {
        this.topPanel = topPanel;
    }

    public void setBottomPanel(BottomPanel bottomPanel) {
        this.bottomPanel = bottomPanel;
        this.chatterBottomPanelBitmap = this.userBottomPanelBitmap = bottomPanel.getPanelBitmap().copy(
                bottomPanel.getPanelBitmap().getConfig(), false
        );
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(chatterBottomPanelBitmap, LR_PANEL_MARGIN, TOP_PANEL_MARGIN + topPanel.getPanelBitmap().getHeight() + VERTICAL_MARGIN_BTW_PANELS, paint);
        canvas.drawBitmap(userBottomPanelBitmap, getWidth() - chatterBottomPanelBitmap.getWidth() - LR_PANEL_MARGIN,
                TOP_PANEL_MARGIN + topPanel.getPanelBitmap().getHeight() + VERTICAL_MARGIN_BTW_PANELS, paint);

        if (moveFlag) {
            if (animationData.stepsLeft()) {
                LocatedBitmap newLocatedBitmap = animationData.getNewLocatedBitmap();
                Bitmap newBitmap = newLocatedBitmap.getBitmap();
                Point newPos = newLocatedBitmap.getPos();
                canvas.drawBitmap(newBitmap, newPos.x, newPos.y, paint);

                if (!animationData.stepsLeft()) {
                    updateChatterPhoto(newBitmap);
                    moveFlag = false;
                }
            }
        }
    }

    private void updateChatterPhoto(Bitmap faceBitmap) {
        Bitmap bottomPanelBitmapCopy = bottomPanel.getPanelBitmap().copy(bottomPanel.getPanelBitmap().getConfig(), true);
        Canvas canvas = new Canvas(bottomPanelBitmapCopy);
        canvas.drawBitmap(faceBitmap, bottomPanel.getPhotoCell().left, bottomPanel.getPhotoCell().top, paint);
        chatterBottomPanelBitmap = bottomPanelBitmapCopy;
    }

    public void startFacePhotoMoving(Bitmap faceBitmap, Rect startFaceBox) {

        Size startSize = new Size(faceBitmap.getWidth(), faceBitmap.getHeight());
        //debug(bottomPanel.getPhotoCell())
        Size endSize = new Size(bottomPanel.getPhotoCell().width(), bottomPanel.getPhotoCell().height());

        Point startPoint = new Point((int) (startFaceBox.left * xRatio), (int) (startFaceBox.top * yRatio));
        Point endPoint = new Point(LR_PANEL_MARGIN + bottomPanel.getPhotoCell().left,
                TOP_PANEL_MARGIN + topPanel.getPanelBitmap().getHeight() + VERTICAL_MARGIN_BTW_PANELS + bottomPanel.getPhotoCell().top);
        animationData = new AnimationData(faceBitmap, startPoint, endPoint, startSize, endSize, 10);
        moveFlag = true;
    }




    public void adjustRatio(Size previewSize) {
        this.xRatio = 1.0f * this.getWidth() / previewSize.getWidth();
        this.yRatio = 1.0f * this.getHeight() / previewSize.getHeight();
    }
}
