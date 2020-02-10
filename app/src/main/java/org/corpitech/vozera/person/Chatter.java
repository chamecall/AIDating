package org.corpitech.vozera.person;

import android.graphics.Rect;

public class Chatter {
    private String emotion;
    private Float beautyScore;
    private boolean detected;
    private Rect faceBox;


    public Chatter() {
        metrics = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    }

    private float [] metrics;

    public void setFaceBox(Rect faceBox) {
        this.faceBox = faceBox;
    }

    public Rect getFaceBox() {
        return faceBox;
    }

    public float[] getMetrics() {
        return metrics;
    }

    public void setMetrics(float[] metrics) {
        this.metrics = metrics;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public void setBeautyScore(Float beautyScore) {
        this.beautyScore = beautyScore;
    }

    public String getEmotion() {
        return emotion;
    }

    public Float getBeautyScore() {
        return beautyScore;
    }

    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }
}
