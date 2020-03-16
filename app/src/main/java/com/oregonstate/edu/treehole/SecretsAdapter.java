package com.oregonstate.edu.treehole;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oregonstate.edu.treehole.data.model.Secret;

public class SecretsAdapter extends RecyclerView.Adapter<SecretsAdapter.SecretsViewHolder> {

    private List<Secret> mSecretList;
    private OnSecretTouchedListener mListener;

    public interface OnSecretTouchedListener {
        void onSecretTouched(Secret secret);
    }

    public SecretsAdapter(OnSecretTouchedListener listener) {
        mSecretList = new ArrayList<>();
        mListener = listener;
    }

    public void updateSecretItems(List<Secret> secrets) {
        mSecretList = secrets;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSecretList != null) {
            return mSecretList.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SecretsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.post_list_item, parent, false);
        return new SecretsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SecretsViewHolder holder, int position) {
        holder.bind(mSecretList.get(position));
    }


    class SecretsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSecretContentTV;
        private TextView mSecretTimeTV;
        private Button mReplyBT;
        private Button mLikesBT;

        public SecretsViewHolder(final View itemView) {
            super(itemView);
            mSecretContentTV = itemView.findViewById(R.id.tv_secret_content);
            mSecretTimeTV = itemView.findViewById(R.id.tv_secret_time);
            mReplyBT = itemView.findViewById(R.id.bt_comment);
            mLikesBT = itemView.findViewById(R.id.bt_like);
            itemView.setOnClickListener(this);

        }

        void bind(Secret secret) {
            mSecretContentTV.setText(secret.content);
            mSecretTimeTV.setText(secret.time.toString());

            String replyButtonString = mReplyBT.getContext().getString(
                    R.string.comment, secret.comments
            );

            String likesButtonString = mLikesBT.getContext().getString(
                    R.string.likes, secret.likes
            );
            mReplyBT.setText(replyButtonString);
            mLikesBT.setText(likesButtonString);
        }

        @Override
        public void onClick(View v) {
            Secret secret = mSecretList.get(getAdapterPosition());
            mListener.onSecretTouched(secret);
        }
    }
}
