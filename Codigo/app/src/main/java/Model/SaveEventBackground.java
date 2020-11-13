package Model;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import Model.Receivers.ReceiverEvent;
import Model.Requests.EventRequest;
import Model.Utils.StaticServiceData;

public class SaveEventBackground extends Service {

    private static final Integer interval = 60000; //1min
    private JSONObject jsonObject;
    private ReceiverEvent receiver = new ReceiverEvent();
    private Timer timer = new Timer();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        execute();
        return 1;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        timer.cancel();
    }

    private void execute() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("env", "TEST");
                    jsonObject.put("type_events", "Background process");
                    jsonObject.put("state", "ACTIVO");
                    jsonObject.put("description", "Proceso corriendo en el background");
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }

                Intent intentEvent = new Intent(SaveEventBackground.this, EventRequest.class);
                intentEvent.putExtra("uri", StaticServiceData.EVENT_URI);
                intentEvent.putExtra("token", StaticServiceData.sessionToken);
                intentEvent.putExtra("jsonData", jsonObject.toString());
                intentEvent.putExtra("action", StaticServiceData.EVENT_ACTION);

                configureBroadCastReceiver();

                startService(intentEvent);


                Log.i("Thread", "Ocurrí");
            }
        };
        timer.scheduleAtFixedRate(task, 0, interval);
    }

    private void configureBroadCastReceiver() {
        IntentFilter filterRegister = new IntentFilter(StaticServiceData.EVENT_ACTION);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filterRegister);
    }

}
