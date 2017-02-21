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

public class LightSensorDetailActivity extends SensorDetailActivity implements SensorEventListener {
    private static final float LIGHT_THRESHOLD = 50;
    private float previousLightReading = 0;
    private ImageView mBulbImageView;

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

        mDisplayStatistics = true;
        mSensorPlotCardView.addView(mScalarPlot);

        String title = "Light";
        mScalarPlot.setUnits("", "lx");
        mScalarPlot.setLegends(new String[]{"Lux", "Mean", "Std"});
        setTitle(title);

        mBulbImageView = new ImageView(this);
        mBulbImageView.setImageResource(R.drawable.dark_bulb);
        mAnimationCardView.addView(mBulbImageView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        mScalarPlot.addData(event.values[0]);

        Drawable backgrounds[] = new Drawable[2];
        if (previousLightReading < LIGHT_THRESHOLD && event.values[0] >= LIGHT_THRESHOLD) {
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.dark_bulb);
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.light_bulb);
            TransitionDrawable animation = new TransitionDrawable(backgrounds);
            mBulbImageView.setImageDrawable(animation);

            animation.startTransition(100);
        } else if (previousLightReading >= LIGHT_THRESHOLD && event.values[0] < LIGHT_THRESHOLD) {
            backgrounds[1] = ContextCompat.getDrawable(this, R.drawable.dark_bulb);
            backgrounds[0] = ContextCompat.getDrawable(this, R.drawable.light_bulb);
            TransitionDrawable animation = new TransitionDrawable(backgrounds);
            mBulbImageView.setImageDrawable(animation);
            animation.startTransition(100);
        }
        previousLightReading = event.values[0];
    }
}
