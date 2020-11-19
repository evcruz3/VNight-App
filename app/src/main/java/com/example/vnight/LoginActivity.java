package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAddItem;
    Button buttonListItem;
    Button buttonLogIn;
    EditText editTextUsername, editTextPassword;
    ProgressDialog loading;

    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_acivity);

        buttonAddItem = (Button)findViewById(R.id.btn_register);
        buttonAddItem.setOnClickListener(this);

        buttonListItem = (Button)findViewById(R.id.btn_list_items);
        buttonListItem.setOnClickListener(this);

        buttonLogIn = (Button)findViewById(R.id.btn_LogIn);
        buttonLogIn.setOnClickListener(this);

        editTextUsername = (EditText)findViewById(R.id.username);
        editTextPassword = (EditText)findViewById(R.id.password);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();

    }

    @Override
    public void onClick(View v) {
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if(v==buttonAddItem){

            Intent intent = new Intent(getApplicationContext(),AddItem.class);
            startActivity(intent);
        }

        if(v==buttonListItem){

            Intent intent = new Intent(getApplicationContext(),ListItem.class);
            startActivity(intent);
        }

        if(v==buttonLogIn){
            checkLogInCredentials(username, password);

        }

    }

    private void checkLogInCredentials(final String username, final String password) {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Boolean logged = false;
                        String firstName = "";
                        String lastName = "";
                        String batch = "";
                        String contactNum = "";
                        loading.dismiss();

                        try {
                            //JSONObject jobj = new JSONObject(jsonResponse.substring(jsonResponse.indexOf("{"),jsonResponse.lastIndexOf("}")+1));
                            System.out.println(response);
                            JSONObject jobj = new JSONObject(response);
                            JSONArray jarray = jobj.getJSONArray("items");


                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jo = jarray.getJSONObject(i);

                                logged = jo.getBoolean("logged");
                                firstName = jo.getString("firstName");
                                lastName = jo.getString("lastName");
                                batch = jo.getString("batch");
                                contactNum = jo.getString("contactNum");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        System.out.println("return: " + firstName + lastName + batch + contactNum);

                        if(logged){
                            Toast.makeText(LoginActivity.this,"Authentication Successful",Toast.LENGTH_LONG).show();
                            sp_editor.putString("username", username).apply();
                            sp_editor.putBoolean("logged",true).apply();
                            sp_editor.putString("firstName", firstName).apply();
                            sp_editor.putString("lastName", lastName).apply();
                            sp_editor.putString("batch", batch).apply();
                            sp_editor.putString("contactNum",contactNum).apply();
                            Intent intent = new Intent(getApplicationContext(),UserHomeActivity.class);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Invalid username and password",Toast.LENGTH_LONG).show();
                        }

                        //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        //startActivity(intent);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","checkLoginCredentials");
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }



}