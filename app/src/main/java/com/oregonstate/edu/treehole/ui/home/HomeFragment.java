package com.oregonstate.edu.treehole.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment
        implements SecretsAdapter.OnSecretTouchedListener {

    private HomeViewModel homeViewModel;
    private static final String TAG = HomeFragment.class.getSimpleName();
    private View root;
    private SecretsAdapter secretsAdapter;
    private RecyclerView mSecretsRV;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //  final TextView textView = root.findViewById(R.id.text_home);

        mSecretsRV = root.findViewById(R.id.rv_secret_items);
        secretsAdapter = new SecretsAdapter(this);
        mSecretsRV.setAdapter(secretsAdapter);
        mSecretsRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mSecretsRV.setHasFixedSize(true);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("secrets");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Secret>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Secret>>() {
                };
                Map<String, Secret> secrets = dataSnapshot.getValue(genericTypeIndicator);
                if (secrets == null || secrets.isEmpty()) {
                    // nothing to get
                    return;
                }
                List<Secret> list = new ArrayList<>();

                for (Map.Entry<String, Secret> entry : secrets.entrySet()) {
                    list.add(entry.getValue());
                }

                secretsAdapter.updateSecretItems(list);
                Log.d(TAG, "Value is " + secrets);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        return root;
    }

    @Override
    public void onSecretTouched(Secret secret) {
        Intent intent = new Intent(getActivity(), SecretDetailActivity.class);
        intent.putExtra("Secret", secret);
        startActivity(intent);
    }
}