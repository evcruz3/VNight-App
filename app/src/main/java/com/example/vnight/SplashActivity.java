package com.example.vnight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

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

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;
    TextView Text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();
        Text = (TextView)findViewById(R.id.textView);
        Intent intent;

//        if(sp.getBoolean("logged",false)){
//            Text.setText("Logged: True");
//        }
//        else
//            Text.setText("Logged: False");
        Text.setText("Initializing...");

        if(sp.getBoolean("logged",false)){


            startMainActivity( new Intent(getApplicationContext(),UserHomeActivity.class));
        }
        else {
            {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=getItems",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Text.setText("All is set!");

                                startMainActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Text.setText("Network Error. Please check your internet connection");
                            }
                        }
                );

                int socketTimeOut = 50000;
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

                stringRequest.setRetryPolicy(policy);

                RequestQueue queue = Volley.newRequestQueue(this);
                queue.add(stringRequest);
            }

        }

    }

    protected void startMainActivity(final Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 0);
    }

}
