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
import Model.User;
import Model.Utils.InternetService;
import Model.Utils.StaticServiceData;


public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static String TAG = MainActivity.class.getName();

    private Button btnSingIn;
    private Button btnSingUp;
    @NotEmpty
    @Email
    private EditText txtEmail;
    @NotEmpty
    @Length(min = 8)
    @Password
    private EditText txtPassword;

    private Validator validator;
    private ReceiverLogin receiver;

    private void initializeViewObjects()
    {
        btnSingIn = findViewById(R.id.btnSingUp);
        btnSingUp = findViewById(R.id.btnSingIn);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
    }

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

    public void goSingUp(View view)
    {
        Intent intent = new Intent(this, SingUpActivity.class);
        startActivity(intent);
    }

    public void singIn(View view)
    {
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {

        User user = new User(txtEmail.getText().toString(), txtPassword.getText().toString());
        JSONObject jsonObject = new JSONObject();

        //Checking internet connection
        if(!InternetService.thereIsInternetConnection(MainActivity.this)){
            Toast.makeText(MainActivity.this, "No se encuentra conectado a Internet", Toast.LENGTH_LONG).show();
            return;
        }

        try {

            jsonObject.put("email",user.getEmail());
            jsonObject.put("password",user.getPassword());

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
    }

    private void configureBroadCastReceiver() {

        receiver = new ReceiverLogin();
        IntentFilter filterRegister =  new IntentFilter(StaticServiceData.LOGIN_ACTION);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filterRegister);
    }

}
