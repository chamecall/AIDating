package org.corpitech.vozera.person;

public class User {

    private float [] metrics;

    public User() {
        metrics = new float[]{0.0f, 0.0f, 0.0f, 0.0f};

    }

    public float[] getMetrics() {
        return metrics;
    }

    public void setMetrics(float[] metrics) {
        this.metrics = metrics;
    }
}
