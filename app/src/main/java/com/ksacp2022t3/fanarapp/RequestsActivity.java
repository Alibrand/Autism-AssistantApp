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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.ksacp2022t3.fanarapp.adapters.RequestsListAdapter;
import com.ksacp2022t3.fanarapp.adapters.SpecialistListAdapter;
import com.ksacp2022t3.fanarapp.models.Request;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.List;

public class RequestsActivity extends AppCompatActivity {
    RecyclerView recycler_requests;
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        recycler_requests = findViewById(R.id.recycler_requests);
        btn_back = findViewById(R.id.btn_back);



        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

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

        firestore.collection("requests")
                .whereEqualTo("specialist_id",firebaseAuth.getUid())
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Request> requestList=queryDocumentSnapshots.toObjects(Request.class);

                        RequestsListAdapter adapter=new RequestsListAdapter(requestList,RequestsActivity.this);
                        recycler_requests.setAdapter(adapter);
                        progressDialog.dismiss();
                        see_all();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RequestsActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();

                    }
                });

    }

    private void see_all(){
        firestore.collection("requests")
                .whereEqualTo("to_notify",firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Request> new_requests=queryDocumentSnapshots.toObjects(Request.class);
                        WriteBatch batch=firestore.batch();

                        for(Request request:new_requests)
                        {
                            batch.update(firestore.collection("requests")
                                    .document(request.getId()),"to_notify","");
                        }
                        batch.commit();
                    }
                });
    }
}