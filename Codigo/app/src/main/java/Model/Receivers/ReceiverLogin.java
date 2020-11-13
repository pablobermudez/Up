package Model.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.up.HomeActivity;
import com.example.up.MainActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Requests.EventRequest;
import Model.Requests.EventRequestHandler;
import Model.ServerResponse;
import Model.Utils.InternetService;
import Model.Utils.StaticEventTypes;
import Model.Utils.StaticServiceData;

public class ReceiverLogin extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String stringJsonData;
        Gson gson = new Gson();
        ServerResponse responseLogin;
        stringJsonData = intent.getStringExtra("jsonData");
        if (stringJsonData.equals("NO_OK")){
            Toast.makeText(context.getApplicationContext(),"Error al ingresar. Vuelva a intentar", Toast.LENGTH_LONG).show();
            return;
        }
        responseLogin = gson.fromJson(stringJsonData, ServerResponse.class);
        if (responseLogin.getSuccess() == true ) {
            Toast.makeText(context.getApplicationContext(),"Login exitoso", Toast.LENGTH_LONG).show();
            StaticServiceData.sessionToken = responseLogin.getToken();
            StaticServiceData.sessionTokenRefresh = responseLogin.getToken_refresh();
            EventRequestHandler.saveEvent(context, StaticEventTypes.EV_LOGIN, StaticEventTypes.EV_LOGIN_DESC_SUCCESS);
            context.startActivity(new Intent(context, HomeActivity.class));
        }
    }
}
