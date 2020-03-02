package org.corpitech.vozera.gui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.Rational;

import org.corpitech.vozera.R;

import static android.graphics.BitmapFactory.decodeResource;
import static org.corpitech.vozera.Utils.debug;

public class TopPanel {

    private Bitmap panelBitmap;
    private float cellWidth;
    private int cellHeight;
    private float circleRadius;
    private Paint paint;
    private float rotateAngle;
    private int[] startColors, endColors;
    private Matrix[] rotateMatrices;
    private float firstCircleX;
    private Context context;
    private final int LEFT_PADDING = 10;
    private int [] icons = {R.raw.lips, R.raw.coin, R.raw.lightning, R.raw.heart};

    public TopPanel(Context context) {
        this.context = context;
        panelBitmap = decodeResource(context.getResources(), R.raw.top_panel);
        float scaleFactor = 0.7f;
        panelBitmap = Bitmap.createScaledBitmap(panelBitmap, (int)(panelBitmap.getWidth() * scaleFactor),
                (int)(panelBitmap.getHeight() * scaleFactor), false);
        //panelBitmap = panelBitmap.copy(panelBitmap.getConfig(), true);
        //circleRadius = (int) (panelBitmap.getHeight() * 0.4 / 2);
        cellWidth = (panelBitmap.getWidth() - LEFT_PADDING) / 4;
        cellHeight = panelBitmap.getHeight();

        int iconWidth;
        float circleDiameter = cellWidth / 2.0f;
        circleRadius = circleDiameter / 2.0f;
        firstCircleX = circleRadius + LEFT_PADDING;
        iconWidth = (int)(cellWidth - circleRadius * 2);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(14 * context.getResources().getDisplayMetrics().density);
        paint.setStrokeWidth(6.0f);

        paint.setColor(Color.WHITE);
        Typeface plain = Typeface.createFromAsset(context.getAssets(), "fonts/LatoBold.ttf");
        Typeface bold = Typeface.create(plain, Typeface.BOLD);
        paint.setTypeface(bold);


        startColors = new int[] {Color.rgb(255, 100, 100),
                Color.rgb(255, 255, 100),
                Color.rgb(100, 255, 255),
                Color.rgb(255, 100, 255)
        };

        endColors = new int[] {Color.rgb(100, 0, 0),
                Color.rgb(100, 100, 0),
                Color.rgb(0, 100, 100),
                Color.rgb(100, 0, 100)
        };


        rotateAngle = 270f;
        rotateMatrices = generateMatrices();
        resizeIcons(iconWidth);
    }

    public Bitmap getPanelBitmap() {
        return panelBitmap;
    }

    private void resizeIcons(int iconWidth) {
        Canvas canvas = new Canvas(panelBitmap);

        for (int i = 0; i < 4; i++) {
            Bitmap icon = decodeResource(context.getResources(), icons[i]);
            Rational aspectRatio = new Rational(icon.getWidth(), icon.getHeight());

            int iconHeight = (int)(iconWidth / aspectRatio.floatValue());
            icon = Bitmap.createScaledBitmap(icon, iconWidth, iconHeight, false);

            float startIconX = firstCircleX + circleRadius + cellWidth * i;
            canvas.drawBitmap(icon, startIconX, 0, paint);
        }
    }

    private Matrix[] generateMatrices() {
        Matrix [] rotateMatrices = new Matrix[4];
        float startX = cellWidth * 0.33f;
        float y = cellHeight / 2;
        for (int i = 0; i < 4; i++) {
            float x = startX + cellWidth * i;
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.preRotate(rotateAngle, x, y);
            rotateMatrices[i] = rotateMatrix;
        }
        return rotateMatrices;
    }


    Bitmap generatePanel(float[] offsets) {
        Bitmap panelBitmapCopy = panelBitmap.copy(panelBitmap.getConfig(), true);
        Canvas canvas = new Canvas(panelBitmapCopy);
        paint.setStyle(Paint.Style.STROKE);

        float y = cellHeight / 2;
        for (int i = 0; i < 4; i++) {
            float x = firstCircleX + cellWidth * i;
            int curColor = (int)new ArgbEvaluator().evaluate(offsets[i], startColors[i], endColors[i]);
            int [] circleColors = new int[] {startColors[i], curColor, Color.WHITE};
            Shader shader = new SweepGradient(x, y, circleColors, new float[]{0, offsets[i], offsets[i]+0.1f});
            shader.setLocalMatrix(rotateMatrices[i]);
            paint.setShader(shader);
            canvas.drawCircle(x, y, circleRadius, paint);

            Rect textRect = new Rect();
            String score = String.valueOf((int)(offsets[i] * 100));
            paint.getTextBounds(score, 0, score.length(), textRect);


        }
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < 4; i++) {
            float x = firstCircleX + cellWidth * i;
            Rect textRect = new Rect();
            String score = String.valueOf((int)(offsets[i] * 100));
            paint.getTextBounds(score, 0, score.length(), textRect);

            canvas.drawText(score, x - textRect.width() / 2.0f - 1, y + textRect.height() / 2.0f, paint);
        }
        return panelBitmapCopy;
    }
}
