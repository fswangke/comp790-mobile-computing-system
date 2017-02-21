package unc.edu.kewang.sensorplot.sensoractivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import unc.edu.kewang.sensorplot.R;
import unc.edu.kewang.sensorplot.sensorview.ScalarSensor2DPlotView;
import unc.edu.kewang.sensorplot.sensorview.VectorSensor2DPlotView;

public class OrientationSensorDetailActivity extends SensorDetailActivity implements SensorEventListener {
    private ImageView mCompassImageView;
    private float previousRotationDegrees = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_detail_activity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_KEY)) {
            mSensorType = intent.getIntExtra(EXTRA_KEY, Sensor.TYPE_ALL);
        }
        if (mSensorType == Sensor.TYPE_ALL) {
            Log.e("TAG", "ERROR!");
        }
        mSensor = mSensorManager.getDefaultSensor(mSensorType);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);

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

        String title = "Orientation";
        mScalarPlot.setUnits("", "degrees");
        mVectorPlot.setUnits("", "degrees");
        mScalarPlot.setLegends(new String[]{"Orientation", "Mean", "Std"});
        mVectorPlot.setLegends(new String[]{"X", "Y", "Z"});
        setTitle(title);

        mCompassImageView = new ImageView(this);
        mCompassImageView.setImageResource(R.drawable.compass);
        mAnimationCardView.addView(mCompassImageView);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mScalarPlot.addData((float) Math.sqrt(
                event.values[0] * event.values[0]
                        + event.values[1] * event.values[1]
                        + event.values[2] * event.values[2]));
        mVectorPlot.addData(event.values[0], event.values[1], event.values[2]);
        float rotationDegrees = -(float) Math.toDegrees(event.values[0]);
        if (Math.abs(previousRotationDegrees - rotationDegrees) >= 2.0) {
            RotateAnimation animation = new RotateAnimation(previousRotationDegrees, rotationDegrees,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillAfter(true);
            mCompassImageView.startAnimation(animation);
            previousRotationDegrees = rotationDegrees;
        }
    }
}
