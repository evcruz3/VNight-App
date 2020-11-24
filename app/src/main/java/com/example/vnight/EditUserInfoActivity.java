package com.example.vnight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vnight.customClasses.UserInfo;
import com.example.vnight.utils.SharedPreferenceHandler;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    UserInfo userInfo;
    Context ctx;

//    @BindView(R.id.et_firstName)
    EditText editTextFirstName;
//    @BindView(R.id.et_lastName)
    EditText editTextLastName;
//    @BindView(R.id.et_batch)
    EditText editTextBatch;
//    @BindView(R.id.et_contactNum)
    EditText editTextContactNumber;
//    @BindView(R.id.et_password)
    EditText editTextPassword;
//    @BindView(R.id.et_passwordRetype)
    EditText editTextPasswordRetype;
//    @BindView(R.id.btn_submit)
    Button buttonSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_info_actvity);
        ctx = this;
        userInfo = SharedPreferenceHandler.getSavedObjectFromPreference(ctx, "UserInfo", "UserInfo", UserInfo.class);
//        ButterKnife.bind(this);
        editTextFirstName = (EditText)findViewById(R.id.et_firstName);
        editTextLastName = (EditText)findViewById(R.id.et_lastName);
        editTextBatch = (EditText)findViewById(R.id.et_batch);
        editTextContactNumber = (EditText)findViewById(R.id.et_contactNum);
        editTextPassword = (EditText)findViewById(R.id.et_password);
        editTextPasswordRetype = (EditText)findViewById(R.id.et_passwordRetype);
        buttonSubmit = (Button)findViewById(R.id.btn_submit);
        buttonSubmit.setOnClickListener(this);


        editTextFirstName.setText(userInfo.getFirstName());
        editTextLastName.setText(userInfo.getLastName());
        editTextBatch.setText("" +userInfo.getBatch());
        editTextContactNumber.setText("+63 "+userInfo.getContactNum());


    }

    @Override
    public void onClick(View v) {
        if(v == buttonSubmit){
            final String firstName = editTextFirstName.getText().toString().trim();
            final String lastName = editTextLastName.getText().toString().trim();
            final int batch =  Integer.parseInt(editTextBatch.getText().toString().trim());
            final String contactNum = editTextContactNumber.getText().toString().trim();
            final String password = editTextPassword.getText().toString().trim();
            final String passwordRetype = editTextPasswordRetype.getText().toString().trim();

            if (firstName.isEmpty()){
                Toast.makeText(ctx,"First Name field must not be empty",Toast.LENGTH_LONG).show();
                return;
            }
            else if(lastName.isEmpty()){
                Toast.makeText(ctx,"Last Name field must not be empty",Toast.LENGTH_LONG).show();
                return;
            }
            else if(password.isEmpty()){
                Toast.makeText(ctx,"password field must not be empty",Toast.LENGTH_LONG).show();
                return;
            } else if(password.compareTo(passwordRetype) != 0){
                Toast.makeText(ctx,"Passwords do not match. Please check your password",Toast.LENGTH_LONG).show();
                return;
            }
            else {
                /* put inside the edit database listener
                userInfo.setFirstName(firstName);
                userInfo.setLastName(lastName);
                userInfo.setBatch(batch);
                userInfo.setContactNum(contactNum);*/
                // TODO: insert database server edit here
                Toast.makeText(EditUserInfoActivity.this, "Update User Info successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditUserInfoActivity.this, UserHomeActivity.class);
                startActivity(intent);
                EditUserInfoActivity.this.finish();
            }
        }

    }
}
