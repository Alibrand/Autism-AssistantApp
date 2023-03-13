package com.ksacp2022t3.fanarapp.adapters;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ksacp2022t3.fanarapp.ExercisesActivity;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.models.Category;
import com.ksacp2022t3.fanarapp.models.Exercise;
import com.ksacp2022t3.fanarapp.models.GlideApp;

import java.util.List;

public class CategoriesListAdapter extends  RecyclerView.Adapter<CategoryItem> {
    List<Category> categories;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    boolean setting_mode;

    public CategoriesListAdapter(List<Category> categories, Context context ,boolean setting_mode){
        this.categories = categories;
        this.context = context;
        this.setting_mode=setting_mode;
        firestore=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.deleting));
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public CategoryItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category,parent,false);
        return new CategoryItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItem holder, int position) {
        Category category=categories.get(position);
        holder.txt_category_name.setText(category.getName());

        StorageReference ref=storage.getReference();
        StorageReference image=ref.child("categories_images/"+category.getImage_name());

        GlideApp.with(context)
                .load(image)
                .centerCrop()
                .into(holder.category_image);

        if(!setting_mode )
        {
            holder.btn_delete.setVisibility(View.GONE);
        }
        else {
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
        if(category.isDefault_category())
            holder.btn_delete.setVisibility(View.GONE);


        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setMessage(R.string.category_delete_msg)
                        .setTitle(R.string.confirm)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.show();
                                WriteBatch batch= firestore.batch();
                                DocumentReference category_document=firestore.collection("accounts")
                                        .document(firebaseAuth.getUid())
                                        .collection("categories")
                                        .document(category.getId());
                                batch.delete(
                                        category_document);

                                category_document.collection("exercises")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<Exercise> exercises=queryDocumentSnapshots.toObjects(Exercise.class);
                                                for (Exercise exercise:exercises)
                                                {
                                                    batch.delete(category_document.collection("exercises")
                                                            .document(exercise.getId()));
                                                }

                                                batch.commit()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                image.delete();
                                                                progressDialog.dismiss();
                                                                categories.remove(category);
                                                                CategoriesListAdapter.this.notifyDataSetChanged();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.dismiss();
                                                                makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                                            }
                                        });


                            }
                        })
                        .setNegativeButton(R.string.cancel,null)
                        .show();
            }
        });

        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ExercisesActivity. class);
                intent.putExtra("setting_mode",setting_mode);
                intent.putExtra("category_id",category.getId());
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}

class CategoryItem extends RecyclerView.ViewHolder
{
    TextView txt_category_name;
    ImageView btn_delete,category_image;
    LinearLayoutCompat item_card;
    public CategoryItem(@NonNull View itemView) {
        super(itemView);
        txt_category_name=itemView.findViewById(R.id.txt_category_name);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        category_image=itemView.findViewById(R.id.category_image);
        item_card=itemView.findViewById(R.id.item_card);


    }
}
