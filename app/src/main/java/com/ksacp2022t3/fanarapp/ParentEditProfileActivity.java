package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.models.ParentAccount;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.Calendar;

public class ParentEditProfileActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_first_name,txt_last_name,
            txt_state_description, txt_phone;
    DatePicker dp_birthdate;
    AppCompatButton btn_save;
    RadioGroup group_gender;
    RadioButton rdbtn_male,rdbtn_female;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_edit_profile);
        btn_back = findViewById(R.id.btn_back);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_state_description = findViewById(R.id.txt_state_description);
        txt_phone = findViewById(R.id.txt_phone);
        dp_birthdate = findViewById(R.id.dp_birthdate);
        btn_save = findViewById(R.id.btn_save);
        group_gender = findViewById(R.id.group_gender);
        rdbtn_male = findViewById(R.id.rdbtn_male);
        rdbtn_female = findViewById(R.id.rdbtn_female);



        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.loading));
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        load_info();




        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_state_description =txt_state_description.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();

                Calendar calendar=Calendar.getInstance();
                calendar.set(dp_birthdate.getYear(),dp_birthdate.getMonth(),dp_birthdate.getDayOfMonth());
                RadioButton selected_gender=findViewById(group_gender.getCheckedRadioButtonId());
                String gender=selected_gender.getText().toString();

                if(str_txt_first_name.isEmpty())
                {
                    txt_first_name.setError(getResources().getString(R.string.required_field));
                    return;
                }
                if(str_txt_last_name.isEmpty())
                {
                    txt_last_name.setError(getResources().getString(R.string.required_field));
                    return;
                }

                if(str_txt_state_description.isEmpty())
                {
                    txt_state_description.setError(getResources().getString(R.string.required_field));
                    return;
                }
                if(str_txt_phone.isEmpty())
                {
                    txt_phone.setError(getResources().getString(R.string.required_field));
                    return;
                }



                progressDialog.show();

                                String uid=firebaseAuth.getUid();

                                firestore.collection("accounts")
                                        .document(uid).
                                        update(
                                                "first_name",str_txt_first_name,
                                                "last_name",str_txt_last_name,
                                                "state_description",str_txt_state_description,
                                                "birth_date",calendar.getTime(),
                                                "phone",str_txt_phone,
                                                "gender",gender
                                        )
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(ParentEditProfileActivity.this, R.string.changes_saved , LENGTH_LONG).show();
                                                SharedPreferences sharedPreferences=ParentEditProfileActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("full_name",str_txt_first_name+" "+str_txt_last_name);
                                                editor.apply();





                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(ParentEditProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });









            }
        });




    }

    void load_info(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        ParentAccount account=documentSnapshot.toObject(ParentAccount.class);
                        txt_first_name.setText(account.getFirst_name());
                        txt_last_name.setText(account.getLast_name());
                        txt_phone.setText(account.getPhone());
                        txt_state_description.setText(account.getState_description());

                        if(account.getGender().equals("male") || account.getGender().equals("ذكر"))
                        {
                            rdbtn_male.setChecked(true);
                        }
                        else
                        {
                            rdbtn_female.setChecked(true);
                        }

                        Calendar calendar=Calendar.getInstance();
                        calendar.setTime(account.getBirth_date());

                        dp_birthdate.updateDate(calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) );



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(ParentEditProfileActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}