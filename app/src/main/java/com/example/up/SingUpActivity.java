package com.example.up;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import Model.User;
import Model.Utils.InternetService;
import Model.Utils.StaticServiceData;


public class SingUpActivity extends AppCompatActivity implements Validator.ValidationListener{

    //View variables
    private Button btnSingUp;
    @NotEmpty
    private EditText txtName;
    @NotEmpty
    private EditText txtLastname;
    @NotEmpty
    private EditText txtDNI;
    @NotEmpty
    @Email
    private EditText txtEmail;
    @NotEmpty
    @Length(min = 8)
    @Password
    private EditText txtPassword;
    @NotEmpty
    private EditText txtComission;

    private Validator validator;
    private ReceiverRegister receiver;

    private void initializeViewObjects()
    {
        btnSingUp = findViewById(R.id.btnSingIn);
        txtName = findViewById(R.id.txtName);
        txtLastname = findViewById(R.id.txtLastname);
        txtDNI = findViewById(R.id.txtDNI);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtComission = findViewById(R.id.txtComission);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        initializeViewObjects();
        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    public void register(View view)
    {
        validator.validate(true);
    }

    @Override
    public void onValidationSucceeded() {

        User user = createUser();
        JSONObject jsonObject = new JSONObject();

        //Checking internet connection
        if(!InternetService.thereIsInternetConnection(SingUpActivity.this)){
            Toast.makeText(SingUpActivity.this, "No se encuentra conectado a Internet", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            jsonObject.put("env", StaticServiceData.ENV);
            jsonObject.put("name",user.getName());
            jsonObject.put("lastname",user.getLastName());
            jsonObject.put("dni",user.getDni());
            jsonObject.put("email",user.getEmail());
            jsonObject.put("password",user.getPassword());
            jsonObject.put("commission",user.getCommission());
            Intent intentRegister = new Intent(SingUpActivity.this, RegisterRequest.class);
            intentRegister.putExtra("action", StaticServiceData.REGISTER_ACTION);
            intentRegister.putExtra("uri", StaticServiceData.REGISTER_URI);
            intentRegister.putExtra("jsonData",jsonObject.toString());
            configureBroadcastReceiver();
            startService(intentRegister);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
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

    private User createUser() {

        return new User(
                txtName.getText().toString(),
                txtLastname.getText().toString(),
                Integer.parseInt(txtDNI.getText().toString()),
                txtEmail.getText().toString(),
                txtPassword.getText().toString(),
                Integer.parseInt(txtComission.getText().toString())
                );
    }

    private void configureBroadcastReceiver (){
        receiver = new ReceiverRegister();
        IntentFilter filterRegister =  new IntentFilter(StaticServiceData.REGISTER_ACTION);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filterRegister);
    }

}
