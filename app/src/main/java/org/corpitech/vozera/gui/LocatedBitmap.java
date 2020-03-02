package org.corpitech.vozera.gui;

import android.graphics.Bitmap;
import android.graphics.Point;

class LocatedBitmap {
    private Bitmap bitmap;
    private Point pos;

    public LocatedBitmap(Bitmap bitmap, Point pos) {
        this.bitmap = bitmap;
        this.pos = pos;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Point getPos() {
        return pos;
    }

}