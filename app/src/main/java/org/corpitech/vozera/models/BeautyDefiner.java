package org.corpitech.vozera.models;
import android.content.Context;
import android.graphics.Bitmap;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.corpitech.vozera.Utils;
import org.pytorch.torchvision.TensorImageUtils;
import java.io.File;


public class BeautyDefiner {
    private static final int INPUT_TENSOR_WIDTH = 224;
    private static final int INPUT_TENSOR_HEIGHT = 224;
    private Module mModule;


    public BeautyDefiner(Context context) {
        final String moduleFileAbsoluteFilePath = new File(Utils.assetFilePath(context, "model.pt")).getPath();
        mModule = Module.load(moduleFileAbsoluteFilePath);
    }

    public Float defineBeauty(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT, false);
        Tensor mInputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, new float[]{0, 0, 0}, new float[]{1, 1, 1});

        final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
        return outputTensor.getDataAsFloatArray()[0];
    }

    public void destroy() {
        mModule.destroy();
    }

}
