package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;

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
    ImageView passwordViewButton;

//    SharedPreferenceHandler mSharedPreferenceHandler;
    UserInfo userInfo;

    Context ctx;

//    SharedPreferences sp;
//    SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_acivity);
        ctx = this;

//        mSharedPreferenceHandler = new SharedPreferenceHandler(this);



        buttonAddItem = (Button)findViewById(R.id.btn_register);
        buttonAddItem.setOnClickListener(this);

        buttonListItem = (Button)findViewById(R.id.btn_list_items);
        buttonListItem.setOnClickListener(this);

        buttonLogIn = (Button)findViewById(R.id.btn_LogIn);
        buttonLogIn.setOnClickListener(this);

        editTextUsername = (EditText)findViewById(R.id.username);
        editTextPassword = (EditText)findViewById(R.id.password);

        passwordViewButton = (ImageView)findViewById(R.id.iv_showPassBtn);
        passwordViewButton.setOnClickListener(this);

//        sp = getSharedPreferences("login",MODE_PRIVATE);
//        sp_editor = sp.edit();

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

        if(v==passwordViewButton){
            if(editTextPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

    }


    private void checkLogInCredentials(final String username, final String password) {



        //loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        Map<String, String> params = new HashMap<>();

        //here we pass params
        //params.put("action","checkLoginCredentials");
        params.put("username",username);
        params.put("password",password);

        DatabaseHandler.doActionToSheet(ctx, "Items", "checkLoginCredentials", params, new DatabaseHandler.onResponseListener() {
            @Override
            public void processResponse(String response) {
                Boolean logged = false;
                String firstName = "";
                String lastName = "";
                int batch = -1;
                String contactNum = "";
                int entryID = 0;
                //loading.dismiss();

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
                        batch = jo.getInt("batch");
                        contactNum = jo.getString("contactNum");
                        entryID = jo.getInt("entryID");


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //System.out.println("return: " + firstName + lastName + batch + contactNum);

                if (logged) {
                    userInfo = new UserInfo(entryID, firstName, lastName, batch, contactNum, username);
                    SharedPreferenceHandler.saveObjectToSharedPreference(ctx, "UserInfo", "UserInfo", userInfo);

                    if (username.compareTo("admin") == 0) {
                        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
                        startActivity(intent);

                    } else {
                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                        startActivity(intent);
                    }
                    LoginActivity.this.finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Invalid username and password",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}