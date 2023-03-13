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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ksacp2022t3.fanarapp.ChatActivity;
import com.ksacp2022t3.fanarapp.ParentProfileActivity;
import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.SpecialistProfileActivity;
import com.ksacp2022t3.fanarapp.models.Account;
import com.ksacp2022t3.fanarapp.models.Request;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.text.SimpleDateFormat;
import java.util.List;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestItem> {

    List<Request> requestList;
    Context context;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;

    public RequestsListAdapter(List<Request> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;

        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.processing));
        progressDialog.setMessage(context.getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public RequestItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request,parent,false);
        return new RequestItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItem holder, int position) {
            Request request=requestList.get(position);
            holder.txt_name.setText(request.getSender_name());
            holder.txt_state_description.setText(request.getSender_state());
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm a");
            String dateMsg=context.getString(R.string.session_date)+":"+simpleDateFormat.format(request.getSession_date());
            String timeMsg=context.getString(R.string.session_time)+":"+timeFormat.format(request.getSession_date());
            String session_appointment=dateMsg+"\n"+timeMsg;
            holder.txt_date.setText(session_appointment);


            if(!request.getStatus().equals("Pending")) {
                holder.status.setVisibility(View.VISIBLE);
               if(request.getStatus().equals("Accepted"))
                   holder.status.setImageResource(R.drawable.ic_baseline_check_box_24);
               else if(request.getStatus().equals("Paid"))
                   holder.status.setImageResource(R.drawable.ic_baseline_paid_24);
               else
                   holder.status.setImageResource(R.drawable.ic_baseline_cancel_24);
            }

            if(request.getStatus().equals("Paid"))
            {
                holder.layout_contact.setVisibility(View.VISIBLE);
                holder.layout_control.setVisibility(View.GONE);
            }

            if(request.getTo_notify().equals(FirebaseAuth.getInstance().getUid()))
            {
                holder.txt_new_notify.setVisibility(View.VISIBLE);
                if(request.getStatus().equals("Pending"))
                    holder.txt_new_notify.setText(R.string.new_req);
                else if (request.getStatus().equals("Paid"))
                    holder.txt_new_notify.setText(R.string.paid);
            }



            holder.txt_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ParentProfileActivity. class);
                    intent.putExtra("id",request.getSender_id());
                    context.startActivity(intent);
                }
            });

            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.show();
                    firestore.collection("requests")
                            .document(request.getId())
                            .update("status","Accepted",
                                    "to_notify",request.getSender_id())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    makeText(context, R.string.request_updated, LENGTH_LONG).show();
                                    request.setStatus("Accepted");
                                    RequestsListAdapter.this.notifyDataSetChanged();
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

        holder.btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                firestore.collection("requests")
                        .document(request.getId())
                        .update("status","Rejected",
                                "to_notify",request.getSender_id())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                makeText(context, R.string.request_updated, LENGTH_LONG).show();
                                request.setStatus("Rejected");
                                RequestsListAdapter.this.notifyDataSetChanged();
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

        firestore.collection("accounts")
                .document(request.getSender_id())
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





    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

class RequestItem extends RecyclerView.ViewHolder{

    TextView txt_name,txt_state_description,txt_date,txt_new_notify;
    AppCompatButton btn_accept,btn_reject,btn_call,btn_chat;
    ImageView status;
    LinearLayoutCompat layout_contact,layout_control;



    public RequestItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_state_description=itemView.findViewById(R.id.txt_state_description);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_new_notify=itemView.findViewById(R.id.txt_new_notify);
        btn_accept=itemView.findViewById(R.id.btn_accept);
        btn_reject=itemView.findViewById(R.id.btn_reject);
        btn_call=itemView.findViewById(R.id.btn_call);
        btn_chat=itemView.findViewById(R.id.btn_chat);
        status=itemView.findViewById(R.id.status);
        layout_contact=itemView.findViewById(R.id.layout_contact);
        layout_control=itemView.findViewById(R.id.layout_control);
    }
}
