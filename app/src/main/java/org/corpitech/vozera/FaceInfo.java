package org.corpitech.vozera;

import android.graphics.Rect;

public class FaceInfo {
    Rect box;
    String emotion;
    Float beautyScore;

    public Rect getBox() {
        return box;
    }

    public String getEmotion() {
        return emotion;
    }

    public Float getBeautyScore() {
        return beautyScore;
    }

    public FaceInfo(Rect box, String emotion, Float beautyScore) {
        this.box = box;
        this.emotion = emotion;
        this.beautyScore = beautyScore;
    }


}