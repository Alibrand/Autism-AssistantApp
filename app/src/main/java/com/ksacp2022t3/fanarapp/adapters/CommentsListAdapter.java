package com.ksacp2022t3.fanarapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ksacp2022t3.fanarapp.R;
import com.ksacp2022t3.fanarapp.SpecialistProfileActivity;
import com.ksacp2022t3.fanarapp.models.Comment;
import com.ksacp2022t3.fanarapp.models.SpecialistAccount;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentItem> {

    List<Comment> comments;
    Context context;

    public CommentsListAdapter(List<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment,parent,false);
        return new CommentItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentItem holder, int position) {
            Comment comment=comments.get(position);
            holder.txt_name.setText(comment.getName());
        holder.txt_comment.setText(comment.getComment());
        long now = System.currentTimeMillis();
        String time=DateUtils.getRelativeTimeSpanString(comment.getCreated_at().getTime(), now, DateUtils.DAY_IN_MILLIS).toString();
        holder.txt_date.setText(time);



    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}

class CommentItem extends RecyclerView.ViewHolder{

    TextView txt_name,txt_date,txt_comment;


    public CommentItem(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_comment=itemView.findViewById(R.id.txt_comment);

    }
}
