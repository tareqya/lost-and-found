package com.project.lostandfind.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.lostandfind.R;
import com.project.lostandfind.Utils.Database;
import com.project.lostandfind.Utils.User;
import com.project.lostandfind.Utils.UtilsFunctions;
import com.project.lostandfind.auth.LoginActivity;
import com.project.lostandfind.interfaces.AuthCallBack;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private Activity activity;
    private ImageView profile_image;
    private FloatingActionButton profile_FBTN_uploadImage;
    private LinearLayout profile_LL_editDetails, profile_LL_logout;
    private TextView profile_TV_name, profile_TV_score, profile_TV_email;
    private Database db;
    private User currentUser;
    public ProfileFragment() {
        db = new Database();
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        findViews(root);
        initVars();
        return root;
    }


    private void findViews(View root) {

        profile_image = root.findViewById(R.id.profile_image);
        profile_FBTN_uploadImage = root.findViewById(R.id.profile_FBTN_uploadImage);
        profile_LL_editDetails = root.findViewById(R.id.profile_LL_editDetails);
        profile_LL_logout = root.findViewById(R.id.profile_LL_logout);
        profile_TV_name = root.findViewById(R.id.profile_TV_name);
        profile_TV_score = root.findViewById(R.id.profile_TV_score);
        profile_TV_email = root.findViewById(R.id.profile_TV_email);

    }


    private void initVars() {

        db.setAuthCallBack(new AuthCallBack() {
            @Override
            public void onCreateAccountComplete(boolean status, String msg) {

            }

            @Override
            public void updateUserInfoComplete(boolean status, String msg) {

            }

            @Override
            public void onLoginComplete(boolean status, String msg) {

            }

            @Override
            public void fetchUserInfoComplete(User user) {
                if(user == null) return;
                currentUser = user;
                profile_TV_name.setText(user.getName());
                profile_TV_email.setText(user.getEmail());
                profile_TV_score.setText(user.getScore() + "");

            }
        });

        db.getUserInfo(db.getCurrentUser().getUid());
        Uri profileImageUri = db.getCurrentUser().getPhotoUrl();
        if(profileImageUri != null){
            Glide
                    .with(activity)
                    .load(profileImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.user)
                    .into(profile_image);
        }

        profile_LL_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.logout();
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });

        profile_FBTN_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                gallery_results.launch(photoPickerIntent);
            }
        });

        profile_LL_editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(activity, profileDetailsUpdateActivity.class));
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });
    }

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                Uri imageUri = intent.getData();
                                final InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                profile_image.setImageBitmap(selectedImage);
                                String imageUrl = db.getCurrentUser().getUid() + "." + UtilsFunctions.getFileExtension(activity, imageUri);
                                db.updateUserProfileImage(imageUrl, imageUri);
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Gallery canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
}