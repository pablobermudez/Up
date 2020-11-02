package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RotationVectorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor rotationSensor;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    private TextView txtX;
    private TextView txtY;
    private TextView txtZ;
    private Button btnSaveRegister;
    private ListView list;
    private Button btnClear;

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int itemCount = 0;

    private void initializeViewObjects()
    {
        txtX = (TextView) findViewById(R.id.txtXV);
        txtY = (TextView) findViewById(R.id.txtYV);
        txtZ = (TextView) findViewById(R.id.txtZV);
        btnSaveRegister = (Button) findViewById(R.id.btnSaveRegister);
        list = (ListView) findViewById(R.id.list);
        btnClear = (Button) findViewById(R.id.btnClearVectorTable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_vector);

        initializeViewObjects();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if(rotationSensor == null)
            Toast.makeText(RotationVectorActivity.this, "El sensor no se encuentra activo en su teléfono.", Toast.LENGTH_LONG).show();

        //Init list view
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listItems);
        list.setAdapter(adapter);

        //Init sharedPreferences
        sharedPref = RotationVectorActivity.this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        fillTable();
        //SharedPreferences.Editor.clear();
    }

    private void fillTable()
    {
        String x = "";
        String y = "";
        String z = "";
        int count = sharedPref.getInt("itemCount", 0);
        itemCount = count;
        for (int i = 1; i <= count; i ++)
        {
            x = sharedPref.getString("x" + i, "");
            y = sharedPref.getString("y" + i, "");
            z = sharedPref.getString("z" + i, "");
            //Add to list
            listItems.add(x + " " + y + " " + z);
            //Send to adaptarter to show the item in list
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int acc){}

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            txtX.setText("X: " + String.format ("%.1f", event.values[0]));
            txtY.setText("Y: " + String.format ("%.1f", event.values[1]));
            txtZ.setText("Z: " + String.format ("%.1f", event.values[2]));
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    public void saveRegister(View v)
    {
        String currentX = txtX.getText().toString();
        String currentY = txtY.getText().toString();
        String currentZ = txtZ.getText().toString();

        itemCount++;
        //Save in preferences
        editor.putInt("itemCount", itemCount);
        editor.putString("x" + itemCount, currentX);
        editor.putString("y" + itemCount, currentY);
        editor.putString("z" + itemCount, currentZ);
        editor.apply();
        //Add to list
        listItems.add(currentX + " " + currentY + " " + currentZ);
        //Send to adaptarter to show the item in list
        adapter.notifyDataSetChanged();
    }

    public void clearTable(View v)
    {
        itemCount = 0;
        editor.clear();
        editor.apply();
        listItems.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(RotationVectorActivity.this, "Datos borrados.", Toast.LENGTH_LONG).show();
    }

}
