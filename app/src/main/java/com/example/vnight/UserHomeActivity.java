package com.example.vnight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView welcomeText;
    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;
    Button buttonLogOut, buttonReserveSlot, buttonSeeReservedPlayers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_home_activity);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();
        buttonLogOut = (Button)findViewById(R.id.btn_logOut);
        buttonLogOut.setOnClickListener(this);
        buttonReserveSlot = (Button)findViewById(R.id.btn_reserveSlot);
        buttonReserveSlot.setOnClickListener(this);
        buttonSeeReservedPlayers = (Button)findViewById(R.id.btn_list_reserved);
        buttonSeeReservedPlayers.setOnClickListener(this);

        String username = sp.getString("username", "");

        welcomeText = (TextView)findViewById(R.id.textView2);
        welcomeText.setText("Welcome, "+username);

    }
    @Override
    public void onClick(View v){
        if(v == buttonLogOut){
            sp_editor.putBoolean("logged", false).apply();
            sp_editor.putString("username", "").apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            UserHomeActivity.this.finish();
        }

        if(v == buttonReserveSlot){
            Intent intent = new Intent(getApplicationContext(), ReservationForm.class);
            startActivity(intent);
        }

        if(v == buttonSeeReservedPlayers){
            Intent intent = new Intent(getApplicationContext(), ListReservedPlayers.class);
            startActivity(intent);
        }
    }
}
