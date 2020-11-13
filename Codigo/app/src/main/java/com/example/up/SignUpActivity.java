package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import Model.Receivers.ReceiverRegister;
import Model.Requests.RegisterRequest;
import Model.Utils.InternetService;
import Model.Utils.StaticServiceData;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity implements Validator.ValidationListener{

    //View variables
    private Button btnSingUp;
    @NotEmpty(message = "Requerido")
    private EditText txtName;
    @NotEmpty(message = "Requerido")
    private EditText txtLastname;
    @NotEmpty(message = "Requerido")
    @Length(max = 9, message = "El DNI no puede ser mayor a 999999999")
    private EditText txtDNI;
    @NotEmpty(message = "Requerido")
    @Email(message =  "Email inválido")
    private EditText txtEmail;
    @NotEmpty(message = "Requerido")
    @Length(min = 8, message = "La contraseña debe tener al menos 8 digitos")
    @Password(message = "Contraseña inválida")
    private EditText txtPassword;
    @NotEmpty(message = "Requerido")
    @Length(max = 9, message = "La comision no puede ser mayor a 999999999")
    private EditText txtComission;

    private Validator validator;
    private ReceiverRegister receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Inicializo los objetos de la vista
        initializeViewObjects();

        //Inicializo el validator para el formulario.
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    /**
     * Inicializa los objetos de la vista.
     */
    private void initializeViewObjects() {
        btnSingUp = (Button) findViewById(R.id.btnSingIn);
        txtName = (EditText) findViewById(R.id.txtName);
        txtLastname = (EditText) findViewById(R.id.txtLastname);
        txtDNI = (EditText) findViewById(R.id.txtDNI);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtComission = (EditText) findViewById(R.id.txtComission);
    }

    /**
     * btnSingIn Onclick method.
     * @param view
     */
    public void register(View view)
    {
        validator.validate(true);
    }

    /**
     * Acciones frente a un ingreso correcto del formulario.
     */
    @Override
    public void onValidationSucceeded() {
        //Checking internet connection
        if(!InternetService.thereIsInternetConnection(SignUpActivity.this)){
            //Mostramos pop up para informar que no se encuentra conectado a internet.
            new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Por favor, verifique su conexión a internet.")
                    .show();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("env", StaticServiceData.ENV);
            jsonObject.put("name", txtName.getText().toString());
            jsonObject.put("lastname", txtLastname.getText().toString());
            jsonObject.put("dni", Integer.parseInt(txtDNI.getText().toString()));
            jsonObject.put("email", txtEmail.getText().toString());
            jsonObject.put("password", txtPassword.getText().toString());
            jsonObject.put("commission", Integer.parseInt(txtComission.getText().toString()));
            Intent intentRegister = new Intent(SignUpActivity.this, RegisterRequest.class);
            intentRegister.putExtra("action", StaticServiceData.REGISTER_ACTION);
            intentRegister.putExtra("uri", StaticServiceData.REGISTER_URI);
            intentRegister.putExtra("jsonData",jsonObject.toString());
            configureBroadcastReceiver();
            startService(intentRegister);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Toast.makeText(SignUpActivity.this, "Error al intentar registrarse.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Acciones frente a un ingreso erroneo en el formulario.
     * @param errors
     */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        //Mostramos los errores existenes en cada campo del formulario.
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
        new SweetAlertDialog(SignUpActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Por favor, verifique los datos ingresados.")
                .show();
    }

    /**
     * Configuramos el broadcast para escuchar el servicio RegisterRequest.
     */
    private void configureBroadcastReceiver (){
        receiver = new ReceiverRegister();
        IntentFilter filterRegister =  new IntentFilter(StaticServiceData.REGISTER_ACTION);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filterRegister);
    }

}
