package unc.edu.kewang.sensorplot.sensorview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


class Sensor2DPlotView extends View {
    protected final static int MAX_DATA_POINT = 150;
    protected int mPlotWidth = 0;
    protected int mPlotHeight = 0;
    protected int mPlotHorizontalOffset = 0;
    protected int mPlotVerticalOffset = 0;
    protected int mLegendSize = 0;
    protected int mLegendLabelSize = 0;
    protected int mLegendRadius = 3;
    protected int mLegendLeftPosition = 0;
    protected int mLegendTopPosition = 0;
    protected float mScaleX = 0.0f;
    protected float mScaleY = 0.0f;
    protected float mMarginRatio = 0.1f;
    protected float mMaxY = 0.0f;
    protected float mMinY = 0.0f;
    protected float mMinX = 0.0f;
    protected float mMaxX = 0.0f;
    protected static final float LINE_WIDTH = 4.5f;
    protected static final float TEXT_FONT_SIZE = 35.0f;
    protected Paint mGridDashedLinePaint;
    protected Paint mAxisTextPaint;
    protected Paint mLegendTextPaint;
    protected Paint mLegendPaint;
    protected Paint mLegendLabelPaint;
    protected Path mGridPath;
    protected static final int GRID_NUMBER = 4;
    protected String mXUnit;
    protected String mYUnit;
    protected String[] mLegends;
    protected int mTotalDataCount = 0;

    public void setXUint(String xUnit) {
        this.mXUnit = xUnit;
    }

    public void setYUint(String yUnit) {
        this.mYUnit = yUnit;
    }

    public void setUnits(String xUnit, String yUnit) {
        this.mXUnit = xUnit;
        this.mYUnit = yUnit;
    }

    public void setLegends(String[] labels) {
        mLegends = labels;
    }

    protected float getCanvasXForValueX(final float valueX) {
        return (valueX) / mScaleX * mPlotWidth + mPlotHorizontalOffset;
    }

    protected float getCanvasYForValueY(final float valueY) {
        return (mMaxY - valueY) / mScaleY * mPlotHeight + mPlotVerticalOffset;
    }

    protected void drawLegends(Canvas canvas) {
        final int PADDING = 5;
        mLegendLabelPaint.setColor(Color.RED);
        canvas.drawRect(
                mLegendLeftPosition + PADDING,
                mLegendTopPosition + PADDING,
                mLegendLeftPosition + PADDING + mLegendLabelSize,
                mLegendTopPosition + PADDING + mLegendLabelSize,
                mLegendLabelPaint);
        canvas.drawText(
                mLegends[0],
                mLegendLeftPosition + PADDING + mLegendLabelSize + PADDING,
                mLegendTopPosition + PADDING + mLegendLabelSize,
                mLegendTextPaint);

        mLegendLabelPaint.setColor(Color.GREEN);
        canvas.drawRect(
                mLegendLeftPosition + PADDING,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING,
                mLegendLeftPosition + PADDING + mLegendLabelSize,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize,
                mLegendLabelPaint);
        canvas.drawText(
                mLegends[1],
                mLegendLeftPosition + PADDING + mLegendLabelSize + PADDING,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize,
                mLegendTextPaint);

        mLegendLabelPaint.setColor(Color.BLUE);
        canvas.drawRect(
                mLegendLeftPosition + PADDING,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize + PADDING,
                mLegendLeftPosition + PADDING + mLegendLabelSize,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize,
                mLegendLabelPaint);
        canvas.drawText(
                mLegends[2],
                mLegendLeftPosition + PADDING + mLegendLabelSize + PADDING,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize,
                mLegendTextPaint);

        canvas.drawRoundRect(
                mLegendLeftPosition,
                mLegendTopPosition,
                mLegendLeftPosition + mLegendSize,
                mLegendTopPosition + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize + PADDING + mLegendLabelSize + PADDING,
                mLegendRadius,
                mLegendRadius,
                mLegendPaint);
    }

