package com.example.vnight;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationForm extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button buttonReserve;
    Spinner spinnerPosition;
    String positionSelected;
    Switch switchGuest;
    String playerName;
    SharedPreferences sp;
    SharedPreferences.Editor sp_editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_form);

        sp = getSharedPreferences("login",MODE_PRIVATE);
        sp_editor = sp.edit();

        buttonReserve = (Button)findViewById(R.id.btn_reserve);
        buttonReserve.setOnClickListener(this);

        spinnerPosition = (Spinner)findViewById(R.id.spinnerPosition);
        spinnerPosition.setOnItemSelectedListener(this);

        switchGuest = (Switch)findViewById(R.id.switch1);

        playerName = sp.getString("firstName", "");

        List<String> positions = new ArrayList<String>();
        positions.add("Wing");
        positions.add("Mid");
        positions.add("Setter");
        positions.add("Libero");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, positions);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPosition.setAdapter(dataAdapter);

    }

    @Override
    public void onClick(View v){
        if(v == buttonReserve){
            if(switchGuest.isChecked()) {
                //Toast.makeText(ReservationForm.this, "You chose " + positionSelected + " for your guest", Toast.LENGTH_LONG).show();
                addItemToSheet(playerName + " (G)");
            }
            else {
                //Toast.makeText(ReservationForm.this, "You chose " + positionSelected, Toast.LENGTH_LONG).show();
                addItemToSheet(playerName);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
        buttonReserve.setEnabled(true);
        positionSelected = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        buttonReserve.setEnabled(false);
    }

    private void   addItemToSheet(final String name) {

        final ProgressDialog loading = ProgressDialog.show(this,"Reserving...","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyNr3uVmGA7hHy5-XTgFyOm1BQ_uraabosBk65MURaBk51LFvM/exec?action=reserveSlot",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(ReservationForm.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                        startActivity(intent);
                        ReservationForm.this.finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ReservationForm.this,"A problem has occurred. Please try again",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
                        startActivity(intent);
                        ReservationForm.this.finish();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                //here we pass params
                params.put("action","reserveSlot");
                params.put("name",name);
                params.put("position",positionSelected);

                return params;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }
}
