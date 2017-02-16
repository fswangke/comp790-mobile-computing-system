package unc.edu.kewang.sensorplot;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import unc.edu.kewang.sensorplot.sensoractivity.*;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private Sensor mLightSensor;
    private Sensor mAccSensor;
    private Sensor mGyroSensor;
    private Sensor mMagneticSensor;

    private TextView mLightSensorReading;
    private TextView mOrientationSensorReading;
    private TextView mAccSensorReading;
    private TextView mGyroSensorReading;
    private TextView mMagneticSensorReading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mAccSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Setup orientation sensor reading
        TextView tv = (TextView) findViewById(R.id.tv_orientation_sensor_name_value);
        tv.setText(mOrientationSensor.getName());
        tv = (TextView) findViewById(R.id.tv_orientation_sensor_power_value);
        tv.setText(String.valueOf(mOrientationSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_orientation_sensor_vendor_value);
        tv.setText(mOrientationSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_orientation_sensor_version_value);
        tv.setText(String.valueOf(mOrientationSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_orientation_sensor_resolution_value);
        tv.setText(String.valueOf(mOrientationSensor.getResolution() + " degree"));
        tv = (TextView) findViewById(R.id.tv_orientation_sensor_range_value);
        tv.setText(String.valueOf(mOrientationSensor.getMaximumRange() + " degree"));

        // Setup light sensor reading
        tv = (TextView) findViewById(R.id.tv_light_sensor_name_value);
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

        // setup acc sensor reading
        tv = (TextView) findViewById(R.id.tv_acc_sensor_name_value);
        tv.setText(mAccSensor.getName());
        tv = (TextView) findViewById(R.id.tv_acc_sensor_power_value);
        tv.setText(String.valueOf(mAccSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_acc_sensor_vendor_value);
        tv.setText(mAccSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_acc_sensor_version_value);
        tv.setText(String.valueOf(mAccSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_acc_sensor_resolution_value);
        tv.setText(String.valueOf(mAccSensor.getResolution() + " m/s2"));
        tv = (TextView) findViewById(R.id.tv_acc_sensor_range_value);
        tv.setText(String.valueOf(mAccSensor.getMaximumRange() + " m/s2"));

        // setup gyro sensor reading
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_name_value);
        tv.setText(mGyroSensor.getName());
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_power_value);
        tv.setText(String.valueOf(mGyroSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_vendor_value);
        tv.setText(mGyroSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_version_value);
        tv.setText(String.valueOf(mGyroSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_resolution_value);
        tv.setText(String.valueOf(mGyroSensor.getResolution() + " rad/s"));
        tv = (TextView) findViewById(R.id.tv_gyro_sensor_range_value);
        tv.setText(String.valueOf(mGyroSensor.getMaximumRange() + " rad/s"));

        // setup magnetic sensor reading
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_name_value);
        tv.setText(mMagneticSensor.getName());
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_power_value);
        tv.setText(String.valueOf(mMagneticSensor.getPower()));
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_vendor_value);
        tv.setText(mMagneticSensor.getVendor());
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_version_value);
        tv.setText(String.valueOf(mMagneticSensor.getVersion()));
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_resolution_value);
        tv.setText(String.valueOf(mMagneticSensor.getResolution() + " uT"));
        tv = (TextView) findViewById(R.id.tv_magnetic_sensor_range_value);
        tv.setText(String.valueOf(mMagneticSensor.getMaximumRange() + " uT"));

        mOrientationSensorReading = (TextView) findViewById(R.id.tv_orientation_sensor_reading_value);
        mLightSensorReading = (TextView) findViewById(R.id.tv_light_sensor_reading_value);
        mAccSensorReading = (TextView) findViewById(R.id.tv_acc_sensor_reading_value);
        mGyroSensorReading = (TextView) findViewById(R.id.tv_gyro_sensor_reading_value);
        mMagneticSensorReading = (TextView) findViewById(R.id.tv_magnetic_sensor_reading_value);
    }

    public void onClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id) {
            case R.id.acc_summary_card:
                intent = new Intent(this, AccelerometerSensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_ACCELEROMETER);
                startActivity(intent);
                break;
            case R.id.gyro_summary_card:
                intent = new Intent(this, SensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_GYROSCOPE);
                startActivity(intent);
                break;
            case R.id.light_summary_card:
                intent = new Intent(this, LightSensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_LIGHT);
                startActivity(intent);
                break;
            case R.id.magnetic_summary_card:
                intent = new Intent(this, SensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_MAGNETIC_FIELD);
                startActivity(intent);
                break;
            case R.id.orientation_summary_card:
                intent = new Intent(this, OrientationSensorDetailActivity.class);
                intent.putExtra(SensorDetailActivity.EXTRA_KEY, Sensor.TYPE_ORIENTATION);
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
        mSensorManager.registerListener(this, mAccSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mOrientationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                String lightSensorReadingString = String.format("%.2f", event.values[0]) + " lx";
                mLightSensorReading.setText(lightSensorReadingString);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                String accSensorReadingString = String.format("%.2f", event.values[0]) + ", "
                        + String.format("%.2f", event.values[1]) + ", "
                        + String.format("%.2f", event.values[2]) + " m/s2";
                mAccSensorReading.setText(accSensorReadingString);
                break;
            case Sensor.TYPE_GYROSCOPE:
                String gyroSensorReadingString = String.format("%.2f", event.values[0]) + ", "
                        + String.format("%.2f", event.values[1]) + ", "
                        + String.format("%.2f", event.values[2]) + " rad/s";
                mGyroSensorReading.setText(gyroSensorReadingString);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                String magneticSensorReadingString = String.format("%.2f", event.values[0]) + ", "
                        + String.format("%.2f", event.values[1]) + ", "
                        + String.format("%.2f", event.values[2]) + " uT";
                mMagneticSensorReading.setText(magneticSensorReadingString);
            case Sensor.TYPE_ORIENTATION:
                String orientationSensorReadingString = String.format("%.2f", event.values[0]) + ", "
                        + String.format("%.2f", event.values[1]) + ", "
                        + String.format("%.2f", event.values[2]) + " degree";
                mOrientationSensorReading.setText(orientationSensorReadingString);
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
