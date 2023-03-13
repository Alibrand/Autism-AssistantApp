package com.ksacp2022t3.fanarapp;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

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
import com.ksacp2022t3.fanarapp.adapters.ChatsListAdapter;
import com.ksacp2022t3.fanarapp.models.Chat;

import java.util.List;

public class InboxActivity extends AppCompatActivity {
    RecyclerView recycler_chats;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        recycler_chats = findViewById(R.id.recycler_chats);
        btn_back = findViewById(R.id.btn_back);




        firebaseAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        progressDialog =new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(R.string.loading);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));







    }

    private void load_chats() {
        progressDialog.show();
        String uid=firebaseAuth.getUid();
        firestore.collection("inbox")
                .whereArrayContains("users_ids",uid)
                .orderBy("last_update", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        List<Chat> chatList=queryDocumentSnapshots.toObjects(Chat.class);

                        if(chatList.size()==0)
                        {
                            makeText(InboxActivity.this, R.string.inbox_empty , LENGTH_LONG).show();
                        }

                        ChatsListAdapter adapter=new ChatsListAdapter(chatList,InboxActivity.this);
                        recycler_chats.setAdapter(adapter);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeText(InboxActivity.this,"Failed to load chats" , LENGTH_LONG).show();

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load_chats();
    }
}