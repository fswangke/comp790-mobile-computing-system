package unc.edu.kewang.sensorplot.sensoractivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

import unc.edu.kewang.sensorplot.R;
import unc.edu.kewang.sensorplot.sensorview.ScalarSensor2DPlotView;
import unc.edu.kewang.sensorplot.sensorview.VectorSensor2DPlotView;

public class AccelerometerSensorDetailActivity extends SensorDetailActivity implements SensorEventListener {
    private static final float SLOW_THRESHOLD = 15;
    private static final float FAST_THRESHOLD = 25;
    private float previousAccelerationReading = 0;
    private ImageView mSpeedImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_detail_activity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_KEY)) {
            mSensorType = intent.getIntExtra(EXTRA_KEY, Sensor.TYPE_ALL);
        }
        mSensor = mSensorManager.getDefaultSensor(mSensorType);

        mAnimationCardView = (CardView) findViewById(R.id.animation_card);
        mSensorPlotCardView = (CardView) findViewById(R.id.scalar_plot_card);

        TextView tv = (TextView) findViewById(R.id.tv_sensor_name_value);
        tv.setText(mSensor.getName());
        tv = (TextView) findViewById(R.id.tv_sensor_power_value);
        tv.setText(String.valueOf(mSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_sensor_vendor_value);
        tv.setText(mSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_sensor_version_value);
        tv.setText(String.valueOf(mSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_sensor_resolution_value);
        tv.setText(String.valueOf(mSensor.getResolution()));
        tv = (TextView) findViewById(R.id.tv_sensor_range_value);
        tv.setText(String.valueOf(mSensor.getMaximumRange()));

        mScalarPlot = new ScalarSensor2DPlotView(this);
        mVectorPlot = new VectorSensor2DPlotView(this);

        mDisplayStatistics = true;
        mSensorPlotCardView.addView(mScalarPlot);

        String title = "Accelerometer";
        if (mSensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            title = "Linear Accelerometer";
        }

        mScalarPlot.setUnits("", "m/s2");
        mScalarPlot.setLegends(new String[]{"Acc", "Mean", "Std"});
        mVectorPlot.setUnits("", "m/s2");
        mVectorPlot.setLegends(new String[]{"X", "Y", "Z"});
        setTitle(title);

        mSpeedImageView = new ImageView(this);
        mSpeedImageView.setImageResource(R.drawable.slow);
        mAnimationCardView.addView(mSpeedImageView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentAcceleration = (float) Math.sqrt(
                event.values[0] * event.values[0]
                        + event.values[1] * event.values[1]
                        + event.values[2] * event.values[2]);
        mScalarPlot.addData(currentAcceleration);
        mVectorPlot.addData(event.values[0], event.values[1], event.values[2]);

        Drawable backgrounds[] = new Drawable[2];

        if (previousAccelerationReading < SLOW_THRESHOLD
                && currentAcceleration >= SLOW_THRESHOLD
                && currentAcceleration < FAST_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.slow);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.medium);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }
        if (previousAccelerationReading < SLOW_THRESHOLD
                && currentAcceleration >= FAST_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.slow);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.fast);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }
        if (previousAccelerationReading >= SLOW_THRESHOLD
                && previousAccelerationReading < FAST_THRESHOLD
                && currentAcceleration >= FAST_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.medium);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.fast);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }
        if (previousAccelerationReading >= FAST_THRESHOLD
                && currentAcceleration >= SLOW_THRESHOLD
                && currentAcceleration < FAST_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.fast);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.medium);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }
        if (previousAccelerationReading >= FAST_THRESHOLD
                && currentAcceleration < SLOW_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.fast);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.slow);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }
        if (previousAccelerationReading >= SLOW_THRESHOLD
                && previousAccelerationReading < FAST_THRESHOLD
                && currentAcceleration < SLOW_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.medium);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.slow);
            TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
            mSpeedImageView.setImageDrawable(crossfader);
            crossfader.startTransition(100);
        }

        previousAccelerationReading = currentAcceleration;
    }
}
