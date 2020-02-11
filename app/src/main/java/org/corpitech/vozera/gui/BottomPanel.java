package org.corpitech.vozera.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import org.corpitech.vozera.R;

import static android.graphics.BitmapFactory.decodeResource;

public class BottomPanel {


    private Paint paint;
    private Bitmap panelBitmap;
    private float [] cellsWidths = {0.33f, 0.33f, 0.33f};
    private Rect photoCell, nameAgeCell, emotionCell, gifCell;


    public BottomPanel(Context context) {
        panelBitmap = decodeResource(context.getResources(), R.raw.bottom_panel);
        panelBitmap = panelBitmap.copy(panelBitmap.getConfig(), true);

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

    public Bitmap generatePanel(Bitmap face) {

        Bitmap panelBitmapCopy = panelBitmap.copy(panelBitmap.getConfig(), true);
        Canvas canvas = new Canvas(panelBitmapCopy);

        if (face != null) {
            Bitmap scaledPhoto = Bitmap.createScaledBitmap(face, photoCell.width(), photoCell.height(), false);
            canvas.drawBitmap(scaledPhoto, photoCell.left, photoCell.top, paint);
        }


        return panelBitmapCopy;
    }
}
