package unc.edu.kewang.sensorplot.sensorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.util.CircularArray;

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

    public VectorSensor2DPlotView(Context context) {
        super(context);
        mX = new CircularArray<Float>(MAX_DATA_POINT);
        mY = new CircularArray<Float>(MAX_DATA_POINT);
        mZ = new CircularArray<Float>(MAX_DATA_POINT);
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
    }

    public void addData(final float x, final float y, final float z) {
        if (mX.size() >= MAX_DATA_POINT) {
            int exceedSize = mX.size() - MAX_DATA_POINT;
            for (int i = 0; i < exceedSize; ++i) {
                mX.popFirst();
                mY.popFirst();
                mZ.popFirst();
            }
        }
        mX.addLast(x);
        mY.addLast(y);
        mZ.addLast(z);
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
        mMaxY = Float.MIN_VALUE;
        mMinY = Float.MAX_VALUE;
        for (int i = 0; i < mX.size(); ++i) {
            mMaxY = Math.max(mMaxY, mX.get(i));
            mMaxY = Math.max(mMaxY, mY.get(i));
            mMaxY = Math.max(mMaxY, mZ.get(i));
            mMinY = Math.min(mMinY, mX.get(i));
            mMinY = Math.min(mMinY, mY.get(i));
            mMinY = Math.min(mMinY, mZ.get(i));
        }
        mScaleY = Math.abs(mMaxY - mMinY);
        mScaleX = mX.size();
        mMinX = mTotalDataCount - mX.size();
        mMaxX = mTotalDataCount;

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
            float x = getCanvasXForValueX(0);
            mXPath.moveTo(x, data_x);
            mYPath.moveTo(x, data_y);
            mZPath.moveTo(x, data_z);
        }
        for (int i = 1; i < mX.size(); ++i) {
            float data_x = getCanvasYForValueY(mX.get(i));
            float data_y = getCanvasYForValueY(mY.get(i));
            float data_z = getCanvasYForValueY(mZ.get(i));
            float x = getCanvasXForValueX(i);
            mXPath.lineTo(x, data_x);
            mYPath.lineTo(x, data_y);
            mZPath.lineTo(x, data_z);
        }
        canvas.drawPath(mXPath, mXPaint);
        canvas.drawPath(mYPath, mYPaint);
        canvas.drawPath(mZPath, mZPaint);
        invalidate();
    }
}

