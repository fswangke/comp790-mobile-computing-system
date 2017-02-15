package unc.edu.kewang.sensorplot;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLightSensor;

    private TextView mLightSensorReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        TextView tv = (TextView) findViewById(R.id.tv_light_sensor_name_value);
        tv.setText(mLightSensor.getName());
        tv = (TextView) findViewById(R.id.tv_light_sensor_power_value);
        tv.setText(String.valueOf(mLightSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_light_sensor_vendor_value);
        tv.setText(mLightSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_light_sensor_version_value);
        tv.setText(String.valueOf(mLightSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_light_sensor_resolution_value);
        tv.setText(String.valueOf(mLightSensor.getResolution() + " lx"));
        tv = (TextView) findViewById(R.id.tv_light_sensor_range_value);
        tv.setText(String.valueOf(mLightSensor.getMaximumRange() + " lx"));
        mLightSensorReading = (TextView) findViewById(R.id.tv_light_sensor_reading_value);
    }

    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
//            case R.id.cardview_light:
//                intent = new Intent(this, SensorDetailActivity.class);
//                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_ACCELEROMETER);
//                startActivity(intent);
//                break;
//            case R.id.gyro:
//                intent = new Intent(this, SensorDetailActivity.class);
//                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_GYROSCOPE);
//                startActivity(intent);
//                break;
            case R.id.cardview_light:
                intent = new Intent(this, SensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_LIGHT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                String sensorReadingString = String.valueOf(event.values[0]) + " lx";
                mLightSensorReading.setText(sensorReadingString);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
