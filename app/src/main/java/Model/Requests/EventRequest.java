package Model.Requests;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.Utils.Format;

public class EventRequest extends IntentService {

    private Exception mException;


    public EventRequest() {
        super("ServicesHttpGET");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SERVICE","Service OnCreate()");
    }

    //este metodo inicia cuando se llama startService
    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        String action = extras.getString("action");
        String token = extras.getString("token");
        String uri = intent.getExtras().getString("uri");

        try
        {
            JSONObject jsonData = new JSONObject(intent.getExtras().getString("jsonData"));
            excecutePost(uri,jsonData,action,token);
        }
        catch (Exception e)
        {
            Log.e("Server response: ", e.toString());
            mException = e;
            return;
        }

    }

    protected void excecutePost(String uri, JSONObject jsonData,  String action,String token) throws JSONException {

        //llamado al metodo post, contiene el armado del paquete del POST

        String result = POST(uri,jsonData,token);

        if (result == null)
        {
            Log.e("Register Event", "Error en get: \n" + mException.toString());
            return;
        }
        if (result == "NO_OK"){
            Log.e("Register Event", "Response NO_OK");
            return;
        }

        //Intent que manda los datos del response al menuActivity
        //Intent intentPostToBroadcast = new Intent(action);
        // Intent intentPostToBroadcast = new Intent(action);
        //intentPostToBroadcast.putExtra("jsonData",result);
        //Log.i("Json data", result);
        //sendBroadcast(intentPostToBroadcast);

    }

    private String POST(String uri, JSONObject jsonData,String token) throws JSONException {

        String result = null;
        URL url;
        HttpURLConnection urlConnection = null;
        int responseCode;
        Exception exceptionPOST = null;

        try {

            //se pasa la uri que viene por parametro hacia el metodo

            url = new URL(uri);

            //configuracion de la conexion

            urlConnection = (HttpURLConnection) url.openConnection();

            //configuro el header del json
            urlConnection.setRequestProperty("Content-type", "application/json; charset = UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
            // tipo de request

            // aca creo el flujo de datos para el body del json

            DataOutputStream dataOutputStreamPost = new DataOutputStream(urlConnection.getOutputStream());
            dataOutputStreamPost.write((jsonData.toString().getBytes("UTF-8")));
            dataOutputStreamPost.flush();
            dataOutputStreamPost.close();

            //aca envio el paquetito entero
            urlConnection.connect();

            //guardo el codigo http para ver que paso
            //se queda bloqueado el hilo hasta obtener respuesta

            responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                result = Format.convertInputStreamToString(new InputStreamReader((urlConnection.getInputStream())));
                Log.e("Server response: ", result + " " + responseCode + " " + urlConnection.getResponseMessage());
            }
            else {
                result = "NO_OK";
                Log.e("Server response: ", result + " " + responseCode + " " + urlConnection.getResponseMessage());
                Log.e("Server response: ", Format.convertInputStreamToString(new InputStreamReader((urlConnection.getErrorStream()))));
            }

            urlConnection.disconnect();
            return result;

        } catch (Exception e) {
            Log.e("Server response: ", e.toString());
            return result;
        }
    }





}
