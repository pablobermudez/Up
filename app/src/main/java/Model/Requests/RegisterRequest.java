package Model.Requests;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Model.Utils.Format;

public class RegisterRequest extends IntentService {

    private Exception mException = null;

    public RegisterRequest() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //este metodo inicia cuando se llama startService
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String action = extras.getString("action");
        try {

            //recibo los parametros que manda el intent que envio el inicio de este servicio

            String uri = intent.getExtras().getString("uri");
            JSONObject jsonData = new JSONObject(intent.getExtras().getString("jsonData"));

            // para saber el tipo de accion que me pidio el inicio de servicio -> LOGIN, REGISTRO
            excecutePost(uri,jsonData,action);
        }
        catch (Exception e)
        {
            mException = e;
            return;
        }
    }

    protected void excecutePost(String uri, JSONObject jsonData, String action) {

        //llamado al metodo post, contiene el armado del paquete del POST

        String result = POST(uri,jsonData);

        if (result == null) {
            Log.e("Connection Error","Error inesperado al conectar con el servidor");
            return;
        }

        //Intent que manda los datos del response al registerActivity
        Intent intentPostToBroadcast = new Intent(action);
        intentPostToBroadcast.putExtra("jsonData",result);
        sendBroadcast(intentPostToBroadcast);

    }

    private String POST(String uri, JSONObject jsonData) {

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
                result =  Format.convertInputStreamToString(new InputStreamReader((urlConnection.getInputStream())));
            }
            else
            {
                result = "NO_OK";
                Log.e("Server response: ", result + " " + responseCode + " " + urlConnection.getResponseMessage());
                Log.e("Server response: ", Format.convertInputStreamToString(new InputStreamReader((urlConnection.getErrorStream()))));
            }
            urlConnection.disconnect();
            return result;
        } catch (Exception e) {
            return result;
        }
    }

}
