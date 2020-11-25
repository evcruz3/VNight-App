package com.example.vnight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
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
import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.DatabaseHandler;
import com.example.vnight.utils.SharedPreferenceHandler;

public class SplashActivity extends AppCompatActivity {

    //SharedPreferences sp;
    //SharedPreferences.Editor sp_editor;
    TextView Text;

//    SharedPreferenceHandler mSharedPreferenceHandler;
    Context ctx;
    UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ctx = this;
//        mSharedPreferenceHandler = new SharedPreferenceHandler(ctx);
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx,"UserInfo", "UserInfo", UserInfo.class);


        //sp = getSharedPreferences("login",MODE_PRIVATE);
        //sp_editor = sp.edit();
        Text = (TextView)findViewById(R.id.textView);
        Intent intent;

//        if(sp.getBoolean("logged",false)){
//            Text.setText("Logged: True");
//        }
//        else
//            Text.setText("Logged: False");
        Text.setText("Initializing...");

        if(userInfo != null){

//            if(sp.getString("username","").compareTo("admin") == 0)
            if(userInfo.getUsername().compareTo("admin") == 0)
                startMainActivity(new Intent(getApplicationContext(),AdminHomeActivity.class));
            else
                startMainActivity( new Intent(getApplicationContext(),UserHomeActivity.class));
        }
        else {
            DatabaseHandler.doActionToSheet(ctx, null, "pokeServer", null, new DatabaseHandler.onResponseListener() {
                @Override
                public void processResponse(String response) {
                    Text.setText("All is set!");
                    startMainActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    SplashActivity.this.finish();
                }
            });
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
