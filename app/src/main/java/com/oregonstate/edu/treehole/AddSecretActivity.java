package com.oregonstate.edu.treehole;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oregonstate.edu.treehole.data.model.Secret;

public class AddSecretActivity extends AppCompatActivity {

    private EditText mSecretEntryET;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_secret);
        mSecretEntryET = findViewById(R.id.et_secret_content_add);
        myRef = FirebaseDatabase.getInstance().getReference("");
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_post_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_secret:
                sendSecret();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendSecret() {
        String secretText = mSecretEntryET.getText().toString();
        if (secretText.isEmpty()) {
            return;
        }
        Secret secret = new Secret(secretText);
        String uuid = secret.secretId;
        // insert secret
        DatabaseReference inSecRef = myRef.child("secrets");
        inSecRef.child(uuid).setValue(secret);
        // insert user secret
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference usRef = myRef.child("userSecrets");
            usRef.child(uid).child(uuid).setValue(secret);
        }
        finish();
    }
}
