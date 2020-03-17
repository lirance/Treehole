package com.oregonstate.edu.treehole;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oregonstate.edu.treehole.data.model.Reply;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.RepliesViewHolder> {

    private List<Reply> mReplyList;

    public RepliesAdapter() {
        mReplyList = new ArrayList<>();
    }

    public void updateRepliesItems(List<Reply> replies) {
        mReplyList = replies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mReplyList != null) {
            return mReplyList.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public RepliesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.secret_reply_list, parent, false);
        return new RepliesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RepliesViewHolder holder, int position) {
        holder.bind(mReplyList.get(position));
    }


    class RepliesViewHolder extends RecyclerView.ViewHolder {
        private TextView mSecretContentTV;
        private TextView mSecretTimeTV;

        public RepliesViewHolder(final View itemView) {
            super(itemView);
            mSecretContentTV = itemView.findViewById(R.id.tv_reply_message);
            mSecretTimeTV = itemView.findViewById(R.id.tv_reply_time);

        }

        void bind(Reply reply) {
            mSecretContentTV.setText(reply.message);
            mSecretTimeTV.setText(new Date(reply.time).toString());
        }

    }
}
