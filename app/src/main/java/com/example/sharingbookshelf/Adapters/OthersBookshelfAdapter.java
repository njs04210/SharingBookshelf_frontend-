package com.example.sharingbookshelf.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sharingbookshelf.Fragments.OtherBookshelfFragment;
import com.example.sharingbookshelf.Fragments.RankingBookInfoPopupFragment;
import com.example.sharingbookshelf.Fragments.UserinfoFragment;
import com.example.sharingbookshelf.Models.BookData;
import com.example.sharingbookshelf.Models.OtherBookshelfResponse;
import com.example.sharingbookshelf.Models.RankingData;
import com.example.sharingbookshelf.R;

import java.util.ArrayList;

public class OthersBookshelfAdapter extends RecyclerView.Adapter<OthersBookshelfAdapter.ViewHolder> {

    private ArrayList<OtherBookshelfResponse.OtherShelfData> bookshelfList;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView mRecyclerView;
        private final ImageView iv_profileImg;
        private final TextView tv_nickname;
        private final TextView tv_kidsInfo;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_bookshelf);
            this.iv_profileImg = (ImageView) view.findViewById(R.id.iv_profileImg);
            this.tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
            this.tv_kidsInfo = (TextView) view.findViewById(R.id.tv_kidsInfo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        OtherBookshelfResponse.OtherShelfData otherShelfData = bookshelfList.get(pos);

                        Bundle bundle = new Bundle();
                        bundle.putInt("mem_id", otherShelfData.getUser().getMem_id());
                        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();

                        UserinfoFragment userinfoFragment = new UserinfoFragment
                                (((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag("OtherBookshelfFragment"));
                        userinfoFragment.setArguments(bundle);
                        userinfoFragment.show(fm, "UserinfoFragment");

                    }
                }
            });
        }
    }

    public OthersBookshelfAdapter(Context context, ArrayList<OtherBookshelfResponse.OtherShelfData> dataSet) {
        this.bookshelfList = dataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookshelf, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        OtherBookshelfResponse.OtherShelfData bookshelfInfoData = bookshelfList.get(position);

        Glide
                .with(viewHolder.iv_profileImg.getContext())
                .load(bookshelfInfoData.getUser().getPhotoURL())
                .fitCenter()
                .placeholder(R.drawable.icon_logo)
                .into(viewHolder.iv_profileImg);
        viewHolder.tv_nickname.setText(bookshelfInfoData.getUser().getNickname());
        String sex = bookshelfInfoData.getKids().getSex() == 1 ? "남자" : "여자";
        viewHolder.tv_kidsInfo.setText("(" + bookshelfInfoData.getKids().getAge() + "세 / " + sex + ")");

        ArrayList<BookData> bookList = bookshelfInfoData.getHasBookList();
        setBooksinShelfView(bookList, viewHolder);
    }

    private void setBooksinShelfView(ArrayList<BookData> bookList, ViewHolder viewHolder) {
        BooksAdapter booksAdapter = new BooksAdapter(bookList);
        viewHolder.mRecyclerView.setAdapter(booksAdapter);
        viewHolder.mRecyclerView.setHasFixedSize(true);
        viewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.HORIZONTAL, false));
    }


    @Override
    public int getItemCount() {
        return (null != bookshelfList ? bookshelfList.size() : 0);
    }
}
