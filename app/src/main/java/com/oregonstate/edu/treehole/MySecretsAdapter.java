package com.oregonstate.edu.treehole;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oregonstate.edu.treehole.data.UpdateSecret;
import com.oregonstate.edu.treehole.data.model.Secret;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MySecretsAdapter extends RecyclerView.Adapter<MySecretsAdapter.SecretsViewHolder> {

    private List<Secret> mSecretList;
    private List<String> mLikeList;
    private OnSecretTouchedListener mListener;

    public interface OnSecretTouchedListener {
        void onSecretTouched(Secret secret, boolean like);
    }

    public MySecretsAdapter(OnSecretTouchedListener listener) {
        mSecretList = new ArrayList<>();
        mLikeList = new ArrayList<>();
        mListener = listener;
    }

    public void updateSecretItems(List<Secret> secrets, List<String> likeList) {
        if (secrets != null) {
            mSecretList = secrets;
        }
        if (likeList != null) {
            mLikeList = likeList;
        }
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
        View itemView = inflater.inflate(R.layout.my_secret_item, parent, false);
        return new SecretsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SecretsViewHolder holder, int position) {
        holder.bind(mSecretList.get(position));
    }


    class SecretsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSecretContentTV;
        private TextView mSecretTimeTV;
        private TextView mReplyBT;
        private TextView mLikesBT;
        private ImageButton imageButton;

        public SecretsViewHolder(final View itemView) {
            super(itemView);
            mSecretContentTV = itemView.findViewById(R.id.tv_secret_content);
            mSecretTimeTV = itemView.findViewById(R.id.tv_secret_time);
            mReplyBT = itemView.findViewById(R.id.bt_comment);
            mLikesBT = itemView.findViewById(R.id.bt_like);
            imageButton = itemView.findViewById(R.id.ib_delete);
            mSecretContentTV.setOnClickListener(this);
            mReplyBT.setOnClickListener(this);
            mSecretTimeTV.setOnClickListener(this);
            mLikesBT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Secret secret = mSecretList.get(getAdapterPosition());
                    boolean likeFlag = mLikeList.contains(secret.secretId);
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    updateLike(view.getContext(), rootRef, likeFlag, secret);
                }
            });
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Secret secret = mSecretList.get(getAdapterPosition());
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    updateDelete(view.getContext(), rootRef, secret);

                }
            });


        }

        void bind(Secret secret) {
            mSecretContentTV.setText(secret.content);
            mSecretTimeTV.setText(new Date(secret.time).toString());
            String replyButtonString = mReplyBT.getContext().getString(
                    R.string.comment, secret.comments
            );

            String likesButtonString = mLikesBT.getContext().getString(
                    R.string.likes, secret.likes
            );

            Drawable heart_red = ContextCompat.getDrawable(mReplyBT.getContext(), R.drawable.ic_action_heart_red);
            Drawable heart = ContextCompat.getDrawable(mReplyBT.getContext(), R.drawable.ic_action_heart);
            heart.setBounds(0, 0, heart.getMinimumWidth(), heart.getMinimumHeight());
            heart_red.setBounds(0, 0, heart_red.getMinimumWidth(), heart_red.getMinimumHeight());

            mReplyBT.setText(replyButtonString);
            mLikesBT.setText(likesButtonString);
            if (mLikeList != null && mLikeList.contains(secret.secretId)) {
                mLikesBT.setCompoundDrawables(heart_red, null, null, null);
            } else {
                mLikesBT.setCompoundDrawables(heart, null, null, null);

            }

        }

        @Override
        public void onClick(View v) {
            Secret secret = mSecretList.get(getAdapterPosition());
            mListener.onSecretTouched(secret, mLikeList.contains(secret.secretId));
        }

        private void updateLike(Context context, DatabaseReference rootRef, boolean mLikeFlag, Secret mSecretItem) {

            try {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // reset replyTV
                mLikeFlag = !mLikeFlag;
                DatabaseReference likeRef = rootRef.child("likelist").child(userId);
                if (mLikeFlag) {
                    mLikeList.add(mSecretItem.secretId);
                    likeRef.child(mSecretItem.secretId).setValue(mSecretItem.secretId);
                    mSecretItem.likes = mSecretItem.likes + 1;
                } else {
                    likeRef.child(mSecretItem.secretId).removeValue();
                    mLikeList.remove(mSecretItem.secretId);
                    mSecretItem.likes = mSecretItem.likes - 1;
                }
                // update secret & user secret
                UpdateSecret.updateSecret(mSecretItem, rootRef);

            } catch (Exception e) {
                String toastString = "please log in";
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
            }

        }


        private void updateDelete(Context context, DatabaseReference rootRef, Secret mSecretItem) {

            try {
                // update secret & user secret
                DatabaseReference inSecRef = rootRef.child("secrets");
                String secretId = mSecretItem.secretId;
                inSecRef.child(secretId).removeValue();

                DatabaseReference usRef = rootRef.child("userSecrets");
                usRef.child(mSecretItem.userId).child(secretId).removeValue();

            } catch (Exception e) {
                String toastString = "please log in";
                Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
            }

        }
    }
}
