package com.oregonstate.edu.treehole.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.oregonstate.edu.treehole.R;
import com.oregonstate.edu.treehole.SecretDetailActivity;
import com.oregonstate.edu.treehole.SecretsAdapter;
import com.oregonstate.edu.treehole.data.model.Secret;
import com.oregonstate.edu.treehole.login.LoginActivity;

public class HomeFragment extends Fragment implements SecretsAdapter.OnSecretTouchedListener {

    private HomeViewModel homeViewModel;

    private View root;
    private SecretsAdapter secretsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        //  final TextView textView = root.findViewById(R.id.text_home);


        return root;
    }

    @Override
    public void onSecretTouched(Secret secret) {
        Intent intent = new Intent(getActivity(), SecretDetailActivity.class);
        // intent.putExtra(OpenWeatherMapUtils.EXTRA_FORECAST_ITEM, forecastItem);
        startActivity(intent);
    }
}