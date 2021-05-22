package com.example.sharingbookshelf.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharingbookshelf.Models.MailData;
import com.example.sharingbookshelf.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MailboxAdapter extends RecyclerView.Adapter<MailboxAdapter.ViewHolder> {

    private ArrayList<MailData> mailList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_profileImage;
        private final TextView tv_nickname;
        private final TextView tv_mailtext;
        private final TextView tv_time;

        public ViewHolder(@NonNull View view) {
            super(view);
            iv_profileImage = view.findViewById(R.id.iv_profileImg);
            tv_nickname = view.findViewById(R.id.tv_nickname);
            tv_mailtext = view.findViewById(R.id.tv_mailtext);
            tv_time = view.findViewById(R.id.tv_time);
        }
    }

    public MailboxAdapter(ArrayList<MailData> dataSet) {
        mailList = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_mail, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MailData mailData = mailList.get(position);
        Glide
                .with(viewHolder.iv_profileImage.getContext())
                .load(mailData.getProfileImage())
                .fitCenter()
                .placeholder(R.drawable.icon_logo)
                .into(viewHolder.iv_profileImage);

        viewHolder.tv_nickname.setText(mailData.getUserName());
        viewHolder.tv_mailtext.setText(mailData.getLastMessage());
        viewHolder.tv_time.setText(mailData.getMessageTime());
    }

    @Override
    public int getItemCount() {
        return (null != mailList ? mailList.size() : 0);
    }
}