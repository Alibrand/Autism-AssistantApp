package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksacp2022t3.fanarapp.models.Category;
import com.ksacp2022t3.fanarapp.models.GlideApp;

import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    EditText txt_category_name;
    ImageView category_image,btn_back;
    AppCompatButton btn_save;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    ProgressDialog progressDialog;

    Uri selected_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        txt_category_name = findViewById(R.id.txt_category_name);
        category_image = findViewById(R.id.category_image);
        btn_back = findViewById(R.id.btn_back);
        btn_save = findViewById(R.id.btn_save);

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle(R.string.processing);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 110);

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_txt_category_name =txt_category_name.getText().toString();


                if(str_txt_category_name.isEmpty())
                {
                     txt_category_name.setError( getString(R.string.required_field));
                     return;
                }

                if(selected_image==null)
                {
                    makeText(AddCategoryActivity.this,getString(R.string.you_should_select)  , LENGTH_LONG).show();
                    return;
                }


                progressDialog.show();


                String image_name= UUID.randomUUID().toString()+".jpg";


                StorageReference ref=storage.getReference();
                StorageReference image_path=ref.child("categories_images/"+image_name);


                image_path.putFile(selected_image)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Category category=new Category();
                                category.setImage_name(image_name);
                                category.setName(str_txt_category_name);
                                DocumentReference doc=firestore.collection("accounts")
                                        .document(firebaseAuth.getUid())
                                        .collection("categories")
                                        .document();
                                category.setId(doc.getId());
                                doc.set(category)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                makeText(AddCategoryActivity.this, R.string.added_successfully , LENGTH_LONG).show();
                                            finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(AddCategoryActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(AddCategoryActivity.this,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });




            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            selected_image = data.getData();

            GlideApp.with(AddCategoryActivity.this)
                    .load(selected_image)
                    .centerCrop()
                    .into(category_image);
        }
    }
}