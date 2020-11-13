package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Model.Receivers.ReceiverLogin;
import Model.Requests.RegisterRequest;
import Model.Utils.InternetService;
import Model.Utils.StaticServiceData;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static String TAG = MainActivity.class.getName();

    private Button btnSingIn;
    private Button btnSingUp;

    @NotEmpty(message = "Requerido")
    @Email(message =  "Email inválido")
    private EditText txtEmail;
    @NotEmpty(message = "Requerido")
    @Length(min = 8, message = "La contraseña debe tener al menos 8 digitos")
    @Password(message = "Contraseña inválida")
    private EditText txtPassword;

    private Validator validator;
    private ReceiverLogin receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViewObjects();
        validator = new Validator(this);
        validator.setValidationListener(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(token -> {
            Log.e(TAG, token.getToken());
        });

    }

    /**
     * Inicializa los objetos de la vista.
     */
    private void initializeViewObjects() {

        btnSingIn = (Button) findViewById(R.id.btnSingUp);
        btnSingUp = (Button) findViewById(R.id.btnSingIn);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
    }

    /**
     * Evento onclick del boton "Registrarse", se inicia la Activity SignUpActivity.
     */
    public void goSingUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Acciones frente al evento onclick del boton "Ingresar".
     * Se valida el formulario y se ejecuta la funcion onValidationSucceeded en caso de exito
     * o se ejecuta la funcion onValidationFailed en caso de que exista algun error en el formulario.
     */
    public void signIn(View view)
    {
        validator.validate();
    }

    /**
     * Acciones frente a un ingreso correcto del formulario.
     */
    @Override
    public void onValidationSucceeded() {

        //Checking internet connection
        if(!InternetService.thereIsInternetConnection(MainActivity.this)){
            //Mostramos pop up para informar que no se encuentra conectado a internet.
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Por favor, verifique su conexión a internet.")
                    .show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("email", txtEmail.getText().toString());
            jsonObject.put("password", txtPassword.getText().toString());

            Intent intentLogin = new Intent(MainActivity.this, RegisterRequest.class);
            intentLogin.putExtra("uri", StaticServiceData.LOGIN_URI);
            intentLogin.putExtra("jsonData",jsonObject.toString());
            intentLogin.putExtra("action", StaticServiceData.LOGIN_ACTION);

            configureBroadCastReceiver();
            startService(intentLogin);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error al intentar ingresar.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Acciones frente a un ingreso erroneo en el formulario.
     * @param errors
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }

        //Mostramos pop up para informar que hubo un error en el formulario.
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Por favor, verifique los datos ingresados.")
                .show();
    }

    /**
     * Configuramos el broadcast para escuchar el servicio RegisterRequest.
     */
    private void configureBroadCastReceiver() {

        receiver = new ReceiverLogin();
        IntentFilter filterRegister =  new IntentFilter(StaticServiceData.LOGIN_ACTION);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filterRegister);
    }

}
