package org.corpitech.vozera;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.util.Size;

import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e(Constants.TAG, "Error process asset " + assetName + " to file path");
        }
        return null;
    }

    public static int[] topK(float[] a, final int topk) {
        float[] values = new float[topk];
        Arrays.fill(values, -Float.MAX_VALUE);
        int[] ixs = new int[topk];
        Arrays.fill(ixs, -1);

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < topk; j++) {
                if (a[i] > values[j]) {
                    for (int k = topk - 1; k >= j + 1; k--) {
                        values[k] = values[k - 1];
                        ixs[k] = ixs[k - 1];
                    }
                    values[j] = a[i];
                    ixs[j] = i;
                    break;
                }
            }
        }
        return ixs;
    }

    public static int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }

    public static Rect adjustRectByBitmapSize(Rect box, Size size) {
        // avoidance of going beyond bitmap size
        int width = size.getWidth();
        int height = size.getHeight();
        Rect resRect = new Rect();
        resRect.left = box.left >= 0 ? box.left : 0;
        resRect.top = box.top >= 0 ? box.top : 0;
        resRect.right = box.right <= width ? box.right : width;
        resRect.bottom = box.bottom <= height ? box.bottom : height;

        return resRect;
    }

    public static List<Rect> adjustRectsByBitmapSize(List<Rect> boxes, Size size) {
        List<Rect> rects = new ArrayList<>();
        for (Rect box : boxes) {
            rects.add(adjustRectByBitmapSize(box, size));
        }
        return rects;
    }


}
