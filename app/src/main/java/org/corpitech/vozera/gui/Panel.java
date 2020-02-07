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

import static android.graphics.BitmapFactory.decodeResource;

public class Panel {

    private Bitmap panelBitmap;
    private int cellWidth;
    private int cellHeight;
    private int circleRadius;
    private Paint paint;
    private Context context;
    private float rotateAngle;
    private int[] startColors, endColors;
    private Matrix[] rotateMatrices;

    public Panel(Context context, int bitmapId) {
        this.context = context;
        panelBitmap = decodeResource(context.getResources(), bitmapId);
        panelBitmap = panelBitmap.copy(panelBitmap.getConfig(), true);
        circleRadius = (int) (panelBitmap.getHeight() * 0.55 / 2);
        cellWidth = panelBitmap.getWidth() / 4;
        cellHeight = panelBitmap.getHeight();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(16 * context.getResources().getDisplayMetrics().density);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setStrokeWidth(15.0f);


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

    }

    private Matrix[] generateMatrices() {
        Matrix [] rotateMatrices = new Matrix[4];
        int startX = cellWidth / 2;
        int y = cellHeight / 2;
        for (int i = 0; i < 4; i++) {
            int x = startX + cellWidth * i;
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.preRotate(rotateAngle, x, y);
            rotateMatrices[i] = rotateMatrix;
        }
        return rotateMatrices;
    }


    public Bitmap generatePanel(float [] offsets) {
        Bitmap panelBitmapCopy = panelBitmap.copy(panelBitmap.getConfig(), true);
        Canvas canvas = new Canvas(panelBitmapCopy);
        paint.setStyle(Paint.Style.STROKE);


        int startX = cellWidth / 2;
        int y = cellHeight / 2;
        for (int i = 0; i < 4; i++) {
            int x = startX + cellWidth * i;
            int curColor = (int)new ArgbEvaluator().evaluate(offsets[i], startColors[i], endColors[i]);
            int [] circleColors = new int[] {startColors[i], curColor, Color.WHITE};
            Shader shader = new SweepGradient(x, y, circleColors, new float[]{0, offsets[i], offsets[i]+0.1f});
            shader.setLocalMatrix(rotateMatrices[i]);
            paint.setShader(shader);
            canvas.drawCircle(x, y, circleRadius, paint);

        }
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < 4; i++) {
            int x = startX + cellWidth * i;
            Rect textRect = new Rect();
            String score = String.valueOf((int)(offsets[i] * 100));
            paint.getTextBounds(score, 0, score.length(), textRect);
            canvas.drawText(score, x - textRect.width() / 2, y + textRect.height() / 2, paint);
            System.out.println("## " + textRect.width() + "  "  + textRect.height());
        }
        return panelBitmapCopy;
    }
}
