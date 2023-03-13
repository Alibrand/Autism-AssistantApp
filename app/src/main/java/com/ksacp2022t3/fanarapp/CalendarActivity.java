package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.models.ParentAccount;
import com.ksacp2022t3.fanarapp.models.Request;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendar_view;
    TimePicker time_picker;
    AppCompatButton btn_send;
    Calendar calendar,dateCalendar,timeCalendar;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendar_view = findViewById(R.id.calendar);
        time_picker = findViewById(R.id.time_picker);
        btn_send = findViewById(R.id.btn_send);
        btn_back = findViewById(R.id.btn_back);

        
        int period=75;


         calendar=Calendar.getInstance();
        timeCalendar=Calendar.getInstance();
        dateCalendar=Calendar.getInstance();
        calendar_view.setMinDate(calendar.getTimeInMillis());
        firebaseAuth=FirebaseAuth.getInstance();

        String specialist_id=getIntent().getStringExtra("specialist_id");

        String specialist_name=getIntent().getStringExtra("specialist_name");
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.sending);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        time_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                timeCalendar.set(Calendar.HOUR,i);
                timeCalendar.set(Calendar.MINUTE,i1);
                 timeCalendar.set(Calendar.AM_PM,i>=12?Calendar.PM:Calendar.AM);

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        calendar_view.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                 dateCalendar.set(Calendar.YEAR,i);
                dateCalendar.set(Calendar.MONTH,i1);
                dateCalendar.set(Calendar.DAY_OF_MONTH,i2);

            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                calendar.set(Calendar.HOUR,timeCalendar.get(Calendar.HOUR));
                calendar.set(Calendar.MINUTE,timeCalendar.get(Calendar.MINUTE));
                calendar.set(Calendar.AM_PM,timeCalendar.get(Calendar.AM_PM));
                calendar.set(Calendar.DAY_OF_MONTH,dateCalendar.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.MONTH,dateCalendar.get(Calendar.MONTH));
                calendar.set(Calendar.YEAR,dateCalendar.get(Calendar.YEAR));

                Date session_date=calendar.getTime();


               progressDialog.show();


               firestore.collection("requests")
                       .whereEqualTo("specialist_id",specialist_id)
                       .whereEqualTo("status","Paid")
                       .get()
                       .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                           @Override
                           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                               List<Request> requests=queryDocumentSnapshots.toObjects(Request.class);
                               if(requests.size()>0) {
                                   Log.d("ttty","tt"+requests.size());
                                   for (Request request : requests) {
                                       Calendar calendar = Calendar.getInstance();
                                       calendar.setTime(request.getSession_date());
                                       calendar.add(Calendar.MINUTE, period);

                                       if (session_date.after(request.getSession_date()) && session_date.before(calendar.getTime())) {
                                           makeText(CalendarActivity.this, getResources().getString(R.string.date_reserved), LENGTH_LONG).show();
                                           progressDialog.dismiss();
                                           return;
                                       }

                                   }
                               }

                               firestore.collection("accounts")
                                       .document(firebaseAuth.getUid())
                                       .get()
                                       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                                               ParentAccount account=documentSnapshot.toObject(ParentAccount.class);
                                               Request session_request=new Request();
                                               DocumentReference new_req=firestore.collection("requests")
                                                       .document();
                                               session_request.setId(new_req.getId());
                                               session_request.setSession_date(session_date);
                                               session_request.setSender_id(account.getId());
                                               session_request.setSender_name(account.getFirst_name()+" "+account.getLast_name());
                                               session_request.setSender_state(account.getState_description());
                                               session_request.setSpecialist_id(specialist_id);
                                               session_request.setSpecialist_name(specialist_name);
                                               session_request.setTo_notify(specialist_id);

                                               new_req.set(session_request)
                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void unused) {
                                                               progressDialog.dismiss();
                                                               makeText(CalendarActivity.this, R.string.request_sent_success , LENGTH_LONG).show();
                                                               finish();
                                                           }
                                                       }).addOnFailureListener(new OnFailureListener() {
                                                           @Override
                                                           public void onFailure(@NonNull Exception e) {
                                                               progressDialog.dismiss();
                                                               makeText(CalendarActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                           }
                                                       });
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               progressDialog.dismiss();
                                               makeText(CalendarActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                           }
                                       });
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(CalendarActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                           }
                       });

            }
        });
















    }
}