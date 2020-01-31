package org.corpitech.vozera.vision;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import org.corpitech.vozera.Chatter;
import org.corpitech.vozera.Constants;
import org.corpitech.vozera.R;
import org.corpitech.vozera.Utils;
import org.corpitech.vozera.models.BeautyDefiner;
import org.corpitech.vozera.models.EmotionRecognizer;
import org.corpitech.vozera.models.FaceAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

class FaceInfo {
    Rect box;
    String emotion;
    Float beautyScore;

    public FaceInfo(Rect box, String emotion, Float beautyScore) {
        this.box = box;
        this.emotion = emotion;
        this.beautyScore = beautyScore;
    }
}

class ChatterFaceInfo {
    List<FaceInfo> facesInfo;
    Size bitmapSize;

    public ChatterFaceInfo(List<FaceInfo> faceInfo, Size bitmapSize) {
        this.facesInfo = faceInfo;
        this.bitmapSize = bitmapSize;
    }
}

public class FrameProcessingActivity extends AbstractCameraXActivity<List<ChatterFaceInfo>> {


    private boolean mAnalyzeImageErrorState;

    private OverlayView canvasView;

    private EmotionRecognizer emotionRecognizer;
    private FaceAnalyzer faceAnalyzer;
    private BeautyDefiner beautyDefiner;
    private Chatter chatter;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    FutureTask emotionRecognitionTask;
    FutureTask beautyRecognitionTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //beautyDefiner = new BeautyDefiner(this);

        //emotionRecognizer = new EmotionRecognizer(this);
        canvasView = findViewById(R.id.canvasView);
        chatter = new Chatter();
        faceAnalyzer = new FaceAnalyzer();

    }

    @Override
    protected void frameAvailable() {
        canvasView.updateOverlay();
    }


    @Override
    public void handleImage(ImageProxy imageProxy, int rotationDegrees) {

        if (mAnalyzeImageErrorState || imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        try {
            Image mediaImage = imageProxy.getImage();
            int rotation = Utils.degreesToFirebaseRotation(rotationDegrees);
            FirebaseVisionImage FBImage = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
            Bitmap bitmap = FBImage.getBitmap();

            List<FaceInfo> facesInfo = new ArrayList<>();

            List<FirebaseVisionFace> faces = faceAnalyzer.analyze(FBImage);
            Size bitmapSize = new Size(bitmap.getWidth(), bitmap.getHeight());

            for (FirebaseVisionFace face : faces) {
                Rect box = Utils.adjustRectByBitmapSize(face.getBoundingBox(), new Size(bitmap.getWidth(), bitmap.getHeight()));
                Bitmap faceBitmap = cropBitmap(bitmap, box);

//                emotionRecognitionTask = executeTaskIfDone(emotionRecognitionTask, () -> recognizeEmotionByBitmap(faceBitmap));
//                beautyRecognitionTask = executeTaskIfDone(beautyRecognitionTask, () -> defineBeautyScore(faceBitmap));

                facesInfo.add(new FaceInfo(box, chatter.getEmotion(), chatter.getBeautyScore()));
                break;
            }

            runOnUiThread(() -> canvasView.setChatterFaceInfo(new ChatterFaceInfo(facesInfo, bitmapSize)));


            if (!chatter.isDetected() && !facesInfo.isEmpty()) {
                chatter.setDetected(true);
                canvasView.startAnimation();
            } else if (chatter.isDetected() && faces.isEmpty()) {
                chatter.setDetected(false);
            }

        } catch (Exception e) {
            Log.e(Constants.TAG, "Error during FBImage analysis", e);
            mAnalyzeImageErrorState = true;
            runOnUiThread(() -> {
                if (!isFinishing()) {
                    showErrorDialog(v -> FrameProcessingActivity.this.finish());
                }
            });
        }

    }

    FutureTask executeTaskIfDone(FutureTask task, Runnable runnable) {
        if (task == null || task.isDone()) {
            task = (FutureTask)executorService.submit(runnable);
        }
        return task;
    }



    private void recognizeEmotionByBitmap(Bitmap bitmap) {
        chatter.setEmotion(emotionRecognizer.recognizeEmotion(bitmap));
    }

    private void defineBeautyScore(Bitmap bitmap) {
       chatter.setBeautyScore(((int) (beautyDefiner.defineBeauty(bitmap) * 100)) / 100.0f);
    }


    private Bitmap cropBitmap(Bitmap bitmap, Rect box) {
        return Bitmap.createBitmap(bitmap, box.left, box.top, box.right - box.left,
                box.bottom - box.top);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (emotionRecognizer != null) {
            emotionRecognizer.destroy();
        }
        if (beautyDefiner != null) {
            beautyDefiner.destroy();
        }
    }
}
