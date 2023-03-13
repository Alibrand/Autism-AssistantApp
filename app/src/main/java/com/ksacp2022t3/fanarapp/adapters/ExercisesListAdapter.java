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
import com.ksacp2022t3.fanarapp.PlayExerciseActivity;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.models.Category;
import com.ksacp2022t3.fanarapp.models.Exercise;
import com.ksacp2022t3.fanarapp.models.GlideApp;

import java.io.Serializable;
import java.util.List;

public class ExercisesListAdapter extends  RecyclerView.Adapter<ExerciseItem> {
    List<Exercise> exercises;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    String category_id;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    boolean setting_mode;

    public ExercisesListAdapter(List<Exercise> exercises, Context context,String  category_id,boolean setting_mode){
        this.exercises = exercises;
        this.context = context;
        this.setting_mode=setting_mode;
        firestore=FirebaseFirestore.getInstance();
        this.category_id=category_id;
        firebaseAuth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.deleting));
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public ExerciseItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise,parent,false);
        return new ExerciseItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseItem holder, int position) {
        Exercise exercise=exercises.get(position);
        holder.txt_exercise.setText(context.getString(R.string.exercise)+(position+1));



        if(!setting_mode)
        {
            holder.btn_delete.setVisibility(View.GONE);
        }
        else {
            holder.btn_delete.setVisibility(View.VISIBLE);
        }
        if(exercise.isDefault_exercise())
            holder.btn_delete.setVisibility(View.GONE);


        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("accounts")
                        .document(firebaseAuth.getUid())
                        .collection("categories")
                        .document(category_id)
                        .collection("exercises")
                        .document(exercise.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                progressDialog.dismiss();
                                exercises.remove(exercise);
                                ExercisesListAdapter.this.notifyDataSetChanged();
                                StorageReference reference=storage.getReference();
                                StorageReference audio=reference.child("exercises_audios/"+exercise.getAudio_name());
                                audio.delete();
                                StorageReference image1=reference.child("exercises_images/"+exercise.getOption1_image());
                                image1.delete();
                                StorageReference image2=reference.child("exercises_images/"+exercise.getOption2_image());
                                image2.delete();
                                StorageReference image3=reference.child("exercises_images/"+exercise.getOption3_image());
                                image3.delete();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                makeText(context,"Error :"+e.getMessage() , LENGTH_LONG).show();
                            }
                        });
            }
        });

        holder.item_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, PlayExerciseActivity. class);
                intent.putExtra("category_id",category_id);
                intent.putExtra("exercise_id",exercise.getId());
                intent.putExtra("exercises", (Serializable) exercises);
                intent.putExtra("index", position);
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
}

class ExerciseItem extends RecyclerView.ViewHolder
{
    TextView txt_exercise;
    ImageView btn_delete;
    LinearLayoutCompat item_card;
    public ExerciseItem(@NonNull View itemView) {
        super(itemView);
        txt_exercise=itemView.findViewById(R.id.txt_exercise);
        btn_delete=itemView.findViewById(R.id.btn_delete);
        item_card=itemView.findViewById(R.id.item_card);


    }
}
