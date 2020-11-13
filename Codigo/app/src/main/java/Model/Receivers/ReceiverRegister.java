package Model.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.up.HomeActivity;
import com.example.up.MainActivity;
import com.google.gson.Gson;

import Model.Requests.EventRequestHandler;
import Model.ServerResponse;
import Model.Utils.StaticEventTypes;
import Model.Utils.StaticServiceData;

public class ReceiverRegister extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String stringJsonData;
        Gson gson = new Gson();
        ServerResponse responseRegister;
        stringJsonData = intent.getStringExtra("jsonData");
        if (stringJsonData.equals("NO_OK")) {
            Toast.makeText(context.getApplicationContext(), "Datos incorrectos. Vuelva a intentar", Toast.LENGTH_LONG).show();
            return;
        }
        responseRegister = gson.fromJson(stringJsonData, ServerResponse.class);
        if (responseRegister.getSuccess() == true) {
            Toast.makeText(context.getApplicationContext(), "Registro exitoso", Toast.LENGTH_LONG).show();
            StaticServiceData.sessionToken = responseRegister.getToken();
            StaticServiceData.sessionTokenRefresh = responseRegister.getToken_refresh();
            EventRequestHandler.saveEvent(context, StaticEventTypes.EV_REGISTER, StaticEventTypes.EV_REGISTER_DESC_SUCCESS);
            context.startActivity(new Intent(context, MainActivity.class));
        }
    }

}
