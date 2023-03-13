package com.ksacp2022t3.fanarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    AppCompatButton btn_sign_up_parent,btn_sign_up_specialist;
    TextView txt_login;
    FirebaseAuth firebaseAuth;
    ImageView btn_switch_lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_sign_up_parent = findViewById(R.id.btn_sign_up_parent);
        btn_sign_up_specialist = findViewById(R.id.btn_sign_up_specialist);
        txt_login = findViewById(R.id.txt_login);
        firebaseAuth=FirebaseAuth.getInstance();
        btn_switch_lang = findViewById(R.id.btn_switch_lang);


        if(firebaseAuth.getCurrentUser()!=null)
        {
            SharedPreferences sharedPreferences=this.getSharedPreferences("fanar",MODE_PRIVATE);
            String account_type=sharedPreferences.getString("account_type","");
            String language=sharedPreferences.getString("language","En");
            setLocale(language);
            Intent intent;
            if(account_type.equals("Parent"))
            {
                intent = new Intent(MainActivity.this, ParentHomeActivity.class);
            }
            else {
                intent = new Intent(MainActivity.this, SpecialistHomeActivity.class);
            }
            startActivity(intent);
            finish();
        }

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity. class);
                startActivity(intent);
            }
        });

        btn_sign_up_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpParentActivity. class);
                startActivity(intent);
            }
        });

        btn_sign_up_specialist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpSpecialistActivity. class);
                startActivity(intent);
            }
        });

        btn_switch_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale current = getCurrentLocale();
                String new_lang="En";
                if(current.getLanguage().equals("en")) {
                    new_lang="Ar";
                    setLocale("ar");
                } else {
                    new_lang="En";
                    setLocale("en");
                }
                SharedPreferences sharedPreferences=MainActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("language",new_lang);
                editor.apply();


                Intent refresh = getIntent();
                    finish();
                    startActivity(refresh);


            }
        });



    }
    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang.toLowerCase(Locale.ROOT));
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);;
        res.updateConfiguration(conf, dm);
//        Intent refresh = getIntent();
//        finish();
//        startActivity(refresh);
    }

    Locale getCurrentLocale(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getResources().getConfiguration().getLocales().get(0);
        } else{
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }
}