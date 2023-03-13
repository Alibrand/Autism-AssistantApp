package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.adapters.CommentsListAdapter;
import com.ksacp2022t3.fanarapp.models.Comment;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.List;

public class SpecialistProfileActivity extends AppCompatActivity {

    TextView txt_full_name,txt_brief,txt_degree,txt_experience,
    txt_comments_count,txt_likes_count;
    ImageView btn_request,btn_back,btn_like;
    EditText txt_comment;
    AppCompatButton btn_call,btn_send;
    RecyclerView recycler_comments;

    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;

    boolean liked=false;


    String specialist_id;
    String uid;

    SpecialistAccount account;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialist_profile);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_brief = findViewById(R.id.txt_brief);
        txt_degree = findViewById(R.id.txt_degree);
        txt_experience = findViewById(R.id.txt_experience);
        btn_request = findViewById(R.id.btn_request);
        btn_back = findViewById(R.id.btn_back);
        btn_call = findViewById(R.id.btn_call);
        txt_comments_count = findViewById(R.id.txt_comments_count);
        txt_likes_count = findViewById(R.id.txt_likes_count);
        btn_like = findViewById(R.id.btn_like);
        recycler_comments = findViewById(R.id.recycler_comments);
        txt_comment = findViewById(R.id.txt_comment);
        btn_send = findViewById(R.id.btn_send);





         specialist_id=getIntent().getStringExtra("specialist_id");

        firestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();

        uid=firebaseAuth.getUid();

        progressDialog=new ProgressDialog(this);
                progressDialog.setTitle(R.string.loading);
                progressDialog.setMessage(getResources().getString(R.string.please_wait));
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);


        load_info();


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(liked)
                {

                 firestore.collection("accounts")
                         .document(specialist_id)
                         .update("liked_users", FieldValue.arrayRemove(uid),
                                 "likes_count",FieldValue.increment(-1))
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void unused) {
                                 btn_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                 liked=false;
                                 account.setLikes_count(account.getLikes_count()-1);
                                 txt_likes_count.setText(String.valueOf(account.getLikes_count()));
                             }
                         });
                }
                else{
                    firestore.collection("accounts")
                            .document(specialist_id)
                            .update("liked_users", FieldValue.arrayUnion(uid),
                                    "likes_count",FieldValue.increment(1))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    btn_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    liked=true;
                                    account.setLikes_count(account.getLikes_count()+1);
                                    txt_likes_count.setText(String.valueOf(account.getLikes_count()));
                                }
                            });
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=txt_comment.getText().toString();

                if(text.isEmpty())
                {
                    return;
                }

                progressDialog.setTitle(R.string.sending);

                progressDialog.show();

                Comment comment=new Comment();
                comment.setProfile_id(specialist_id);
                comment.setUid(firebaseAuth.getUid());
                comment.setComment(text);
                SharedPreferences sharedPreferences=SpecialistProfileActivity.this.getSharedPreferences("fanar",MODE_PRIVATE);
                comment.setName(sharedPreferences.getString("full_name",""));
                DocumentReference doc=firestore.collection("accounts")
                        .document(specialist_id)
                        .collection("comments")
                        .document();
                comment.setId(doc.getId());
                doc.set(comment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                txt_comment.setText("");

                                load_info();

                                firestore.collection("accounts")
                                        .document(specialist_id)
                                        .update("comments_count",FieldValue.increment(1));

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(SpecialistProfileActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                            }
                        });




            }
        });









    }



    void load_info(){
        progressDialog.show();
        firestore.collection("accounts")
                .document(specialist_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.dismiss();
                        account=documentSnapshot.toObject(SpecialistAccount.class);
                        txt_full_name.setText(account.getFirst_name()+" "+account.getLast_name());
                        txt_brief.setText(account.getBrief());
                        txt_degree.setText(account.getDegree());
                        txt_experience.setText(account.getExperience_years());
                        txt_comments_count.setText(String.valueOf(account.getComments_count()));
                        txt_likes_count.setText(String.valueOf(account.getLikes_count()));
                        btn_call.setText(String.valueOf(account.getPhone()));

                        btn_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(SpecialistProfileActivity.this,CalendarActivity. class);
                                intent.putExtra("specialist_id",specialist_id);
                                intent.putExtra("specialist_name",account.getFirst_name()+" "+account.getLast_name());
                                startActivity(intent);
                            }
                        });


                        liked=account.getLiked_users().contains(uid);
                        if(liked)
                            btn_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                        else
                            btn_like.setImageResource(R.drawable.ic_baseline_favorite_border_24);

                        btn_call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+account.getPhone()));
                                startActivity(intent);
                            }
                        });

                        load_comments();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                         Toast.makeText(SpecialistProfileActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                         finish();
                    }
                });
    }

    void load_comments(){
        progressDialog.setTitle(R.string.loading);
        progressDialog.show();
        firestore.collection("accounts")
                .document(specialist_id)
                .collection("comments")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Comment> comments=queryDocumentSnapshots.toObjects(Comment.class);
                        CommentsListAdapter adapter=new CommentsListAdapter(comments,SpecialistProfileActivity.this);
                        recycler_comments.setAdapter(adapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SpecialistProfileActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });

    }



}