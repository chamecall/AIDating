package org.corpitech.vozera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import org.corpitech.vozera.gui.OverlayView;

public class ScanActivity extends BaseModuleActivity {
    private TextureView cameraView;
    //private boolean mAnalyzeImageErrorState;
    private OverlayView canvasView;
    Processor processor;


    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200,
            REQUEST_CODE_AUDIO_RECORD_PERMISSION = 300;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};


    protected int getContentViewLayoutId() {
        return org.corpitech.vozera.R.layout.scanning;
    }



    protected TextureView getCameraPreviewCameraView() {
        return findViewById(org.corpitech.vozera.R.id.previewArea);
    }


    protected void frameAvailable() {
        //canvasView.updateOverlay();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StatusBarUtils.setStatusBarOverlay(getWindow(), true);
        setContentView(getContentViewLayoutId());
        checkCameraPermission();
        checkAudioRecordPermission();

        this.cameraView = getCameraPreviewCameraView();
        cameraView.post(this::setupCameraX);


        canvasView = findViewById(org.corpitech.vozera.R.id.canvasView);
        canvasView.setFaceDetectionBound(findViewById(org.corpitech.vozera.R.id.face_detection_bound));

        processor = new Processor(canvasView);

    }


    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSION);
        }
    }

    private void checkAudioRecordPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_AUDIO_RECORD_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(
                        this,
                        "You can't use image classification example without granting CAMERA permission",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }


    private PreviewConfig createPreviewConfig(Size screenSize, Rational aspectRatio, int rotation, CameraX.LensFacing lensFacing) {

        return new PreviewConfig.Builder()
                .setLensFacing(lensFacing)
                .setTargetResolution(screenSize)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build();
    }

    private ImageAnalysisConfig createAnalysisConfig(Size screenSize, Rational aspectRatio, int rotation, CameraX.LensFacing lensFacing) {
        return new ImageAnalysisConfig.Builder()
                .setLensFacing(lensFacing)
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setCallbackHandler(mBackgroundHandler)
                .setTargetResolution(screenSize)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build();
    }


    private void setupCameraX() {


        CameraX.unbindAll();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        cameraView.getDisplay().getRealMetrics(displayMetrics);
        Size imageAnalysisSize = new Size(480, 360);
        Size screenSize = new Size(displayMetrics.widthPixels, displayMetrics.heightPixels);
        Rational aspectRatio = new Rational(displayMetrics.widthPixels, displayMetrics.heightPixels);
        int rotation = cameraView.getDisplay().getRotation();

        CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;
        final PreviewConfig previewConfig = createPreviewConfig(screenSize, aspectRatio, rotation, lensFacing);
        final ImageAnalysisConfig analysisConfig = createAnalysisConfig(imageAnalysisSize, aspectRatio, rotation, lensFacing);

        final Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {

            ViewGroup parent = (ViewGroup) cameraView.getParent();
            parent.removeView(cameraView);
            parent.addView(cameraView, 0);
            SurfaceTexture surfaceTexture = output.getSurfaceTexture();
            setPreviewTransform(output);

            cameraView.setSurfaceTexture(surfaceTexture);

            cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    frameAvailable();
                }
            });

        });


        final ImageAnalysis imageAnalysis = new ImageAnalysis(analysisConfig);
        imageAnalysis.setAnalyzer(this::handleImage);
        CameraX.bindToLifecycle(this, preview, imageAnalysis);

    }

    public void handleImage(ImageProxy imageProxy, int rotationDegrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }

        processor.handleImage(imageProxy, rotationDegrees);

//        catch (Exception e) {
//                    Log.e(Constants.TAG, "Error during FBImage analysis", e);
//                    mAnalyzeImageErrorState = true;
//                    runOnUiThread(() -> {
//                        if (!isFinishing()) {
//                            showErrorDialog(v -> finish());
//                        }
//                    });
//                }

    }

    private void setPreviewTransform(Preview.PreviewOutput output) {
        Matrix matrix = new Matrix();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        cameraView.getDisplay().getRealMetrics(displayMetrics);

        float centerX = cameraView.getWidth() / 2.0f;
        float centerY = cameraView.getHeight() / 2.0f;
        int angle = getDisplaySurfaceRotation(cameraView.getDisplay().getRotation());
        matrix.postRotate(-angle, centerX, centerY);

        float bufferRatio = (float) output.getTextureSize().getHeight() / output.getTextureSize().getWidth();

        int scaledWidth, scaledHeight;

        if (cameraView.getWidth() > cameraView.getHeight()) {
            scaledHeight = cameraView.getWidth();
            scaledWidth = Math.round(cameraView.getWidth() * bufferRatio);
        } else {
            scaledHeight = cameraView.getHeight();
            scaledWidth = Math.round(cameraView.getHeight() * bufferRatio);
        }

        float xScale = (float) scaledWidth / cameraView.getWidth();
        float yScale = (float) scaledHeight / cameraView.getHeight();

        matrix.preScale(xScale, yScale, centerX, centerY);
        cameraView.setTransform(matrix);
    }

    private int getDisplaySurfaceRotation(int rotation) {
        int angle = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                angle = 90;
                break;

            case Surface.ROTATION_180:
                angle = 180;
                break;

            case Surface.ROTATION_270:
                angle = 270;
                break;
        }
        return angle;
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (processor != null) {
            processor.destroy();
        }
    }

}
