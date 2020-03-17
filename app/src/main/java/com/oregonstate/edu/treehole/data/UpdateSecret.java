package com.oregonstate.edu.treehole.data;

import com.google.firebase.database.DatabaseReference;
import com.oregonstate.edu.treehole.data.model.Secret;

public class UpdateSecret {

    public static boolean updateSecret(Secret secret, String userId, DatabaseReference rootRef) {
        String secretId = secret.secretId;
        // insert secret
        DatabaseReference inSecRef = rootRef.child("secrets");
        inSecRef.child(secretId).setValue(secret);
        // insert user secret

        DatabaseReference usRef = rootRef.child("userSecrets");
        usRef.child(userId).child(secretId).setValue(secret);

        return true;

    }
}
