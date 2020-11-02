package Model.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;

import Model.ServerResponse;

public class ReceiverEvent extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String stringJsonData = null;
        Gson gson = new Gson();
        ServerResponse responseLogin;
        stringJsonData = intent.getStringExtra("jsonData");
        responseLogin = gson.fromJson(stringJsonData, ServerResponse.class);
        if (responseLogin.getSuccess() == true ) {
            if ("Background process".equals(responseLogin.getEventType())){
                Toast.makeText(context.getApplicationContext(),"Evento del proceso del background registrado.", Toast.LENGTH_LONG).show();
            } else  {
                Toast.makeText(context.getApplicationContext(),"Evento registrado de forma exitosa", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(context.getApplicationContext(),"Problema para registrar el evento", Toast.LENGTH_LONG).show();
        }
    }
}