    protected void drawGrid(Canvas canvas) {
        for (int row_index = 0; row_index <= GRID_NUMBER; ++row_index) {
            int y = row_index * mPlotHeight / GRID_NUMBER + mPlotVerticalOffset;
            mGridPath.moveTo(mPlotHorizontalOffset, y);
            mGridPath.lineTo(mPlotHorizontalOffset + mPlotWidth, y);
        }

        for (int col_index = 0; col_index <= GRID_NUMBER; ++col_index) {
            int x = col_index * mPlotWidth / GRID_NUMBER + mPlotHorizontalOffset;
            mGridPath.moveTo(x, mPlotVerticalOffset);
            mGridPath.lineTo(x, mPlotVerticalOffset + mPlotHeight);
        }
        canvas.drawPath(mGridPath, mGridDashedLinePaint);
    }

    protected void drawAxis(Canvas canvas) {
        for (int row_index = 0; row_index <= GRID_NUMBER; ++row_index) {
            if (row_index % 2 == 0) {
                float axisValue = mMinY + mScaleY * row_index / GRID_NUMBER;
                canvas.drawText(
                        String.format("%.2f", axisValue),
                        mPlotHorizontalOffset / 5,
                        getCanvasYForValueY(axisValue),
                        mAxisTextPaint);
            }
        }
        for (int col_index = 0; col_index <= GRID_NUMBER; ++col_index) {
            if (col_index % 2 == 0) {
                float axisValue = mMinX + mScaleX * col_index / GRID_NUMBER;
                canvas.drawText(
                        String.format("%d", (int) axisValue),
                        getCanvasXForValueX(axisValue - mMinX),
                        (int) (mPlotHeight + mPlotVerticalOffset * 1.5),
                        mAxisTextPaint);
            }
        }
        canvas.drawText(mXUnit, getCanvasXForValueX(mMaxX + mScaleX * 0.05f), (int) (mPlotHeight + mPlotVerticalOffset * 1.5), mAxisTextPaint);
        canvas.drawText(mYUnit, mPlotHorizontalOffset / 5, getCanvasYForValueY(mMaxY + mScaleY * 0.05f), mAxisTextPaint);
    }

    protected void updatePlotCanvas(final int viewWidth, final int viewHeight) {
        this.mPlotHeight = viewHeight - (int) (viewHeight * this.mMarginRatio * 2 + 0.5);
        this.mPlotWidth = viewWidth - (int) (viewWidth * this.mMarginRatio * 2 + 0.5);
        this.mPlotHorizontalOffset = (int) (viewWidth * this.mMarginRatio + 0.5);
        this.mPlotVerticalOffset = (int) (viewHeight * this.mMarginRatio + 0.5);
        this.mLegendSize = this.mPlotWidth / 5;
        this.mLegendLabelSize = this.mLegendSize / 4;
        this.mLegendLeftPosition = viewWidth - this.mPlotHorizontalOffset - mLegendSize - 10;
        this.mLegendTopPosition = this.mPlotVerticalOffset + 10;
        this.mLegendTextPaint.setTextSize(mLegendLabelSize);
    }

    private void setup() {
        mGridDashedLinePaint = new Paint();
        mGridDashedLinePaint.setARGB(128, 0, 0, 0);
        mGridDashedLinePaint.setStyle(Paint.Style.STROKE);
        mGridDashedLinePaint.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));
        mGridPath = new Path();
        mAxisTextPaint = new Paint();
        mAxisTextPaint.setStyle(Paint.Style.FILL);
        mAxisTextPaint.setTextSize(TEXT_FONT_SIZE);
        mAxisTextPaint.setColor(Color.BLACK);
        mLegendTextPaint = new Paint();
        mLegendTextPaint.setStyle(Paint.Style.FILL);
        mLegendTextPaint.setTextSize(TEXT_FONT_SIZE);
        mLegendTextPaint.setColor(Color.BLACK);
        mLegendLabelPaint = new Paint();
        mLegendLabelPaint.setStyle(Paint.Style.FILL);
        mLegendPaint = new Paint();
        mLegendPaint.setStyle(Paint.Style.FILL);
        mLegendPaint.setARGB(100, 0, 0, 0);
        mLegends = new String[3];
    }

    public Sensor2DPlotView(Context context) {
        super(context);
        setup();
    }

    public Sensor2DPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public Sensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public Sensor2DPlotView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }
}

