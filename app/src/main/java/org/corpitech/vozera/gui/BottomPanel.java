package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.corpitech.vozera.R;

import static android.graphics.BitmapFactory.decodeResource;
import static org.corpitech.vozera.Utils.debug;

public class BottomPanel {


    private Paint paint, textBorderPaint;
    private Bitmap panelBitmap;
    private float [] cellsWidths = {0.3f, 0.3f, 0.4f};
    private Rect photoCell, nameAgeCell, emotionCell, gifCell;
    private int TEXT_SIZE = 19;
    private int AVATAR_SIZE_IN_CELL_IN_PERCENT = 80;
    private int width, height;


    public BottomPanel(Context context) {
        panelBitmap = decodeResource(context.getResources(), R.raw.bottom_panel);
        float scaleFactor = 0.7f;
        panelBitmap = Bitmap.createScaledBitmap(panelBitmap, (int)(panelBitmap.getWidth() * scaleFactor),
                (int)(panelBitmap.getHeight() * scaleFactor), false);

        width = panelBitmap.getWidth();
        height = panelBitmap.getHeight();
        //panelBitmap = panelBitmap.copy(panelBitmap.getConfig(), true);


        calcCellsSizes();
        initPaint(context);
        drawAvatarBorder();
        paint.setShadowLayer(1.0f, 1.0f, 4.0f, Color.BLACK);


    }


    private void initPaint(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);

        //paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));


        Typeface plain = Typeface.createFromAsset(context.getAssets(), "fonts/LatoBlack.ttf");
        Typeface bold = Typeface.create(plain, Typeface.BOLD);

        paint.setTypeface(bold);

//        textBorderPaint = new Paint();
//        textBorderPaint.setStyle(Paint.Style.STROKE);
//        textBorderPaint.setTextSize(TEXT_SIZE * context.getResources().getDisplayMetrics().density);
//        textBorderPaint.setTypeface(bold);

//        textBorderPaint.setStrokeWidth(2);
//        textBorderPaint.setColor(Color.LTGRAY);

    }

    private void drawAvatarBorder() {
        paint.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas(panelBitmap);
        canvas.drawRect(photoCell, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Bitmap getPanelBitmap() {
        return panelBitmap;
    }

    public Rect getPhotoCell() {
        return photoCell;
    }

    private void calcCellsSizes() {
        int photoCellWidth = (int)(cellsWidths[0] * panelBitmap.getWidth());

        int avatar_size = (int)((photoCellWidth) * AVATAR_SIZE_IN_CELL_IN_PERCENT / 100.0f);
        int avatarWidthMargin = (photoCellWidth - avatar_size) / 2;
        int avatarHeightMargin = (panelBitmap.getHeight() - avatar_size) / 2;

        photoCell = new Rect(avatarWidthMargin, avatarHeightMargin, avatarWidthMargin + avatar_size, avatarHeightMargin + avatar_size);


        int nameAgeCellRightX = photoCellWidth + (int)(cellsWidths[1] * panelBitmap.getWidth());
        nameAgeCell = new Rect(photoCellWidth, 0, nameAgeCellRightX, panelBitmap.getHeight() / 2);

        emotionCell = new Rect(photoCellWidth, panelBitmap.getHeight() / 2, nameAgeCellRightX,
                panelBitmap.getHeight());


        int gifCellWidth = (int)(cellsWidths[2] * panelBitmap.getWidth());
        int gifCellRightX = nameAgeCellRightX + gifCellWidth;

        gifCell = new Rect(nameAgeCellRightX, 0, gifCellRightX, panelBitmap.getHeight());
    }

    public Rect getGifCell() {
        return gifCell;
    }



    public Bitmap generateScoreBitmap(int totalScore) {
        Bitmap scoreBitmap = Bitmap.createBitmap(gifCell.width(), gifCell.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scoreBitmap);

        String score = String.valueOf(totalScore);
        Rect textRect = new Rect();

        paint.getTextBounds(score, 0, score.length(), textRect);
        canvas.drawText(score, (gifCell.width() - textRect.width()) / 2, (gifCell.height() + textRect.height()) / 2, paint);
        //canvas.drawText(score, gifCell.centerX() - textRect.width() / 2, gifCell.centerY() + textRect.height() / 2, textBorderPaint);
        return scoreBitmap;
    }
}
