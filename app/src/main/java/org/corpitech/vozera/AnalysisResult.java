package org.corpitech.vozera;

public class AnalysisResult {

    public final String[] topNClassNames;
    public final float[] topNScores;
    public long analysisDuration;
    public final long moduleForwardDuration;

    public AnalysisResult(String[] topNClassNames, float[] topNScores,
                          long moduleForwardDuration) {
        this.topNClassNames = topNClassNames;
        this.topNScores = topNScores;
        this.moduleForwardDuration = moduleForwardDuration;
        this.analysisDuration = 0;
    }
}
