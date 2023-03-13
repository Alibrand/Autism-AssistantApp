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
import com.ksacp2022t3.fanarapp.adapters.ExercisesListAdapter;
import com.ksacp2022t3.fanarapp.models.Category;
import com.ksacp2022t3.fanarapp.models.Exercise;

import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    FloatingActionButton btn_add;
    ImageView btn_back,btn_settings;
    RecyclerView recycler_exercises;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    boolean settings_mode=false;

    List<Exercise> exercises;
    String category_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        btn_add = findViewById(R.id.btn_add);
        btn_back = findViewById(R.id.btn_back);
        btn_settings = findViewById(R.id.btn_settings);
        recycler_exercises = findViewById(R.id.recycler_exercises);


        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


         category_id=getIntent().getStringExtra("category_id");

        boolean setting_mode=getIntent().getBooleanExtra("setting_mode",false);
        if(setting_mode)
            btn_add.setVisibility(View.VISIBLE);
        else
            btn_add.setVisibility(View.GONE);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExercisesActivity.this,AddExerciseActivity. class);
                intent.putExtra("category_id",category_id);
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_mode=!settings_mode;
                if(settings_mode==true)
                {
                    Toast.makeText(ExercisesActivity.this,getString( R.string.mange_exercises ), Toast.LENGTH_LONG).show();
                    btn_add.setVisibility(View.VISIBLE);

                }
                else
                {
                    btn_add.setVisibility(View.GONE);
                }
                recycler_exercises.setAdapter(null);
                ExercisesListAdapter adapter=new ExercisesListAdapter(exercises,ExercisesActivity.this,category_id,settings_mode);
                recycler_exercises.setAdapter(adapter);

            }
        });

    }



    void load_exercises(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(firebaseAuth.getUid())
                .collection("categories")
                .document(category_id)
                .collection("exercises")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        exercises =queryDocumentSnapshots.toObjects(Exercise.class);
                        ExercisesListAdapter adapter=new ExercisesListAdapter(exercises,ExercisesActivity.this,category_id,settings_mode);
                        recycler_exercises.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ExercisesActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        settings_mode=false;
        load_exercises();
    }
}