package org.corpitech.vozera.models;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.MainThread;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.corpitech.vozera.Constants;
import org.corpitech.vozera.Utils;
import org.pytorch.torchvision.TensorImageUtils;
import java.io.File;


public class EmotionRecognizer {
    private static final int INPUT_TENSOR_WIDTH = 44;
    private static final int INPUT_TENSOR_HEIGHT = 44;
    private Module mModule;


    public EmotionRecognizer(Context context) {
        final String moduleFileAbsoluteFilePath = new File(Utils.assetFilePath(context, "model.pt")).getPath();
        System.out.println(moduleFileAbsoluteFilePath);
        mModule = Module.load(moduleFileAbsoluteFilePath);

    }

    @MainThread
    public String recognizeEmotion(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT, false);
        Tensor mInputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, new float[]{0, 0, 0}, new float[]{1, 1, 1});

        final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
        final float[] scores = outputTensor.getDataAsFloatArray();
        final int[] ixs = Utils.topK(scores, 1);

        final String[] topKClassNames = new String[1];
        final float[] topKScores = new float[1];
        for (int i = 0; i < 1; i++) {
            final int ix = ixs[i];
            topKClassNames[i] = Constants.EMOTION_CLASSES[ix];
            topKScores[i] = scores[ix];
        }
        return topKClassNames[0];
    }

    public void destroy() {
        mModule.destroy();
    }

}
