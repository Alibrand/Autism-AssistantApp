package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {
    TextView txt_session_date,txt_session_time,txt_specialist_name;
    EditText txt_card_number,txt_name_on_card,txt_expiry_date,txt_cvv;
    Switch sw_save;
    Spinner sp_card_type;
    AppCompatButton btn_pay;

    ProgressDialog progressDialog;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        txt_session_date = findViewById(R.id.txt_session_date);
        txt_session_time = findViewById(R.id.txt_session_time);
        txt_specialist_name = findViewById(R.id.txt_specialist_name);
        txt_card_number = findViewById(R.id.txt_card_number);
        txt_name_on_card = findViewById(R.id.txt_name_on_card);
        txt_expiry_date = findViewById(R.id.txt_expiry_date);
        txt_cvv = findViewById(R.id.txt_cvv);
        sw_save = findViewById(R.id.sw_save);
        sp_card_type = findViewById(R.id.sp_card_type);
        btn_pay = findViewById(R.id.btn_pay);

        String [] card_types=new String[]{"Visa","MasterCard"};
        ArrayAdapter adapter=new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,card_types);
        sp_card_type.setAdapter(adapter);

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.processing);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        String session_date=getIntent().getStringExtra("session_date");
        txt_session_date.setText(session_date);
        String session_time=getIntent().getStringExtra("session_time");
        txt_session_time.setText(session_time);
        String specialist_name=getIntent().getStringExtra("specialist_name");
        txt_specialist_name.setText(specialist_name);


        String request_id=getIntent().getStringExtra("request_id");
        String specialist_id=getIntent().getStringExtra("specialist_id");

        SharedPreferences sharedPreferences=this.getSharedPreferences("fanar",MODE_PRIVATE);
        String card_type=sharedPreferences.getString("card_type","");
        String card_number=sharedPreferences.getString("card_number","");
        String card_name=sharedPreferences.getString("card_name","");
        String card_expiry_date=sharedPreferences.getString("card_expiry_date","");
        String card_cvv=sharedPreferences.getString("card_cvv","");

        txt_card_number.setText(card_number);
        txt_name_on_card.setText(card_name);
        txt_expiry_date.setText(card_expiry_date);
        txt_cvv.setText(card_cvv);
        sp_card_type.setSelection(Arrays.asList(card_type).indexOf(card_type));



        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_card_number =txt_card_number.getText().toString();
                String str_txt_name_on_card =txt_name_on_card.getText().toString();
                String str_txt_expiry_date =txt_expiry_date.getText().toString();
                String str_txt_cvv =txt_cvv.getText().toString();
                String type=sp_card_type.getSelectedItem().toString();


                if(str_txt_card_number.isEmpty())
                {
                     txt_card_number.setError(getString(R.string.required_field) );
                     return;
                }
                if(!isValidCardNo(str_txt_card_number,type))
                {
                    txt_card_number.setError(getString(R.string.invalid_card_number));
                    return;
                }
                 if(str_txt_name_on_card.isEmpty())
                 {
                      txt_name_on_card.setError(getString(R.string.required_field));
                      return;
                 }

                 if(str_txt_expiry_date.isEmpty())
                 {
                      txt_expiry_date.setError(getString(R.string.required_field));
                      return;
                 }

                 if(!isValidDate(str_txt_expiry_date))
                 {txt_expiry_date.setError(getString(R.string.invalid_date));
                     return;

                 }


                 if(str_txt_cvv.isEmpty())
                 {
                      txt_cvv.setError(getString(R.string.required_field));
                      return;
                 }

                if(str_txt_cvv.length()!=3)
                {
                    txt_cvv.setError(getString(R.string.invalid_cvv));
                    return;
                }


                 //save card info
                SharedPreferences sharedPreferences=PaymentActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(sw_save.isChecked())
                {
                    editor.putString("card_type",type);
                    editor.putString("card_number",str_txt_card_number);
                    editor.putString("card_name",str_txt_name_on_card);
                    editor.putString("card_expiry_date",str_txt_expiry_date);
                    editor.putString("card_cvv",str_txt_cvv);
                    editor.apply();
                }
                else{
                    editor.putString("card_type","");
                    editor.putString("card_number","");
                    editor.putString("card_name","");
                    editor.putString("card_expiry_date","");
                    editor.putString("card_cvv","");
                    editor.apply();
                }



                 progressDialog.show();
                 firestore.collection("requests")
                         .document(request_id)
                         .update("status","Paid",
                                 "to_notify",specialist_id)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 progressDialog.dismiss();
                                 makeText(PaymentActivity.this,getString(R.string.payment_done)  , LENGTH_LONG).show();
                                 finish();


                             }
                         }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 progressDialog.dismiss();
                                 makeText(PaymentActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                             }
                         });



            }
        });



    }
    boolean isValidCardNo(String str,String type)
    {
        String ptVisa = "^4[0-9]{6,}$";
        String ptMasterCard = "^5[1-5][0-9]{5,}$";
        Pattern p;
        if(type.equals("Visa"))
            p = Pattern.compile(ptVisa);
        else
            p=Pattern.compile(ptMasterCard);
         Matcher m = p.matcher(str);
        return m.matches();
    }
     boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        Date date;
        try {
             date=dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }

        if(date.before(new Date()))
        {
            return  false;
        }
        return true;
    }



}