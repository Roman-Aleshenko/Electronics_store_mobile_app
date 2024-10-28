package com.example.electronicsstore.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.electronicsstore.R;
import com.example.electronicsstore.activities.LoginActivity;
import com.example.electronicsstore.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private UserManager userManager;

    CircleImageView profileImg;
    EditText name,email,number,address, password;
    Button update;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    public ProfileFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        userManager = new UserManager();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        profileImg = rootView.findViewById(R.id.profile_img);
        name = rootView.findViewById(R.id.profile_name);
        email = rootView.findViewById(R.id.profile_email);
        number = rootView.findViewById(R.id.profile_number);
        address = rootView.findViewById(R.id.profile_address);
        update = rootView.findViewById(R.id.update);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });

        // Добавление кнопки логаута
        Button logoutButton = rootView.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userManager.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return rootView;
    }

    private void updateUserProfile() {
        String userId = auth.getCurrentUser().getUid();
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userNumber = number.getText().toString().trim();
        String userAddress = address.getText().toString().trim();

        // Обновление данных пользователя в базе данных
        DatabaseReference userRef = database.getReference("Пользователи").child(userId);
        userRef.child("name").setValue(userName);
        userRef.child("email").setValue(userEmail);
        userRef.child("number").setValue(userNumber);
        userRef.child("address").setValue(userAddress).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Профиль обновлён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getData() != null){
            Uri profileUri = data.getData();
            profileImg.setImageURI(profileUri);

            final StorageReference reference = storage.getReference().child("profile_picture")
                    .child(FirebaseAuth.getInstance().getUid());

            reference.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Загружено", Toast.LENGTH_SHORT).show();

                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            database.getReference().child("Пользователи").child(FirebaseAuth.getInstance().getUid())
                                    .child("profileImd").setValue(uri.toString());
                            Toast.makeText(getContext(), "Аватарка загружена", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
        }
    }
}