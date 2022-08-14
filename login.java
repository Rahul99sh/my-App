package com.codefury.starthub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apachat.loadingbutton.core.customViews.CircularProgressButton;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class login extends AppCompatActivity {

    private EditText id;
    private EditText password;
    private Button procced;
    private TextView forgotpass;
    private ImageView loginsucess;
    private ProgressDialog progressDialog;
    private CardView cardView;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference RootRef;
    private LinearLayout cardLinear;
    private LinearLayout load_linear;
    private ImageView load_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.login_bk_color));
        }
        setContentView(R.layout.activity_login);

        CircularProgressButton btn = (CircularProgressButton) findViewById(R.id.cirLoginButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.startAnimation();
            }
        });


//        Glide.with(this).asGif().load(R.drawable.log_sucess).into(loginsucess);
//        Glide.with(this).asGif().load(R.drawable.loading1).into(load_auth);

//        final String UserTag = getIntent().getExtras().get("tag").toString();
        forgotpass = findViewById(R.id.forgot_pass);
        id = findViewById(R.id.editTextEmail_login);
        password = findViewById(R.id.editTextPassword_login);
        procced = findViewById(R.id.cirLoginButton);
        forgotpass = findViewById(R.id.forgot_pass);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();

        procced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mid = id.getText().toString();
                String mpass = password.getText().toString();
                hideKeybaord(view);
                if (TextUtils.isEmpty(mid)) {
                    id.setError("Email ID is Required.");
                }
                else if (TextUtils.isEmpty(mpass)) {
                    password.setError("Password is Required.");
                }
                else if (mpass.length() < 6) {
                    password.setError("Password should be more than 6 characters.");
                } else {
                        progressDialog.setMessage("Please wait while Logging in...");
                        progressDialog.setTitle("Login");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                    mAuth.signInWithEmailAndPassword(mid, mpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String CurrentUserId = mAuth.getCurrentUser().getUid();

                                RootRef.child("Users").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        Toast.makeText(login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                        id.setText("");
                                        password.setText("");
                                        progressDialog.dismiss();
                                        sendUserToFirstloginActivity();
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            } else {
                                progressDialog.dismiss();
                                if(task.getException().getMessage().contains("CONFIGURATION_NOT_FOUND")){
                                    Toast.makeText(login.this, "Error : User does not exists.", Toast.LENGTH_SHORT).show();

                                }else {

                                    Toast.makeText(login.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
                }
            }
        });


        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendusertoforgotpassactivity();
            }
        });


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                btn.doneLoadingAnimation("#ffffff","R.");
//                btn.revertAnimation();
//            }
//        }, 3000);

    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,Register.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);

    }


    private void hideKeybaord(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }


    private void sendUserToFirstloginActivity() {
        Intent intent = new Intent(this,FirstLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }
    private void sendusertoforgotpassactivity() {
        Intent intent = new Intent(this,ForgotPassword.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }


}