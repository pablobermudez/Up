package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private TextView txtX;
    private TextView txtY;
    private TextView txtZ;

    private void initializeViewObjects()
    {
        txtX = (TextView) findViewById(R.id.txtXA);
        txtY = (TextView) findViewById(R.id.txtYA);
        txtZ = (TextView) findViewById(R.id.txtZA);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        initializeViewObjects();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int acc){}

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            txtX.setText("X: " + String.format ("%.1f", event.values[0]) + "m/s^2");
            txtY.setText("Y: " + String.format ("%.1f", event.values[1]) + "m/s^2");
            txtZ.setText("Z: " + String.format ("%.1f", event.values[2]) + "m/s^2");
        }
    }

    @Override
    protected  void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
