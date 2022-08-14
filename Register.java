package com.codefury.starthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import android.os.Bundle;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail, UserPassword,username,phNumber;
    private TextView AlreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        changeStatusBarColor();


        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();


        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                SendUserToLoginActivity();
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }




    private void CreateNewAccount()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String name = username.getText().toString();
        String mobileNumber = phNumber.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            UserEmail.setError("Email ID is Required.");
        }
        if (TextUtils.isEmpty(password))
        {
            UserPassword.setError("Password is Required.");
        }
        if(TextUtils.isEmpty(name)){
            username.setError("Name is Required.");
        }
        if(TextUtils.isEmpty(mobileNumber)){
            phNumber.setError("Mobile Number is Required.");
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we wre creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                String deviceToken = FirebaseMessaging.getInstance().getToken().toString();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(currentUserID).setValue("");
                                HashMap<String, String> statusMap = new HashMap<>();
                                statusMap.put("uid", currentUserID);
                                statusMap.put("name", name);
                                statusMap.put("device_token",deviceToken);
                                statusMap.put("mobile", mobileNumber);
                                RootRef.child("Users").child(currentUserID).setValue(statusMap);

                                SendUserToMainActivity();
                                Toast.makeText(Register.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(Register.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }




    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,login.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }
    private void InitializeFields()
    {
        CreateAccountButton = findViewById(R.id.cirRegisterButton);
        UserEmail = (EditText) findViewById(R.id.editTextEmail_register);
        UserPassword = (EditText) findViewById(R.id.editTextPassword_register);
        AlreadyHaveAccountLink = (TextView) findViewById(R.id.alreyhaveacc);
        username = findViewById(R.id.editTextName_register);
        phNumber = findViewById(R.id.editTextMobile_register);
        loadingBar = new ProgressDialog(this);
    }


    private void SendUserToLoginActivity()
    {
        startActivity(new Intent(this,login.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Register.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}