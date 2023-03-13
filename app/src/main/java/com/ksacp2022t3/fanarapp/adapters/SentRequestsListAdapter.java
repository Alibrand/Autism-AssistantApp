package com.ksacp2022t3.fanarapp.adapters;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.ChatActivity;
import com.ksacp2022t3.fanarapp.PaymentActivity;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.models.Account;
import com.ksacp2022t3.fanarapp.models.Request;

import java.text.SimpleDateFormat;
import java.util.List;

public class SentRequestsListAdapter extends RecyclerView.Adapter<SentRequestItem> {

    List<Request> requestList;
    Context context;
    FirebaseAuth firebaseAuth;


    public SentRequestsListAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        firebaseAuth=FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public SentRequestItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sent_request,parent,false);
        return new SentRequestItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SentRequestItem holder, int position) {
            Request request=requestList.get(position);
            holder.txt_name.setText(request.getSpecialist_name());

            if(request.getTo_notify().equals(firebaseAuth.getUid()))
            {
                holder.txt_new_notify.setVisibility(View.VISIBLE);
                if(request.getStatus().equals("Accepted"))
                    holder.txt_new_notify.setText(R.string.accepted);
                else
                    holder.txt_new_notify.setText(R.string.rejected);
            }

            if(!request.getStatus().equals("Pending")) {
                holder.status.setVisibility(View.VISIBLE);
               if(request.getStatus().equals("Accepted"))
                   holder.status.setImageResource(R.drawable.ic_baseline_check_box_24);
               else if(request.getStatus().equals("Paid"))
                   holder.status.setImageResource(R.drawable.ic_baseline_paid_24);
               else
                   holder.status.setImageResource(R.drawable.ic_baseline_cancel_24);
            }

            if(request.getStatus().equals("Accepted"))
            {
                holder.btn_pay.setVisibility(View.VISIBLE);
            }

            FirebaseFirestore firestore=FirebaseFirestore.getInstance();

            firestore.collection("accounts")
                            .document(request.getSpecialist_id())
                                    .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Account account=documentSnapshot.toObject(Account.class);
                                                    holder.btn_call.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Intent intent=new Intent(Intent.ACTION_DIAL);
                                                            intent.setData(Uri.parse("tel:"+account.getPhone()));
                                                            context.startActivity(intent);

                                                        }
                                                    });

                                                }
                                            });


        holder.btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiver_name", request.getSpecialist_name());
                intent.putExtra("receiver_id", request.getSpecialist_id());
                context.startActivity(intent);

            }
        });

        holder.btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PaymentActivity.class);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                intent.putExtra("session_date", simpleDateFormat.format(request.getSession_date()));
                simpleDateFormat=new SimpleDateFormat("HH:mm a");
                intent.putExtra("session_time", simpleDateFormat.format(request.getSession_date()));
                intent.putExtra("request_id", request.getId());
                intent.putExtra("specialist_id", request.getSpecialist_id());
                intent.putExtra("specialist_name", request.getSpecialist_name());
                context.startActivity(intent);
            }
        });





    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

class SentRequestItem extends RecyclerView.ViewHolder{

    TextView txt_name,txt_new_notify;
    AppCompatButton btn_chat,btn_call,btn_pay;
    ImageView status;


    public SentRequestItem(@NonNull View itemView) {
        super(itemView);
        txt_new_notify=itemView.findViewById(R.id.txt_new_notify);
        txt_name=itemView.findViewById(R.id.txt_name);
        btn_call=itemView.findViewById(R.id.btn_call);
        btn_chat=itemView.findViewById(R.id.btn_chat);
        btn_pay=itemView.findViewById(R.id.btn_pay);
        status=itemView.findViewById(R.id.status);
    }
}
