package com.codefury.starthub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FirstLoginActivity extends AppCompatActivity {
    LinearLayout linearLayout1;
    LinearLayout linearLayout2;
    LinearLayout linearLayout3;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    private PrefManager prefManager;
    private Button proceed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        linearLayout1 = findViewById(R.id.startup);
        linearLayout2 = findViewById(R.id.investor);
        linearLayout3 = findViewById(R.id.mentor);
        checkBox1 = findViewById(R.id.checkBox2);
        checkBox2 = findViewById(R.id.checkBox1);
        checkBox3 = findViewById(R.id.checkBox3);
        proceed = findViewById(R.id.cirProceedButton);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        getSupportActionBar().hide();
        //for changing status bar icon colors
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }

        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox2.isChecked() && !checkBox3.isChecked()) {
                    checkBox1.setVisibility(View.VISIBLE);
                    checkBox1.setChecked(true);
                    checkBox1.setClickable(false);
                }
            }
        });
        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox1.isChecked() && !checkBox3.isChecked()) {
                    checkBox2.setVisibility(View.VISIBLE);
                    checkBox2.setChecked(true);
                    checkBox2.setClickable(false);
                }
            }
        });
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox2.isChecked() && !checkBox1.isChecked()) {
                    checkBox3.setVisibility(View.VISIBLE);
                    checkBox3.setChecked(true);
                    checkBox3.setClickable(false);
                }
            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox3.isChecked() && !checkBox2.isChecked() && !checkBox1.isChecked()){
                    Toast.makeText(FirstLoginActivity.this, "Please Select Your Role", Toast.LENGTH_SHORT).show();
                }else{
                    launchHomeScreen();
                }
            }
        });
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }
}