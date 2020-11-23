package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vnight.utils.DatabaseHandler;

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {


    EditText editTextFirstName,editTextLastName,editTextBatch,editTextContactNum,editTextusername,editTextpassword, editTextPasswordRetype;
    Button buttonAddItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        editTextFirstName = (EditText)findViewById(R.id.firstName);
        editTextLastName = (EditText)findViewById(R.id.lastName);
        editTextBatch = (EditText)findViewById(R.id.batch);
        editTextContactNum = (EditText)findViewById(R.id.contactNum);
        editTextusername = (EditText)findViewById(R.id.username);
        editTextpassword = (EditText)findViewById(R.id.password);
        editTextPasswordRetype = (EditText)findViewById(R.id.passwordRetype);

        buttonAddItem = (Button)findViewById(R.id.btn_register);
        buttonAddItem.setOnClickListener(this);


    }

    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {


        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String batch = editTextBatch.getText().toString().trim();
        final String contactNum = editTextContactNum.getText().toString().trim();
        final String username = editTextusername.getText().toString().trim();
        final String password = editTextpassword.getText().toString().trim();
        final String passwordRetype = editTextPasswordRetype.getText().toString().trim();

        if (firstName.isEmpty()){
            Toast.makeText(AddItem.this,"First Name field must not be empty",Toast.LENGTH_LONG).show();
            return;
        }
        else if(lastName.isEmpty()){
            Toast.makeText(AddItem.this,"Last Name field must not be empty",Toast.LENGTH_LONG).show();
            return;
        }
        else if(username.isEmpty()){
            Toast.makeText(AddItem.this,"username field must not be empty",Toast.LENGTH_LONG).show();
            return;
        }
        else if(password.isEmpty()){
            Toast.makeText(AddItem.this,"password field must not be empty",Toast.LENGTH_LONG).show();
            return;
        } else if(password.compareTo(passwordRetype) != 0){
            Toast.makeText(AddItem.this,"Passwords do not match. Please check your password",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Map<String, String> params = new HashMap<>();

            //here we pass params
            //params.put("action", "addItem");
            params.put("firstName", firstName);
            params.put("lastName", lastName);
            params.put("batch", batch);
            params.put("contactNum", contactNum);
            params.put("username", username);
            params.put("password", password);

//            final String key = username+password;

//            final ProgressDialog loading = ProgressDialog.show(AddItem.this,"Signing you up","Please wait");
            DatabaseHandler.addRowEntryToSheet(AddItem.this, DatabaseHandler.PLAYERS_SHEET_NAME, params, new DatabaseHandler.onResponseListener() {
                @Override
                public void processResponse(String response) {
                    if(response.compareTo(DatabaseHandler.WriteReturnCodes.ROW_WRITE_SUCCESS) == 0){
//                        System.out.println("DEBUG: Just checking if i went here");
                        Toast.makeText(AddItem.this,"Sign-up successful",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        AddItem.this.finish();
                    }
                    else if(response.compareTo(DatabaseHandler.WriteReturnCodes.DUPLICATE_KEY_DETECTED) == 0){
                        Toast.makeText(AddItem.this, "username already exists",Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }




    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            addItemToSheet();

            //Define what to do when button is clicked
        }
    }
}

