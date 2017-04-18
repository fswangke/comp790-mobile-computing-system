package unc.cs.kewang.neuraldigitrecognizer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kewang on 4/17/17.
 */

public class DrawView extends View {
    private static final String TAG = DrawView.class.getSimpleName();
    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public static final int MNIST_WIDTH = 28;
    public static final int MNIST_HEIGHT = 28;

    @Override
    protected void onDraw(Canvas canvas) {
        if (mTrajectories == null) {
            return;
        }
        if (!isInitialized) {
            isInitialized = init(MNIST_WIDTH, MNIST_HEIGHT);
        }

        if (mBitmap == null) {
            return;
        }
        int startIndex = mDrawnTrajectoriesNumber - 1;
        if (startIndex < 0) {
            startIndex = 0;
        }
        mPaint.setAntiAlias(true);

        for (int trajectoryIndex = startIndex; trajectoryIndex < mTrajectories.getTotalTrajectoryNumber(); ++trajectoryIndex) {
            DrawTrajectory.Trajectory trajectory = mTrajectories.getTrajectory(trajectoryIndex);
            mPaint.setColor(trajectory.getColor());

            int pointNumber = trajectory.getTrajectoryPointsNumber();
            if (pointNumber < 1) {
                continue;
            }
            DrawTrajectory.TrajectoryPoint point = trajectory.getTrajectoryPoint(0);
            float lastX = point.x;
            float lastY = point.y;
            for (int pointIndex = 0; pointIndex < pointNumber; ++pointIndex) {
                point = trajectory.getTrajectoryPoint(pointIndex);
                float x = point.x;
                float y = point.y;
                mCanvas.drawLine(lastX, lastY, x, y, mPaint);
                lastX = x;
                lastY = y;
            }
        }

        canvas.drawBitmap(mBitmap, mMatrixFromCanvas, mPaint);
        mDrawnTrajectoriesNumber = mTrajectories.getTotalTrajectoryNumber();
    }

    public PointF projectToCanvas(float viewX, float viewY) {
        float tmpPoints[] = new float[2];
        tmpPoints[0] = viewX;
        tmpPoints[1] = viewY;

        mMatrixFromView.mapPoints(tmpPoints);
        PointF out = new PointF();
        out.x = tmpPoints[0];
        out.y = tmpPoints[1];

//        Log.d(TAG, String.format("Canvas location %f, %f", out.x, out.y));
        return out;
    }

    private boolean isInitialized = false;
    private Matrix mMatrixFromCanvas = new Matrix();
    private Matrix mMatrixFromView = new Matrix();
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private DrawTrajectory mTrajectories;

    public void setTrajectories(DrawTrajectory trajectories) {
        mTrajectories = trajectories;
    }

    private int mDrawnTrajectoriesNumber = 0;
    private int mCanvasHeight = MNIST_HEIGHT;
    private int mCanvasWidth = MNIST_WIDTH;

    public void reset() {
        mDrawnTrajectoriesNumber = 0;
        if (mBitmap != null) {
            mPaint.setColor(Color.WHITE);
            mCanvas.drawRect(new Rect(0, 0, mCanvasWidth, mCanvasHeight), mPaint);
        }
    }

    public boolean init(int canvasWidth, int canvasHeight) {
        // canvas size
        this.mCanvasHeight = canvasHeight;
        this.mCanvasWidth = canvasWidth;
        float fpCanvasHeight = (float) (canvasHeight);
        float fpCanvasWidth = (float) (canvasWidth);

        // view size
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        // scales
        float widthScale = viewWidth / fpCanvasWidth;
        float heightScale = viewHeight / fpCanvasHeight;
//        Log.d(TAG, String.format("Scale %f, %f", widthScale, heightScale));

        float scale = Math.min(widthScale, heightScale);

        float canvasCenterX = fpCanvasWidth * scale / 2;
        float canvasCenterY = fpCanvasHeight * scale / 2;
        float canvasTranslationX = viewWidth / 2 - canvasCenterX;
        float canvasTranslationY = viewHeight / 2 - canvasCenterY;
//        Log.d(TAG, String.format("Center %f, %f", canvasCenterX, canvasCenterY));
//        Log.d(TAG, String.format("Translation %f, %f", canvasTranslationX, canvasTranslationY));

        mMatrixFromCanvas.setScale(scale, scale);
        mMatrixFromCanvas.postTranslate(canvasTranslationX, canvasTranslationY);
        mMatrixFromCanvas.invert(mMatrixFromView);

        return true;
    }

    public void onResume() {
        createBitmap();
    }

    public void onPause() {
        releaseBitmap();
    }

    private void createBitmap() {
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = Bitmap.createBitmap(mCanvasWidth, mCanvasHeight, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        reset();
    }

    private void releaseBitmap() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            mCanvas = null;
        }
        reset();
    }

    public float[] getImagePixels() {
        if(mBitmap == null) {
            return null;
        }

        float[] pixels = new float[mCanvasHeight * mCanvasWidth];
        int[] rawPixels = new int[mCanvasHeight * mCanvasWidth];
        mBitmap.getPixels(rawPixels, 0, mCanvasWidth, 0, 0, mCanvasWidth, mCanvasHeight);

        for(int i = 0; i < pixels.length; ++i) {
            pixels[i] = rawPixels[i];
        }
        return pixels;
    }
}
