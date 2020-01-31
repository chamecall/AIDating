package org.corpitech.vozera;

public class Chatter {
    private String emotion;
    private Float beautyScore;
    private boolean detected;

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
