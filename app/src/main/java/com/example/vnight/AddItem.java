package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {


    EditText editTextFirstName,editTextLastName,editTextBatch,editTextContactNum,editTextusername,editTextpassword;
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

        buttonAddItem = (Button)findViewById(R.id.btn_register);
        buttonAddItem.setOnClickListener(this);


    }

    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"Adding Item","Please wait");
        final String firstName = editTextFirstName.getText().toString().trim();
        final String lastName = editTextLastName.getText().toString().trim();
        final String batch = editTextBatch.getText().toString().trim();
        final String contactNum = editTextContactNum.getText().toString().trim();
        final String username = editTextusername.getText().toString().trim();
        final String password = editTextpassword.getText().toString().trim();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=addItem",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(AddItem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","addItem");
                params.put("firstName",firstName);
                params.put("lastName",lastName);
                params.put("batch",batch);
                params.put("contactNum",contactNum);
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }




    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            addItemToSheet();

            //Define what to do when button is clicked
        }
    }
}

