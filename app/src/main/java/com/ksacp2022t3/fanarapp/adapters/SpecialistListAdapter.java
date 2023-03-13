package com.ksacp2022t3.fanarapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.SpecialistProfileActivity;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.util.List;

public class SpecialistListAdapter extends RecyclerView.Adapter<SpecialistItem> {

    List<SpecialistAccount> accountList;
    Context context;

    public SpecialistListAdapter(List<SpecialistAccount> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
    }

    @NonNull
    @Override
    public SpecialistItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_specialist,parent,false);
        return new SpecialistItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialistItem holder, int position) {
            SpecialistAccount account=accountList.get(position);
            holder.txt_name.setText(account.getFirst_name()+" "+account.getLast_name());
            holder.txt_brief.setText(account.getBrief());
            holder.txt_comments.setText(String.valueOf(account.getComments_count()));
            holder.txt_likes.setText(String.valueOf(account.getLikes_count()));

            holder.account_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SpecialistProfileActivity. class);
                    intent.putExtra("specialist_id",account.getId());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }
}

class SpecialistItem extends RecyclerView.ViewHolder{

    TextView txt_name,txt_brief,txt_comments,txt_likes;
    LinearLayoutCompat account_card;

    public SpecialistItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_brief=itemView.findViewById(R.id.txt_brief);
        txt_comments=itemView.findViewById(R.id.txt_comments);
        txt_likes=itemView.findViewById(R.id.txt_likes);
        account_card=itemView.findViewById(R.id.account_card);
    }
}
