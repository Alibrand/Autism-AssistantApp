package com.ksacp2022t3.fanarapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.models.Message;



import java.text.SimpleDateFormat;
import java.util.List;

public class MessagesListAdapter extends RecyclerView.Adapter<MessageItem> {
    List<Message> messageList;
    Context context;
    FirebaseAuth firebaseAuth;

    public MessagesListAdapter(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message,parent,false);
        return new MessageItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageItem holder, int position) {
        Message message= messageList.get(position);
        String uid= firebaseAuth.getUid();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String dateString=message.getCreated_at()==null?"now":simpleDateFormat.format(message.getCreated_at());


        //if the current user is the sender
        if(message.getFrom().equals(uid))
        {

                holder.receiver_message.setVisibility(View.GONE);
                holder.txt_sender_message.setText(message.getText());
                holder.txt_send_date.setText(dateString);

        }
        else{

            holder.sender_message.setVisibility(View.GONE);
            holder.txt_receiver_message.setText(message.getText());
            holder.txt_receiver_date.setText(dateString);


        }




    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

class MessageItem extends RecyclerView.ViewHolder{
    TextView txt_sender_message,txt_receiver_message,
    txt_send_date,txt_receiver_date;
    LinearLayoutCompat sender_message,receiver_message;

    public MessageItem(@NonNull View itemView) {
        super(itemView);
        sender_message=itemView.findViewById(R.id.sender_message);
        receiver_message=itemView.findViewById(R.id.receiver_message);
        txt_sender_message=itemView.findViewById(R.id.txt_sender_message);
        txt_receiver_message=itemView.findViewById(R.id.txt_receiver_message);
        txt_send_date=itemView.findViewById(R.id.txt_send_date);
        txt_receiver_date=itemView.findViewById(R.id.txt_receiver_date);
    }
}
