package org.corpitech.vozera;

import android.util.Size;

import java.util.List;

public class ChatterFaceInfo {
    FaceInfo faceInfo;
    Size bitmapSize;

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public Size getBitmapSize() {
        return bitmapSize;
    }


    public ChatterFaceInfo(FaceInfo faceInfo, Size bitmapSize) {
        this.faceInfo = faceInfo;
        this.bitmapSize = bitmapSize;
    }
}