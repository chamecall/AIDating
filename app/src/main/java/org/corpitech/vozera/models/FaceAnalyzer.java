package org.corpitech.vozera.models;

import android.graphics.Bitmap;
import android.graphics.Rect;

import android.os.HandlerThread;
import android.os.Looper;

import com.google.android.gms.common.util.concurrent.HandlerExecutor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FaceAnalyzer {
    private FirebaseVisionFaceDetectorOptions options;


    private HandlerExecutor handlerExecutor;

    public FaceAnalyzer() {
        this.options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
                .build();

        HandlerThread mBackgroundThread = new HandlerThread("ModulweeActivity");
        mBackgroundThread.start();

    }

    public class FaceDetectionResult {
        public Bitmap bitmap;
        public List<Rect> boxes;

        public FaceDetectionResult(Bitmap bitmap, List<Rect> boxes) {
            this.bitmap = bitmap;
            this.boxes = boxes;
        }
    }




    public List<FirebaseVisionFace> analyze(FirebaseVisionImage FBImage) {


        if (handlerExecutor == null) {
            handlerExecutor = new HandlerExecutor(Looper.myLooper());
        }

        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        List<FirebaseVisionFace> result = null;

        Task<List<FirebaseVisionFace>> task = faceDetector.detectInImage(FBImage);
        try {
            result = Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;

    }




}
