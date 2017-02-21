package unc.edu.kewang.sensorplot.sensorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.v4.util.CircularArray;
import android.util.AttributeSet;

import unc.edu.kewang.sensorplot.sensoractivity.SensorDetailActivity;

public class VectorSensor2DPlotView extends Sensor2DPlotView {
    CircularArray<Float> mX;
    CircularArray<Float> mY;
    CircularArray<Float> mZ;
    Paint mXPaint;
    Paint mYPaint;
    Paint mZPaint;
    Path mXPath;
    Path mYPath;
    Path mZPath;

    private void setup() {
        mX = new CircularArray<>(MAX_DATA_POINT);
        mY = new CircularArray<>(MAX_DATA_POINT);
        mZ = new CircularArray<>(MAX_DATA_POINT);
        mRelativeDataInsertionTime = new CircularArray<>(MAX_DATA_POINT);
        mXPaint = new Paint();
        mXPaint.setColor(Color.RED);
        mXPaint.setStyle(Paint.Style.STROKE);
        mXPaint.setStrokeWidth(LINE_WIDTH);
        mYPaint = new Paint();
        mYPaint.setColor(Color.GREEN);
        mYPaint.setStyle(Paint.Style.STROKE);
        mYPaint.setStrokeWidth(LINE_WIDTH);
        mZPaint = new Paint();
        mZPaint.setColor(Color.BLUE);
        mZPaint.setStyle(Paint.Style.STROKE);
        mZPaint.setStrokeWidth(LINE_WIDTH);
        mXPath = new Path();
        mYPath = new Path();
        mZPath = new Path();

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(mRunnable, SensorDetailActivity.SENSOR_SAMPLE_DELAY_IN_MILLISECONDS / 2);
                if (!mX.isEmpty()) {
                    float lastX = mX.getLast();
                    float lastY = mY.getLast();
                    float lastZ = mZ.getLast();
                    addData(lastX, lastY, lastZ);
                }
                invalidate();
            }
        };
        mStartupTime = System.currentTimeMillis();
        mHandler.postDelayed(mRunnable, SensorDetailActivity.SENSOR_SAMPLE_DELAY_IN_MILLISECONDS / 2);
    }

    public VectorSensor2DPlotView(Context context) {
        super(context);
        setup();
    }

    public VectorSensor2DPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public VectorSensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public VectorSensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    public void addData(final float x, final float y, final float z) {
        if (mX.size() >= MAX_DATA_POINT) {
            int exceedSize = mX.size() - MAX_DATA_POINT;
            for (int i = 0; i < exceedSize; ++i) {
                mX.popFirst();
                mY.popFirst();
                mZ.popFirst();
                mRelativeDataInsertionTime.popFirst();
            }
        }
        long currentSensorReadingTime = System.currentTimeMillis();
        mRelativeDataInsertionTime.addLast(currentSensorReadingTime - mStartupTime);
        mX.addLast(x);
        mY.addLast(y);
        mZ.addLast(z);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // update plot canvas size
        int viewHeight = getHeight();
        int viewWidth = getWidth();
        updatePlotCanvas(viewWidth, viewHeight);

        // update data scale
        mMaxY = Float.MIN_VALUE;
        mMinY = Float.MAX_VALUE;
        mMaxX = Long.MIN_VALUE;
        mMinX = Long.MAX_VALUE;
        for (int i = 0; i < mX.size(); ++i) {
            mMaxY = Math.max(mMaxY, mX.get(i));
            mMaxY = Math.max(mMaxY, mY.get(i));
            mMaxY = Math.max(mMaxY, mZ.get(i));
            mMinY = Math.min(mMinY, mX.get(i));
            mMinY = Math.min(mMinY, mY.get(i));
            mMinY = Math.min(mMinY, mZ.get(i));

            mMaxX = Math.max(mMaxX, mRelativeDataInsertionTime.get(i));
            mMinX = Math.min(mMinX, mRelativeDataInsertionTime.get(i));
        }
        mScaleY = Math.abs(mMaxY - mMinY);
        mScaleX = Math.abs(mMaxX - mMinX);

        drawAxis(canvas);
        drawGrid(canvas);
        drawLegends(canvas);

        // draw three curves
        mXPath.reset();
        mYPath.reset();
        mZPath.reset();
        // draw data
        if (mX.size() > 0) {
            float data_x = getCanvasYForValueY(mX.get(0));
            float data_y = getCanvasYForValueY(mY.get(0));
            float data_z = getCanvasYForValueY(mZ.get(0));
            float x = getCanvasXForValueX(mRelativeDataInsertionTime.get(0));
            mXPath.moveTo(x, data_x);
            mYPath.moveTo(x, data_y);
            mZPath.moveTo(x, data_z);
        }
        for (int i = 1; i < mX.size(); ++i) {
            float data_x = getCanvasYForValueY(mX.get(i));
            float data_y = getCanvasYForValueY(mY.get(i));
            float data_z = getCanvasYForValueY(mZ.get(i));
            float x = getCanvasXForValueX(mRelativeDataInsertionTime.get(i));
            mXPath.lineTo(x, data_x);
            mYPath.lineTo(x, data_y);
            mZPath.lineTo(x, data_z);
        }
        canvas.drawPath(mXPath, mXPaint);
        canvas.drawPath(mYPath, mYPaint);
        canvas.drawPath(mZPath, mZPaint);
    }
}

