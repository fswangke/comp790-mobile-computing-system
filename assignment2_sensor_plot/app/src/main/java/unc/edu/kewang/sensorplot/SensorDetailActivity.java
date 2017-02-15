package unc.edu.kewang.sensorplot;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SensorDetailActivity extends AppCompatActivity implements SensorEventListener {
    public static final String EXTRA_KEY = "SENSOR_TYPE";
    private SensorManager mSensorManager;
    private Sensor mSensor = null;
    private int mSensorType = Sensor.TYPE_ALL;
    private SensorPlotView mSensorPlotView;

    private TextView tvtv;

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
        if (mSensorType == Sensor.TYPE_ALL) {
            Log.e("TAG", "ERROR!");
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

        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.sensor_detail_root_linear_layout);
        LayoutInflater layoutInflater = getLayoutInflater();
        CardView cardView = (CardView) layoutInflater.inflate(R.layout.empty_cardview, null);
        final float DP = getResources().getDisplayMetrics().density;
        final float FLOAT_DP_4 = DP * 4;
        final int INT_DP_4 = (int) (FLOAT_DP_4 + 0.5);
        final int INT_DP_8 = (int) (DP * 8 + 0.5);
        cardView.setRadius(FLOAT_DP_4);
        cardView.setPadding(INT_DP_8, INT_DP_8, INT_DP_8, INT_DP_8);
        cardView.setContentPadding(INT_DP_4, INT_DP_4, INT_DP_4, INT_DP_4);
        mSensorPlotView = new SensorPlotView(this);
        cardView.addView(mSensorPlotView);
        tvtv = new TextView(this);
        cardView.addView(tvtv);
        rootLinearLayout.addView(cardView);

        ActionBar actionBar = getActionBar();
        String title = "SensorPlot";
        switch (mSensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                title = "Accelerometer";
                break;
            case Sensor.TYPE_GYROSCOPE:
                title = "Gyroscope";
                break;
            case Sensor.TYPE_LIGHT:
                title = "Light";
                break;
            default:
                title = "SensorPlot";
                break;
        }
        if (actionBar != null) {
        }
        setTitle(title);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tvtv.setText(String.valueOf(event.values[0]));
        if (mSensorType == Sensor.TYPE_ACCELEROMETER) {
            mSensorPlotView.addData((float) Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]));
        } else {
            mSensorPlotView.addData(event.values[0]);
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
