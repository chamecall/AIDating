package org.corpitech.vozera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.os.Handler;
import android.util.Size;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import org.corpitech.vozera.gui.OverlayView;
import org.corpitech.vozera.gui.TopPanel;
import org.corpitech.vozera.models.BeautyDefiner;
import org.corpitech.vozera.models.EmotionRecognizer;
import org.corpitech.vozera.models.FaceAnalyzer;
import org.corpitech.vozera.person.Chatter;
import org.corpitech.vozera.person.User;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


class Processor {

    private EmotionRecognizer emotionRecognizer;
    private FaceAnalyzer faceAnalyzer;
    private BeautyDefiner beautyDefiner;
    private Chatter chatter;
    private User user;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private FutureTask emotionRecognitionTask;
    private FutureTask beautyRecognitionTask;
    private OverlayView canvasView;
    private Context context;
    private TopPanel topPanel;
    private Random random;
    private final int METRICS_NUM = 4;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            float [] newChatterMetrics = generateRandomNums(), newUserMetrics = generateRandomNums();
            float [] chatterMetricsDiffs = subtractArrays(newChatterMetrics, chatter.getMetrics());
            float [] userMetricsDiffs = subtractArrays(newUserMetrics, user.getMetrics());
            float maxValue = getMaxInArrays(chatterMetricsDiffs, userMetricsDiffs);

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, maxValue);
            valueAnimator.addUpdateListener(valueAnimator1 -> {
                float curFraction = valueAnimator.getAnimatedFraction();
                float [] curChatterValues = new float[METRICS_NUM];
                float [] curUserValues = new float[METRICS_NUM];
                for (int i = 0; i < METRICS_NUM; i++) {
                    System.out.println(chatter.getMetrics()[i] + " " + chatterMetricsDiffs[i] + " " + curFraction);
                    curChatterValues[i] = chatter.getMetrics()[i] + chatterMetricsDiffs[i] * curFraction;
                    curUserValues[i] = user.getMetrics()[i] + userMetricsDiffs[i] * curFraction;
                }
                canvasView.setTLPanel(topPanel.generatePanel(curChatterValues));
                canvasView.setTRPanel(topPanel.generatePanel(curUserValues));

                canvasView.post(() -> canvasView.invalidate());
            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    chatter.setMetrics(newChatterMetrics);
                    user.setMetrics(newUserMetrics);
                }
            });

            valueAnimator.setDuration(500);
            valueAnimator.start();



            timerHandler.postDelayed(this, 5000);
        }
    };


    private float getMaxInArrays(float[]... arrays) {
        float max = getMaxInArray(arrays[0]);
        for (int i = 1; i < arrays.length; i++) {
            float curArrMax = getMaxInArray(arrays[i]);
            if (curArrMax > max) {
                max = curArrMax;
            }
        }
        return max;
    }

    private float getMaxInArray(float [] array) {
        float max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }

    private float [] subtractArrays(float [] fArr, float [] sArr) {
        float [] diffArr = new float[4];
        for (int i = 0; i < fArr.length; i++) {
            diffArr[i] = fArr[i] - sArr[i];
        }
        return diffArr;
    }

    private Handler timerHandler = new Handler();

    public Processor(OverlayView overlayView, Context context) {
        random = new Random();
        topPanel = new TopPanel(context);

        canvasView = overlayView;
        canvasView.setTLPanel(topPanel.generatePanel(new float[]{0, 0, 0, 0}));
        canvasView.setTRPanel(topPanel.generatePanel(new float[]{0, 0, 0, 0}));
        //beautyDefiner = new BeautyDefiner(this);

        //emotionRecognizer = new EmotionRecognizer(this);
        chatter = new Chatter();
        user = new User();
        faceAnalyzer = new FaceAnalyzer();


        timerHandler.postDelayed(timerRunnable, 5000);




    }


    private float[] generateRandomNums() {
        float[] rArr = new float[4];
        for (int i = 0; i < 4; i++) {
            rArr[i] = random.nextFloat() - 0.01f;
        }
        return rArr;
    }


    public void handleImage(ImageProxy imageProxy, int rotationDegrees) {

        Image mediaImage = imageProxy.getImage();
        int rotation = Utils.degreesToFirebaseRotation(rotationDegrees);
        FirebaseVisionImage FBImage = FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
        Bitmap bitmap = FBImage.getBitmap();
        FirebaseVisionFace face = faceAnalyzer.analyze(FBImage);

        Size bitmapSize = new Size(bitmap.getWidth(), bitmap.getHeight());

        if (face != null) {
            Rect box = Utils.adjustRectByBitmapSize(face.getBoundingBox(), new Size(bitmap.getWidth(), bitmap.getHeight()));
            Bitmap faceBitmap = cropBitmap(bitmap, box);

//                emotionRecognitionTask = executeTaskIfDone(emotionRecognitionTask, () -> recognizeEmotionByBitmap(faceBitmap));
//                beautyRecognitionTask = executeTaskIfDone(beautyRecognitionTask, () -> defineBeautyScore(faceBitmap));


            chatter.setFaceBox(box);
            canvasView.updateFaceBox(chatter.getFaceBox());
            canvasView.setTLPanel(topPanel.generatePanel(generateRandomNums()));

            if (!chatter.isDetected()) {
                chatter.setDetected(true);
                canvasView.startFaceBoundAnimation(chatter.getFaceBox());
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

        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}
