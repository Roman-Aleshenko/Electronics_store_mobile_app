package com.example.electronicsstore.ui.profile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {
    private FirebaseAuth auth;

    public UserManager() {
        auth = FirebaseAuth.getInstance();
    }

    public void signOut() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            auth.signOut();
        }
    }
}