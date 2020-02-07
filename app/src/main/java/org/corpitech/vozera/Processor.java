package org.corpitech.vozera;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.util.Size;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import org.corpitech.vozera.gui.OverlayView;
import org.corpitech.vozera.models.BeautyDefiner;
import org.corpitech.vozera.models.EmotionRecognizer;
import org.corpitech.vozera.models.FaceAnalyzer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

class Processor {

    private EmotionRecognizer emotionRecognizer;
    private FaceAnalyzer faceAnalyzer;
    private BeautyDefiner beautyDefiner;
    private Chatter chatter;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private FutureTask emotionRecognitionTask;
    private FutureTask beautyRecognitionTask;
    private OverlayView canvasView;


    public Processor(OverlayView overlayView) {
        canvasView = overlayView;
        //beautyDefiner = new BeautyDefiner(this);

        //emotionRecognizer = new EmotionRecognizer(this);
        chatter = new Chatter();
        faceAnalyzer = new FaceAnalyzer();

    }


    public void handleImage(ImageProxy imageProxy, int rotationDegrees) {

            Image mediaImage = imageProxy.getImage();
            int rotation = Utils.degreesToFirebaseRotation(rotationDegrees);
            FirebaseVisionImage FBImage = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
            Bitmap bitmap = FBImage.getBitmap();
            FirebaseVisionFace face = faceAnalyzer.analyze(FBImage);

            Size bitmapSize = new Size(bitmap.getWidth(), bitmap.getHeight());

            FaceInfo faceInfo;
            if (face != null) {
                Rect box = Utils.adjustRectByBitmapSize(face.getBoundingBox(), new Size(bitmap.getWidth(), bitmap.getHeight()));
                Bitmap faceBitmap = cropBitmap(bitmap, box);

//                emotionRecognitionTask = executeTaskIfDone(emotionRecognitionTask, () -> recognizeEmotionByBitmap(faceBitmap));
//                beautyRecognitionTask = executeTaskIfDone(beautyRecognitionTask, () -> defineBeautyScore(faceBitmap));

                faceInfo = new FaceInfo(box, chatter.getEmotion(), chatter.getBeautyScore());
                canvasView.setFaceInfo(new ChatterFaceInfo(faceInfo, bitmapSize));


                if (!chatter.isDetected()) {
                    chatter.setDetected(true);
                    canvasView.startFaceBoundAnimation();
                }

            } else {
                chatter.setDetected(false);
            }


    }


    FutureTask executeTaskIfDone(FutureTask task, Runnable runnable) {
        if (task == null || task.isDone()) {
            task = (FutureTask) executorService.submit(runnable);
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

    public void destroy() {
        if (emotionRecognizer != null) {
            emotionRecognizer.destroy();
        }
        if (beautyDefiner != null) {
            beautyDefiner.destroy();
        }
    }
}
