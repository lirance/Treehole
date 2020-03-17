package com.oregonstate.edu.treehole;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oregonstate.edu.treehole.data.UpdateSecret;
import com.oregonstate.edu.treehole.data.model.Reply;
import com.oregonstate.edu.treehole.data.model.Secret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SecretDetailActivity extends AppCompatActivity {

    private static final String TAG = SecretDetailActivity.class.getSimpleName();
    private Secret mSecretItem;
    private boolean mLikeFlag;
    private TextView mSecretContentTV;
    private TextView mSecretTimeTV;

    //    private ImageView
    private TextView mReplyTV;
    private TextView mLikesTV;

    private ImageButton mSendCommentIBT;

    private FirebaseAuth mAuth;
    private RepliesAdapter repliesAdapter;
    private RecyclerView mRepliesRV;
    private EditText mComment;
    final private FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_item_detail);
        mAuth = FirebaseAuth.getInstance();

        mSecretContentTV = findViewById(R.id.tv_secret_content_dt);
        mSecretTimeTV = findViewById(R.id.tv_secret_time_dt);

        mReplyTV = findViewById(R.id.bt_comment_dt);
        mLikesTV = findViewById(R.id.bt_like_dt);

        mComment = findViewById(R.id.et_comment_add);

        mRepliesRV = findViewById(R.id.rv_reply_item);
        repliesAdapter = new RepliesAdapter();
        mRepliesRV.setAdapter(repliesAdapter);
        mRepliesRV.setLayoutManager(new LinearLayoutManager(this));
        mRepliesRV.setHasFixedSize(true);

        mSendCommentIBT = findViewById(R.id.bt_send_cm);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("Secret")) {
            mSecretItem = (Secret) intent.getSerializableExtra("Secret");
        }

        if (intent != null && intent.hasExtra("LikeFlag")) {
            mLikeFlag = (boolean) intent.getSerializableExtra("LikeFlag");
        }

        if (mSecretItem == null) {
            Log.d(TAG, "no secret Item detected");
            finish();
        }
        mSecretContentTV.setText(mSecretItem.content);
        mSecretTimeTV.setText(new Date(mSecretItem.time).toString());

        String replyButtonString = mReplyTV.getContext().getString(
                R.string.comment, mSecretItem.comments
        );

        String likesButtonString = mLikesTV.getContext().getString(
                R.string.likes, mSecretItem.likes
        );
        mReplyTV.setText(replyButtonString);
        mLikesTV.setText(likesButtonString);

        Drawable heart = ContextCompat.getDrawable(mReplyTV.getContext(), R.drawable.ic_action_heart_red);
        heart.setBounds(0, 0, heart.getMinimumWidth(), heart.getMinimumHeight());
        if (mLikeFlag) {
            mLikesTV.setCompoundDrawables(heart, null, null, null);
        }

        final DatabaseReference rootRef = database.getReference();
        final DatabaseReference myRef = rootRef.child("replies").child(mSecretItem.secretId);

        mSendCommentIBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    insertReply(rootRef);
                } else {
                    String toastString = "please log in";
                    Toast.makeText(view.getContext(), toastString, Toast.LENGTH_LONG).show();
                }
            }
        });

        // String myUserId = getUid();
        Query myTopPostsQuery = myRef.orderByChild("time");

        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Reply> list = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    list.add(ds.getValue(Reply.class));
                }

                Collections.reverse(list);

                repliesAdapter.updateRepliesItems(list);
                Log.d(TAG, "Value is " + list);

//                GenericTypeIndicator<Map<String, Reply>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Reply>>() {
//                };
//                Map<String, Reply> secrets = dataSnapshot.getValue(genericTypeIndicator);
//                if (secrets == null || secrets.isEmpty()) {
//                    // nothing to get
//                    return;
//                }
//                List<Reply> list = new ArrayList<>();
//                for (Map.Entry<String, Reply> entry : secrets.entrySet()) {
//                    list.add(entry.getValue());
//                }
//                repliesAdapter.updateRepliesItems(list);
//                Log.d(TAG, "Value is " + secrets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        mLikesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLike(rootRef);
            }
        });

    }


    private void insertReply(DatabaseReference rootRef) {
        String replyText = mComment.getText().toString();
        if (replyText.isEmpty()) {
            return;
        }
        Reply reply = new Reply(replyText);

        String key = reply.replyId;
        // insert reply into database;
        DatabaseReference myRef = rootRef.child("replies").child(mSecretItem.secretId);
        myRef.child(key).setValue(reply);

        //update comment number for secret && user/secret
        try {
            String userId = mAuth.getCurrentUser().getUid();
            // reset replyTV
            mSecretItem.comments = mSecretItem.comments + 1;
            UpdateSecret.updateSecret(mSecretItem, userId, rootRef);
            mComment.setText("");
            String replyButtonString = mReplyTV.getContext().getString(
                    R.string.comment, mSecretItem.comments
            );
            mReplyTV.setText(replyButtonString);
        } catch (Exception e) {
            Log.w(TAG, "not log in yet");
        }

    }

    private void updateLike(DatabaseReference rootRef) {
        Drawable heart_red = ContextCompat.getDrawable(mReplyTV.getContext(), R.drawable.ic_action_heart_red);
        Drawable heart = ContextCompat.getDrawable(mReplyTV.getContext(), R.drawable.ic_action_heart);
        heart.setBounds(0, 0, heart.getMinimumWidth(), heart.getMinimumHeight());
        heart_red.setBounds(0, 0, heart.getMinimumWidth(), heart.getMinimumHeight());

        try {
            String userId = mAuth.getCurrentUser().getUid();
            // reset replyTV
            mLikeFlag = !mLikeFlag;
            getIntent().putExtra("LikeFlag", mLikeFlag);
            DatabaseReference likeRef = rootRef.child("likelist").child(userId);
            if (mLikeFlag) {
                likeRef.child(mSecretItem.secretId).setValue(mSecretItem.secretId);

                mSecretItem.likes = mSecretItem.likes + 1;
                mLikesTV.setCompoundDrawables(heart_red, null, null, null);

            } else {
                likeRef.child(mSecretItem.secretId).removeValue();
                mSecretItem.likes = mSecretItem.likes - 1;
                mLikesTV.setCompoundDrawables(heart, null, null, null);
            }
            // update secret & user secret
            UpdateSecret.updateSecret(mSecretItem, userId, rootRef);

            String likesButtonString = mLikesTV.getContext().getString(
                    R.string.likes, mSecretItem.likes
            );
            mLikesTV.setText(likesButtonString);
        } catch (Exception e) {

            String toastString = "please log in";
            Toast.makeText(this.getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
            Log.w(TAG, "not log in yet");
        }

    }
}
