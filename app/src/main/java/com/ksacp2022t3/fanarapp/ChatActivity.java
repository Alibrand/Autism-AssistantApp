package com.ksacp2022t3.fanarapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.adapters.MessagesListAdapter;
import com.ksacp2022t3.fanarapp.models.Account;
import com.ksacp2022t3.fanarapp.models.Chat;
import com.ksacp2022t3.fanarapp.models.Message;

import java.util.Arrays;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recycler_messages;
    ImageView btn_back,profile_image;
    AppCompatButton btn_send  ;
    EditText txt_message;
    TextView txt_name;

    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    ProgressBar progress;

    ListenerRegistration messagesListener;

    List<Message> messagesList;
    String chat_id, sender_id, receiver_id, receiver_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recycler_messages = findViewById(R.id.recycler_messages);
        btn_back = findViewById(R.id.btn_back);
        txt_name = findViewById(R.id.txt_name);
        btn_send = findViewById(R.id.btn_send);
        txt_message = findViewById(R.id.txt_message);
        progress = findViewById(R.id.progress);
        profile_image = findViewById(R.id.profile_image);





        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        sender_id = firebaseAuth.getUid();
        receiver_name = getIntent().getStringExtra("receiver_name");
        receiver_id = getIntent().getStringExtra("receiver_id");












        //if this is a new chat with this user
        //create a unique id for this chat
        int compare=sender_id.compareTo(receiver_id);
        if (getIntent().getStringExtra("chat_id") == null)
            if(compare<0)
                chat_id = sender_id + "_" + receiver_id;
            else
                chat_id = receiver_id + "_" + sender_id;
        else
            chat_id = getIntent().getStringExtra("chat_id");


        //check if chat not exists crate it


        //set the other user name
        txt_name.setText(receiver_name);

        create_chat();


        //listening to every change in  chat's messages
        //adding a snapshot listener
        messagesListener = firestore.collection("inbox")
                .document(chat_id)
                .collection("messages")
                .orderBy("created_at", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messagesList = value.toObjects(Message.class);


                        update_messages_status();

                        MessagesListAdapter adapter = new MessagesListAdapter(messagesList, ChatActivity.this);
                        recycler_messages.setAdapter(adapter);
                        recycler_messages.scrollToPosition(messagesList.size() - 1);


                    }
                });

        txt_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    send_message();
                }
                return false;
            }
        });


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progress.setIndeterminate(true);

                send_message( );
            }
        });


    }

    private void send_message( ) {
        String text = txt_message.getText().toString();
        if (text.isEmpty())
            return;
        txt_message.setText("");
        Message message = new Message();
        message.setText(text);
        message.setTo(receiver_id);
        message.setFrom(sender_id);
        message.setCreated_at(null);


        DocumentReference doc=firestore.collection("inbox")
                .document(chat_id)
                .collection("messages")
                .document();
        message.setId(doc.getId());
        doc.set(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progress.setIndeterminate(false);
                        //update chat last_update
                        firestore.collection("inbox")
                                .document(chat_id)
                                .update("last_update", FieldValue.serverTimestamp());
                    }
                })   .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.setIndeterminate(false);
                        Toast.makeText(ChatActivity.this,"Error :"+e.getMessage() , Toast.LENGTH_LONG).show();
                    }
                }) ;


    }

    void create_chat(){

        firestore.collection("inbox")
                .document(chat_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists())
                        {
                            //get current user name (sender name)
                            //read stored user data from shared preferences
                            SharedPreferences sharedPref = ChatActivity.this.getSharedPreferences("fanar", Context.MODE_PRIVATE);
                            String sender_name = sharedPref.getString("full_name", "");

                            Chat chat = new Chat();
                            chat.setLast_update(null);
                            List<String> users_ids = Arrays.asList(sender_id, receiver_id);
                            List<String> users_names = Arrays.asList(sender_name, receiver_name);
                            chat.setUsers_ids(users_ids);
                            chat.setUsers_names(users_names);
                            chat.setId(chat_id);

                            firestore.collection("inbox")
                                    .document(chat_id)
                                    .set(chat)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChatActivity.this, "Failed to create chat", Toast.LENGTH_LONG).show();
                                            progress.setIndeterminate(false);
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ChatActivity.this,"Error failed to create chat:"+e.getMessage() , Toast.LENGTH_LONG).show();
                        finish();
                    }
                });


    }

    private void update_messages_status() {
        firestore.collection("inbox")
                .document(chat_id)
                .collection("messages")
                .whereEqualTo("to", sender_id)
                .whereEqualTo("status", "unseen")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            //update messages status
                            firestore.collection("inbox")
                                    .document(chat_id)
                                    .collection("messages")
                                    .document(doc.getId())
                                    .update("status", "seen");
                        }
                    }
                })
        ;
    }

    @Override
    protected void onDestroy() {
        messagesListener.remove();
        super.onDestroy();

    }
}