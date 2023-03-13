package com.ksacp2022t3.fanarapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SpecialistHomeActivity extends AppCompatActivity {
    AppCompatButton btn_sign_out,btn_requests,btn_chat;
    TextView txt_full_name,txt_language,txt_edit,txt_new_requests;
    FirebaseAuth firebaseAuth;
    ImageView btn_go_requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist_home);
        btn_sign_out = findViewById(R.id.btn_sign_out);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_language = findViewById(R.id.txt_language);
        txt_edit = findViewById(R.id.txt_edit);
        btn_go_requests = findViewById(R.id.btn_go_requests);
        txt_new_requests = findViewById(R.id.txt_new_requests);
        btn_chat = findViewById(R.id.btn_chat);
        btn_requests = findViewById(R.id.btn_requests);
        




        

        //get saved logged user name
        SharedPreferences sharedPreferences=this.getSharedPreferences("fanar",MODE_PRIVATE);
        String full_name=sharedPreferences.getString("full_name","");
        String language=sharedPreferences.getString("language","En");
        txt_language.setText(language);
        txt_full_name.setText(full_name);

        firebaseAuth=FirebaseAuth.getInstance();

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(SpecialistHomeActivity.this,MainActivity. class);
                startActivity(intent);
                finish();
            }
        });

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecialistHomeActivity.this,SpecialistEditProfileActivity. class);
                startActivity(intent);
            }
        });

        btn_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecialistHomeActivity.this,RequestsActivity. class);
                startActivity(intent);
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecialistHomeActivity.this,InboxActivity. class);
                startActivity(intent);
            }
        });
        btn_go_requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecialistHomeActivity.this,RequestsActivity. class);
                startActivity(intent);
            }
        });

        txt_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lang=txt_language.getText().toString();



                new AlertDialog.Builder(SpecialistHomeActivity.this)
                        .setTitle(getResources().getString(R.string.confirm))
                        .setMessage(getResources().getString(R.string.language_change_message))
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences sharedPreferences=SpecialistHomeActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                String new_lang="En";
                                if(lang.equals("En")) {
                                    new_lang="Ar";
                                }
                                editor.putString("language",new_lang);
                                editor.apply();
                                Intent intent = new Intent(SpecialistHomeActivity.this,MainActivity. class);
                                startActivity(intent);
                                finish();

                            }
                        }).setNegativeButton(R.string.cancel,null)
                        .show();





            }
        });
    }


    public  void edit(View view)
    {
        Intent intent = new Intent(SpecialistHomeActivity.this,SpecialistEditProfileActivity. class);
        startActivity(intent);
    }

    private void check_requests(){
        txt_new_requests.setVisibility(View.GONE);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("requests")
                .whereEqualTo("to_notify",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()>0)
                        {
                           txt_new_requests.setVisibility(View.VISIBLE);
                        }
                        txt_new_requests.setText(String.valueOf(queryDocumentSnapshots.size()));
                    }
                });
    }

    private  void  check_new_messages(){
        btn_chat.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_chat_24,0,0);
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("inbox")
                .whereArrayContains("users_ids",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc:queryDocumentSnapshots.getDocuments()) {
                            doc.getReference()
                                    .collection("messages")
                                    .whereEqualTo("to",firebaseAuth.getUid())
                                    .whereEqualTo("status","unseen")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            int new_messages_count= queryDocumentSnapshots.getDocuments().size();
                                            if(new_messages_count>0)
                                            {
                                                btn_chat.setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_baseline_mark_unread_chat_alt_24,0,0);
                                                return;
                                            }
                                        }
                                    })
                            ;
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        check_requests();
        check_new_messages();
    }
}