package org.corpitech.vozera.vision;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Rational;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import org.corpitech.vozera.BaseModuleActivity;
import org.corpitech.vozera.R;
import org.corpitech.vozera.StatusBarUtils;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;

public abstract class AbstractCameraXActivity<R> extends BaseModuleActivity {
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 200,
            REQUEST_CODE_AUDIO_RECORD_PERMISSION = 300;
    private static final String[] PERMISSIONS = {Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};


    protected int getContentViewLayoutId() {
        return org.corpitech.vozera.R.layout.scanning;
    }

    private TextureView cameraView;

    protected TextureView getCameraPreviewCameraView() {
        return findViewById(org.corpitech.vozera.R.id.previewArea);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusBarOverlay(getWindow(), true);
        setContentView(getContentViewLayoutId());
        checkCameraPermission();
        checkAudioRecordPermission();

        this.cameraView = getCameraPreviewCameraView();
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

    protected abstract void frameAvailable();

    @WorkerThread
    @Nullable
    protected abstract void handleImage(ImageProxy image, int rotationDegrees);


}
