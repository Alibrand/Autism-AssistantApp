package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.models.Account;

public class LoginActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_email,txt_password;
    AppCompatButton btn_log_in;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    TextView txt_sign_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_back = findViewById(R.id.btn_back);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_log_in = findViewById(R.id.btn_login);
        txt_sign_up = findViewById(R.id.txt_sign_up);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle(getResources().getString(R.string.login));
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        btn_log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_email = txt_email.getText().toString();
                String str_txt_password = txt_password.getText().toString();

                if(str_txt_email.isEmpty())
                {
                     txt_email.setError(getResources().getString(R.string.required_field));
                     return;
                }
                if(str_txt_password.isEmpty())
                {
                     txt_password.setError(getResources().getString(R.string.required_field));
                     return;
                }

                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                String uid=authResult.getUser().getUid();
                                Log.d("fanaar",uid);

                                firestore.collection("accounts")
                                        .document(uid)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                progressDialog.dismiss();
                                                Account account=documentSnapshot.toObject(Account.class);
                                                Log.d("fanaar",documentSnapshot.getData().get("account_type").toString());
                                                SharedPreferences sharedPreferences=LoginActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("account_type",account.getAccount_type());
                                                editor.putString("full_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.apply();
                                                Intent intent;
                                                if(account.getAccount_type().equals("Parent"))
                                                 intent = new Intent(LoginActivity.this,ParentHomeActivity.
                                                        class);
                                                else
                                                    intent = new Intent(LoginActivity.this,SpecialistHomeActivity.
                                                            class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("EXIT", true);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(LoginActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                 Toast.makeText(LoginActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


    }
}