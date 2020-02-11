package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import org.corpitech.vozera.R;

import static android.graphics.BitmapFactory.decodeResource;

public class BottomPanel {


    private Paint paint;
    private Bitmap panelBitmap;
    private float [] cellsWidths = {0.33f, 0.33f, 0.33f};
    private Rect photoCell, nameAgeCell, emotionCell, gifCell;
    private Bitmap prevChatterPhoto, prevUserPhoto;

    public BottomPanel(Context context) {
        panelBitmap = decodeResource(context.getResources(), R.raw.bottom_panel);
        float scaleFactor = 0.7f;
        panelBitmap = Bitmap.createScaledBitmap(panelBitmap, (int)(panelBitmap.getWidth() * scaleFactor),
                (int)(panelBitmap.getHeight() * scaleFactor), false);
        panelBitmap = panelBitmap.copy(panelBitmap.getConfig(), true);



        calcCellsSizes();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(17 * context.getResources().getDisplayMetrics().density);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

    }

    private void calcCellsSizes() {
        int photoCellRightX = (int)(cellsWidths[0] * panelBitmap.getWidth());
        photoCell = new Rect(0, 0, photoCellRightX, panelBitmap.getHeight());

        int nameAgeCellRightX = photoCellRightX + (int)(cellsWidths[1] * panelBitmap.getWidth());
        nameAgeCell = new Rect(photoCellRightX, 0, nameAgeCellRightX, panelBitmap.getHeight() / 2);

        emotionCell = new Rect(photoCellRightX, panelBitmap.getHeight() / 2, nameAgeCellRightX,
                panelBitmap.getHeight());

        int gifCellRightX = nameAgeCellRightX + (int)(cellsWidths[2] * panelBitmap.getWidth());
        gifCell = new Rect(nameAgeCellRightX, 0, gifCellRightX, panelBitmap.getHeight());
    }

    public Rect getGifCell() {
        return gifCell;
    }

    public Bitmap generateChatterPanel(Bitmap face, int totalScore) {
        if (face != null) {
            prevChatterPhoto = face;
        }
        return generatePanel(prevChatterPhoto, totalScore);
    }

    public Bitmap generateUserPhoto(Bitmap face, int totalScore) {
        if (face != null) {
            prevUserPhoto = face;
        }
        return generatePanel(prevUserPhoto, totalScore);
    }

    public Bitmap generatePanel(Bitmap face, int totalScore) {

        Bitmap panelBitmapCopy = panelBitmap.copy(panelBitmap.getConfig(), true);
        Canvas canvas = new Canvas(panelBitmapCopy);



        if (face != null) {
            Bitmap scaledPhoto = Bitmap.createScaledBitmap(face, photoCell.width(), photoCell.height(), false);
            canvas.drawBitmap(scaledPhoto, photoCell.left, photoCell.top, paint);
        }

        String score = String.valueOf(totalScore);
        Rect textRect = new Rect();

        paint.getTextBounds(score, 0, score.length(), textRect);
        canvas.drawText(score, gifCell.centerX() - textRect.width() / 2, gifCell.centerY() + textRect.height() / 2, paint);

        return panelBitmapCopy;
    }
}
