package unc.edu.kewang.sensorplot.sensorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.util.CircularArray;
import android.util.AttributeSet;


public class ScalarSensor2DPlotView extends Sensor2DPlotView {
    float sum_0 = 0;
    float sum_1 = 0;
    float sum_2 = 0;
    CircularArray<Float> mData;
    CircularArray<Float> mMean;
    CircularArray<Float> mStd;
    Paint mDataPaint;
    Paint mMeanPaint;
    Paint mStdPaint;
    Path mDataPath;
    Path mMeanPath;
    Path mStdPath;

    private void setup() {
        mData = new CircularArray<>(MAX_DATA_POINT);
        mMean = new CircularArray<>(MAX_DATA_POINT);
        mStd = new CircularArray<>(MAX_DATA_POINT);
        mDataPaint = new Paint();
        mDataPaint.setColor(Color.RED);
        mDataPaint.setStyle(Paint.Style.STROKE);
        mDataPaint.setStrokeWidth(LINE_WIDTH);
        mMeanPaint = new Paint();
        mMeanPaint.setColor(Color.GREEN);
        mMeanPaint.setStyle(Paint.Style.STROKE);
        mMeanPaint.setStrokeWidth(LINE_WIDTH);
        mStdPaint = new Paint();
        mStdPaint.setColor(Color.BLUE);
        mStdPaint.setStyle(Paint.Style.STROKE);
        mStdPaint.setStrokeWidth(LINE_WIDTH);
        mDataPath = new Path();
        mMeanPath = new Path();
        mStdPath = new Path();
    }

    public ScalarSensor2DPlotView(Context context) {
        super(context);
        setup();
    }

    public ScalarSensor2DPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ScalarSensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public ScalarSensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    public void addData(final float data) {
        if (mData.size() >= MAX_DATA_POINT) {
            int exceedSize = mData.size() - MAX_DATA_POINT;
            for (int i = 0; i < exceedSize; ++i) {
                mData.popFirst();
                mMean.popFirst();
                mStd.popFirst();
            }
        }
        sum_0 += 1;
        sum_1 += data;
        sum_2 += (data * data);
        mData.addLast(data);
        mMean.addLast(sum_1 / sum_0);
        if (sum_0 > 1) {
            mStd.addLast((float) Math.sqrt((sum_0 * sum_2 - sum_1 * sum_1) / (sum_0 * (sum_0 - 1))));
        } else {
            mStd.addLast(0.0f);
        }
        mTotalDataCount++;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // update plot canvas size
        int viewHeight = getHeight();
        int viewWidth = getWidth();
        updatePlotCanvas(viewWidth, viewHeight);

        // update data scale
        // start from 1 since no std exists for one datapoint
        mMaxY = Float.MIN_VALUE;
        mMinY = Float.MAX_VALUE;
        for (int i = 1; i < mData.size(); ++i) {
            mMaxY = Math.max(mMaxY, mData.get(i));
            mMaxY = Math.max(mMaxY, mMean.get(i));
            mMaxY = Math.max(mMaxY, mStd.get(i));
            mMinY = Math.min(mMinY, mData.get(i));
            mMinY = Math.min(mMinY, mMean.get(i));
            mMinY = Math.min(mMinY, mStd.get(i));
        }
        mScaleY = Math.abs(mMaxY - mMinY);
        mScaleX = mData.size();
        mMinX = mTotalDataCount - mData.size();
        mMaxX = mTotalDataCount;

        drawAxis(canvas);
        drawGrid(canvas);
        drawLegends(canvas);

        mDataPath.reset();
        mMeanPath.reset();
        mStdPath.reset();
        // draw data
        if (mData.size() > 0) {
            float data_y = getCanvasYForValueY(mData.get(0));
            float mean_y = getCanvasYForValueY(mMean.get(0));
            float std_y = getCanvasYForValueY(mStd.get(0));
            float x = getCanvasXForValueX(0);
            mDataPath.moveTo(x, data_y);
            mMeanPath.moveTo(x, mean_y);
            mStdPath.moveTo(x, std_y);
        }
        for (int i = 1; i < mData.size(); ++i) {
            float data_y = getCanvasYForValueY(mData.get(i));
            float mean_y = getCanvasYForValueY(mMean.get(i));
            float std_y = getCanvasYForValueY(mStd.get(i));
            float x = getCanvasXForValueX(i);// / (float) MAX_DATA_POINT);
            mDataPath.lineTo(x, data_y);
            mMeanPath.lineTo(x, mean_y);
            mStdPath.lineTo(x, std_y);
        }
        canvas.drawPath(mDataPath, mDataPaint);
        canvas.drawPath(mMeanPath, mMeanPaint);
        canvas.drawPath(mStdPath, mStdPaint);

        // draw three curves
        invalidate();
    }
}

