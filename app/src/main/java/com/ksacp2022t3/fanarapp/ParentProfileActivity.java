package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.models.ParentAccount;

import java.text.SimpleDateFormat;

public class ParentProfileActivity extends AppCompatActivity {

    TextView txt_birth_date,txt_gender,txt_state_description,txt_name;
    AppCompatButton btn_call;
    ImageView btn_back;

    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_profile);
        txt_birth_date = findViewById(R.id.txt_birth_date);
        txt_gender = findViewById(R.id.txt_gender);
        txt_state_description = findViewById(R.id.txt_state_description);
        txt_name = findViewById(R.id.txt_full_name);
        btn_call = findViewById(R.id.btn_call);
        btn_back = findViewById(R.id.btn_back);




        String id=getIntent().getStringExtra("id");

        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        progressDialog.show();
        firestore.collection("accounts")
                .document(id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        ParentAccount account=documentSnapshot.toObject(ParentAccount.class);

                        txt_name.setText(account.getFullName());
                        txt_gender.setText(account.getGender());
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        txt_birth_date.setText(simpleDateFormat.format(account.getBirth_date()));

                        txt_state_description.setText(account.getState_description());

                        btn_call.setText(account.getPhone());
                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                startActivity(intent);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ParentProfileActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });







    }
}