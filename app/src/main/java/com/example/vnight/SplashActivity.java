package com.example.vnight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        if(sp.getBoolean("logged",false)){
            intent = new Intent(getApplicationContext(),UserHomeActivity.class);

        }
        else {
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }
        startMainActivity(intent);
    }

    protected void startMainActivity(final Intent intent){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

}
