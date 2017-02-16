package unc.edu.kewang.sensorplot.sensoractivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import unc.edu.kewang.sensorplot.MainActivity;
import unc.edu.kewang.sensorplot.R;
import unc.edu.kewang.sensorplot.sensorview.*;

public class SensorDetailActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_KEY = "SENSOR_TYPE";
    protected SensorManager mSensorManager;
    protected Sensor mSensor = null;
    protected int mSensorType = Sensor.TYPE_ALL;
    protected ScalarSensor2DPlotView mScalarPlot;
    protected VectorSensor2DPlotView mVectorPlot;
    protected boolean mDisplayStatistics;
    protected CardView mAnimationCardView;
    protected CardView mSensorPlotCardView;

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onSwitchPlot(View view) {
        if (mSensorType == Sensor.TYPE_LIGHT) {
            return;
        }
        if (mDisplayStatistics) {
            mDisplayStatistics = false;
            mSensorPlotCardView.removeView(mScalarPlot);
            mSensorPlotCardView.addView(mVectorPlot);
        } else {
            mDisplayStatistics = true;
            mSensorPlotCardView.addView(mScalarPlot);
            mSensorPlotCardView.removeView(mVectorPlot);
        }
    }

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

        mAnimationCardView = (CardView) findViewById(R.id.animation_card);
        mSensorPlotCardView = (CardView) findViewById(R.id.scalar_plot_card);

        // Animated views have derived classes
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.sensor_detail_root_linear_layout);
        linearLayout.removeView(mAnimationCardView);

        mDisplayStatistics = true;
        mSensorPlotCardView.addView(mScalarPlot);

        String title = "SensorPlot";
        switch (mSensorType) {
            case Sensor.TYPE_GYROSCOPE:
                title = "Gyroscope";
                mScalarPlot.setUnits("", "rad/s");
                mVectorPlot.setUnits("", "rad/s");
                mScalarPlot.setLegends(new String[]{"Rotation", "Mean", "Std"});
                mVectorPlot.setLegends(new String[]{"X", "Y", "Z"});
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                title = "Magnetic Field";
                mScalarPlot.setUnits("", "uT");
                mVectorPlot.setUnits("", "uT");
                mScalarPlot.setLegends(new String[]{"Magnetic", "Mean", "Std"});
                mVectorPlot.setLegends(new String[]{"X", "Y", "Z"});
                break;
            default:
                title = "SensorPlot";
                break;
        }
        setTitle(title);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (mSensorType) {
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_MAGNETIC_FIELD:
                mScalarPlot.addData((float) Math.sqrt(
                        event.values[0] * event.values[0]
                                + event.values[1] * event.values[1]
                                + event.values[2] * event.values[2]));
                mVectorPlot.addData(event.values[0], event.values[1], event.values[2]);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back) {

            Intent intent = new Intent(SensorDetailActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
