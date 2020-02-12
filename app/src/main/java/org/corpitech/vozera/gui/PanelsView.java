package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PanelsView extends View {
    private Bitmap bottomPanel;
    private int topPanelHeight;
    private Paint paint;

    public final static int TOP_PANEL_MARGIN = 5, LR_PANEL_MARGIN = 10, VERTICAL_MARGIN_BTW_PANELS = 5;

    public PanelsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setTlPanelHeight(int _topPanelHeight) {
        this.topPanelHeight = _topPanelHeight;
    }

    public void setBottomPanel(Bitmap _bottomPanel) {
        bottomPanel = _bottomPanel;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bottomPanel, LR_PANEL_MARGIN, TOP_PANEL_MARGIN + topPanelHeight + VERTICAL_MARGIN_BTW_PANELS, paint);
        canvas.drawBitmap(bottomPanel, getWidth() - bottomPanel.getWidth() - LR_PANEL_MARGIN, TOP_PANEL_MARGIN + topPanelHeight + VERTICAL_MARGIN_BTW_PANELS, paint);
    }
}
