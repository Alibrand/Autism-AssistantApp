package com.ksacp2022t3.fanarapp.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksacp2022t3.fanarapp.ChatActivity;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.models.Chat;



import java.text.SimpleDateFormat;
import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatItem> {
    List<Chat> chatList;
    Context context;
    FirebaseAuth firebaseAuth;


    public ChatsListAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();


    }





    @NonNull
    @Override
    public ChatItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat,parent,false);
        return new ChatItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatItem holder, int position) {
        Chat chat= chatList.get(position);
        String uid= firebaseAuth.getUid();
        //determine the other participant
        List<String> chat_users_ids=chat.getUsers_ids();
        List<String> chat_users_names=chat.getUsers_names();
        String receiver_name;
        String receiver_id;
        int indx=chat_users_ids.indexOf(uid);
        //the index should be either 0 or 1 only
        if(indx==0) {
            receiver_name = chat_users_names.get(1);
            receiver_id=chat_users_ids.get(1);
        }
        else {
            receiver_name = chat_users_names.get(0);
            receiver_id=chat_users_ids.get(0);

        }



        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm aa");
        holder.time.setText(sdf.format(chat.getLast_update()) );
        holder.name.setText(receiver_name);

        holder.chat_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("chat_id", chat.getId());
                    intent.putExtra("receiver_name", receiver_name);
                    intent.putExtra("receiver_id", receiver_id);
                    context.startActivity(intent);

            }
        });
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("inbox")
                .document(chat.getId())
                .collection("messages")
                .whereEqualTo("to",firebaseAuth.getUid())
                .whereEqualTo("status","unseen")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int new_messages_count= queryDocumentSnapshots.getDocuments().size();
                                            if(new_messages_count>0)
                                            {
                                                holder.txt_new_messages.setVisibility(View.VISIBLE);
                                                holder.txt_new_messages.setText(String.valueOf(new_messages_count));
                                                return;
                                            }
                    }
                });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

class ChatItem extends RecyclerView.ViewHolder{
    TextView time,name,txt_new_messages;
    LinearLayoutCompat chat_card;
    ImageView profile_image;

    public ChatItem(@NonNull View itemView) {
        super(itemView);
        time=itemView.findViewById(R.id.time);
        name=itemView.findViewById(R.id.name);
        profile_image=itemView.findViewById(R.id.profile_image);
        chat_card=itemView.findViewById(R.id.chat_card);
        txt_new_messages=itemView.findViewById(R.id.txt_new_messages);
    }
}
