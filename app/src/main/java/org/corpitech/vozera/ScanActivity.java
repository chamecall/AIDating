package org.corpitech.vozera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

import org.corpitech.vozera.gui.OverlayView;
import org.corpitech.vozera.gui.PanelsView;

import static org.corpitech.vozera.Utils.debug;

public class ScanActivity extends BaseModuleActivity {
    private TextureView cameraView;
    //private boolean mAnalyzeImageErrorState;
    private OverlayView canvasView;
    Processor processor;
    PanelsView panelsView;


    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200,
            REQUEST_CODE_AUDIO_RECORD_PERMISSION = 300;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};


    protected void frameAvailable() {
        canvasView.invalidate();
        panelsView.invalidate();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //StatusBarUtils.setStatusBarOverlay(getWindow(), true);
        setContentView(org.corpitech.vozera.R.layout.scanning);
        checkCameraPermission();
        checkAudioRecordPermission();

        panelsView = findViewById(R.id.panels_view);

        canvasView = findViewById(R.id.canvasView);
        canvasView.setFaceDetectionGif(findViewById(R.id.face_detection_bound));
        canvasView.setlBrainGif(findViewById(R.id.l_brain_animation));
        canvasView.setrBrainGif(findViewById(R.id.r_brain_animation));
        canvasView.setlBrainBack(findViewById(R.id.l_brain_back));
        canvasView.setrBrainBack(findViewById(R.id.r_brain_back));

        processor = new Processor(panelsView, canvasView, this);
        this.cameraView = findViewById(org.corpitech.vozera.R.id.previewArea);
        cameraView.post(this::setupCameraX);

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

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        cameraView.getDisplay().getRealMetrics(displayMetrics);
        Size previewSize = new Size(cameraView.getWidth(), cameraView.getHeight());
        Rational aspectRatio = new Rational(cameraView.getWidth(), cameraView.getHeight());
        int rotation = cameraView.getDisplay().getRotation();

        CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;

        final PreviewConfig previewConfig = createPreviewConfig(previewSize, aspectRatio, rotation, lensFacing);
        final ImageAnalysisConfig analysisConfig = createAnalysisConfig(previewSize, aspectRatio, rotation, lensFacing);

        final Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {

            ViewGroup parent = (ViewGroup) cameraView.getParent();
            parent.removeView(cameraView);
            parent.addView(cameraView, 0);
            SurfaceTexture surfaceTexture = output.getSurfaceTexture();

            setPreviewTransform(output);
            // to calc sizes ratio between bitmaps and overlay view
            canvasView.adjustRatio(output.getTextureSize());
            panelsView.adjustRatio(output.getTextureSize());
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

        float centerX = cameraView.getWidth() / 2.0f;
        float centerY = cameraView.getHeight() / 2.0f;


        int angle = getDisplaySurfaceRotation(cameraView.getDisplay().getRotation());
        matrix.postRotate(-angle, centerX, centerY);

        float bufferRatio = (float) output.getTextureSize().getWidth() / output.getTextureSize().getHeight();

        float scaledWidth, scaledHeight;
        if (angle == 90 || angle == 270) {
            scaledWidth = cameraView.getWidth() / bufferRatio;
            scaledHeight = cameraView.getWidth();
        } else {
            scaledWidth = cameraView.getWidth();
            scaledHeight = cameraView.getWidth() / bufferRatio;
        }

//
        debug(scaledWidth, cameraView.getWidth());
        debug(scaledHeight, cameraView.getHeight());
        float xScale = (float) scaledWidth / cameraView.getWidth();
        float yScale = scaledHeight / cameraView.getHeight();
//
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
