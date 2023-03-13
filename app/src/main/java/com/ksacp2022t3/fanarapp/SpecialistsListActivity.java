package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.adapters.SpecialistListAdapter;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.List;

public class SpecialistsListActivity extends AppCompatActivity {

    RecyclerView recycler_specialists;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialists_list);
        recycler_specialists = findViewById(R.id.recycler_specialists);
        btn_back = findViewById(R.id.btn_back);



        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle(getResources().getString(R.string.loading));
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
               .whereEqualTo("account_type","Specialist")
               .get()
               .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       List<SpecialistAccount> accountList=queryDocumentSnapshots.toObjects(SpecialistAccount.class);

                       SpecialistListAdapter adapter=new SpecialistListAdapter(accountList,SpecialistsListActivity.this);
                       recycler_specialists.setAdapter(adapter);
                       progressDialog.dismiss();
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       progressDialog.dismiss();
                        Toast.makeText(SpecialistsListActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();

                   }
               });

    }
}