package com.oregonstate.edu.treehole;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oregonstate.edu.treehole.data.UpdateSecret;
import com.oregonstate.edu.treehole.data.model.Reply;
import com.oregonstate.edu.treehole.data.model.Secret;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SecretDetailActivity extends AppCompatActivity {

    private static final String TAG = SecretDetailActivity.class.getSimpleName();
    private Secret mSecretItem;
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

        final DatabaseReference rootRef = database.getReference();
        DatabaseReference myRef = rootRef.child("replies").child(mSecretItem.secretId);

        mSendCommentIBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    insertReply(rootRef);
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Reply>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Reply>>() {
                };
                Map<String, Reply> secrets = dataSnapshot.getValue(genericTypeIndicator);
                if (secrets == null || secrets.isEmpty()) {
                    // nothing to get
                    return;
                }
                List<Reply> list = new ArrayList<>();
                for (Map.Entry<String, Reply> entry : secrets.entrySet()) {
                    list.add(entry.getValue());
                }
                repliesAdapter.updateRepliesItems(list);
                Log.d(TAG, "Value is " + secrets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
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

    private void updateLike(){

    }
}
