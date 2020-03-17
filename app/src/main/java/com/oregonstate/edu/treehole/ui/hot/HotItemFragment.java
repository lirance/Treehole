package com.oregonstate.edu.treehole.ui.hot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oregonstate.edu.treehole.R;
import com.oregonstate.edu.treehole.SecretDetailActivity;
import com.oregonstate.edu.treehole.SecretsAdapter;
import com.oregonstate.edu.treehole.data.model.Secret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HotItemFragment extends Fragment
        implements SecretsAdapter.OnSecretTouchedListener {

    private static final String TAG = HotItemFragment.class.getSimpleName();
    private View root;
    private SecretsAdapter secretsAdapter;
    private RecyclerView mSecretsRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_hot, container, false);

        mSecretsRV = root.findViewById(R.id.rv_secret_items_hot);
        secretsAdapter = new SecretsAdapter(this);
        mSecretsRV.setAdapter(secretsAdapter);
        mSecretsRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mSecretsRV.setHasFixedSize(true);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("secrets");
        myRef.orderByChild("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Secret> list = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    list.add(ds.getValue(Secret.class));
                }

                Collections.reverse(list);

                secretsAdapter.updateSecretItems(list, null);
                Log.d(TAG, "Value is " + list);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        DatabaseReference likeRef = database.getReference("likelist");
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            likeRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, String>> genericTypeIndicator = new GenericTypeIndicator<Map<String, String>>() {
                    };
                    Map<String, String> userLikes = dataSnapshot.getValue(genericTypeIndicator);
                    if (userLikes == null || userLikes.isEmpty()) {
                        // nothing to get
                        return;
                    }
                    List<String> likeList = new ArrayList<>(userLikes.keySet());
                    secretsAdapter.updateSecretItems(null, likeList);
                    Log.d(TAG, "Value is " + userLikes);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return root;
    }

    @Override
    public void onSecretTouched(Secret secret, boolean like) {
        Intent intent = new Intent(getActivity(), SecretDetailActivity.class);
        intent.putExtra("Secret", secret);
        intent.putExtra("LikeFlag", like);
        startActivity(intent);
    }
}