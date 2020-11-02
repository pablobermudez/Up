package Model.Requests;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import Model.Utils.InternetService;
import Model.Utils.StaticEventTypes;
import Model.Utils.StaticServiceData;

public final class EventRequestHandler {

    public static void saveEvent(Context context, String eventType, String eventDesc)
    {
        if(!InternetService.thereIsInternetConnection(context)){
            return;
        }

        try
        {
            JSONObject json = new JSONObject();
            json.put("env", StaticServiceData.ENV);
            json.put("type_events", eventType);
            json.put("description", eventDesc);

            Intent intentEvent = new Intent(context, EventRequest.class);
            intentEvent.putExtra("uri", StaticServiceData.EVENT_URI);
            intentEvent.putExtra("token", StaticServiceData.sessionToken);
            intentEvent.putExtra("jsonData", json.toString());

            context.startService(intentEvent);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

    }

}
