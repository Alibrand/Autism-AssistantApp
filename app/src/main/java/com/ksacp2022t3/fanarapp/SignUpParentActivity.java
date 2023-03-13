package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.models.InitCategories;
import com.ksacp2022t3.fanarapp.models.ParentAccount;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpParentActivity extends AppCompatActivity {
    ImageView btn_back;
    EditText txt_first_name,txt_last_name,
            txt_state_description, txt_phone,txt_email,txt_password,txt_confirm_password;
    DatePicker dp_birthdate;
    AppCompatButton btn_sign_up;
    RadioGroup group_gender;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_parent);
        btn_back = findViewById(R.id.btn_back);
        txt_first_name = findViewById(R.id.txt_first_name);
        txt_last_name = findViewById(R.id.txt_last_name);
        txt_state_description = findViewById(R.id.txt_state_description);
        txt_phone = findViewById(R.id.txt_phone);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_confirm_password = findViewById(R.id.txt_confirm_password);
        dp_birthdate = findViewById(R.id.dp_birthdate);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        group_gender = findViewById(R.id.group_gender);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.sign_up));
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);





        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_first_name =txt_first_name.getText().toString();
                String str_txt_last_name =txt_last_name.getText().toString();
                String str_txt_state_description =txt_state_description.getText().toString();
                String str_txt_email =txt_email.getText().toString();
                String str_txt_phone =txt_phone.getText().toString();
                String str_txt_password =txt_password.getText().toString();
                String str_txt_confirm_password =txt_confirm_password.getText().toString();
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
                if(str_txt_email.isEmpty())
                {
                    txt_email.setError(getResources().getString(R.string.required_field));
                    return;
                }

                if(!isValidEmail(str_txt_email) )
                {
                    txt_email.setError(getResources().getString(R.string.bad_email));
                    return;
                }



                if(str_txt_password.isEmpty())
                {
                    txt_password.setError(getResources().getString(R.string.required_field));
                    return;
                }
                if(!isValidPassword(str_txt_password) )
                {
                    txt_password.setError(getResources().getString(R.string.weak_password));
                    return;
                }

                if(str_txt_confirm_password.isEmpty())
                {
                    txt_confirm_password.setError(getResources().getString(R.string.required_field));
                    return;
                }
                if(!str_txt_confirm_password.equals(str_txt_password))
                {
                    txt_confirm_password.setError(getResources().getString(R.string.passwords_not_match));
                    return;
                }


                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(str_txt_email,str_txt_password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                String uid=authResult.getUser().getUid();
                                ParentAccount account=new ParentAccount();
                                account.setFirst_name(str_txt_first_name);
                                account.setLast_name(str_txt_last_name);
                                account.setGender(gender);
                                account.setAccount_type("Parent");
                                account.setGender(gender);
                                account.setBirth_date(calendar.getTime());
                                account.setState_description(str_txt_state_description);
                                account.setPhone(str_txt_phone);
                                account.setEmail(str_txt_email);
                                account.setId(uid);

                               firestore.collection("accounts")
                                        .document(uid).set(account)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();

                                                InitCategories initCategories=new InitCategories(SignUpParentActivity.this);
                                                initCategories.insert_to_firestore();


                                                makeText(SignUpParentActivity.this,getResources().getString(R.string.account_created) , LENGTH_LONG).show();
                                                SharedPreferences sharedPreferences=SignUpParentActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                                editor.putString("account_type",account.getAccount_type());
                                                editor.putString("full_name",account.getFirst_name()+" "+account.getLast_name());
                                                editor.apply();

                                                Intent intent = new Intent(SignUpParentActivity.this,ParentHomeActivity.
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
                                                makeText(SignUpParentActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(SignUpParentActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });





            }
        });




    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);

        return matcher.matches();

    }
}