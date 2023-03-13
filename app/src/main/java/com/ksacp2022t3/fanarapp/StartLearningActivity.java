package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.adapters.CategoriesListAdapter;
import com.ksacp2022t3.fanarapp.models.Category;

import java.util.List;

public class StartLearningActivity extends AppCompatActivity {
    ImageView btn_back,btn_settings;
    RecyclerView recycler_categories;
    FloatingActionButton btn_add;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    boolean settings_mode=false;

    List<Category> categories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_learning);
        recycler_categories = findViewById(R.id.recycler_categories);
        btn_settings = findViewById(R.id.btn_settings);
        btn_back = findViewById(R.id.btn_back);
        btn_add = findViewById(R.id.btn_add);





        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               settings_mode=!settings_mode;
               if(settings_mode==true)
               {
                   Toast.makeText(StartLearningActivity.this, getString(R.string.mange_categories) , Toast.LENGTH_LONG).show();
                   btn_add.setVisibility(View.VISIBLE);

               }
               else
               {
                   btn_add.setVisibility(View.GONE);

               }
                recycler_categories.setAdapter(null);
                CategoriesListAdapter adapter=new CategoriesListAdapter(categories,StartLearningActivity.this,settings_mode);
                recycler_categories.setAdapter(adapter);

            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartLearningActivity.this,AddCategoryActivity. class);
                startActivity(intent);
            }
        });




    }

    void load_categories(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .collection("categories")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        categories=queryDocumentSnapshots.toObjects(Category.class);
                        CategoriesListAdapter adapter=new CategoriesListAdapter(categories,StartLearningActivity.this,settings_mode);
                        recycler_categories.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(StartLearningActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings_mode=false;
        load_categories();
    }
}