package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private TextView txtBattery;
    private Button btnGoToAccelerometer;
    private Button btnGoToRotationVectorSensor;

    private void initializeViewObjects()
    {
        txtBattery = (TextView) findViewById(R.id.txtBattery);
        btnGoToAccelerometer = (Button) findViewById(R.id.btnGoToAccelerometer);
        btnGoToRotationVectorSensor = (Button) findViewById(R.id.btnGoToRotationVectorSensor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViewObjects();
        this.registerReceiver(this.getBatteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(getBatteryLevelReceiver);
    }

    private BroadcastReceiver getBatteryLevelReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            txtBattery.setText("Nivel de Batería actual: " + level + "%");
        }
    };

    public void GoToAccelerometerActivity(View v)
    {
        Intent intent = new Intent(this, AccelerometerActivity.class);
        startActivity(intent);
    }

    public void GoRotationVectorActivity(View v)
    {
        Intent intent = new Intent(this, RotationVectorActivity.class);
        startActivity(intent);
    }

    public void GoToAlarmList(View v)
    {
        Intent intent = new Intent(this, SetEmailActivity.class);
        startActivity(intent);
    }

}
