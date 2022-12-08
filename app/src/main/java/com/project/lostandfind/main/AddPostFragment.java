package com.project.lostandfind.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.project.lostandfind.R;
import com.project.lostandfind.Utils.Database;
import com.project.lostandfind.Utils.ItemLocation;
import com.project.lostandfind.Utils.LostItemPost;
import com.project.lostandfind.Utils.Post;
import com.project.lostandfind.Utils.User;
import com.project.lostandfind.Utils.UtilsFunctions;
import com.project.lostandfind.interfaces.AuthCallBack;
import com.project.lostandfind.interfaces.PostCallBack;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class AddPostFragment extends Fragment {
    private static final int SCORE_PER_POST = 200;

    private TextInputLayout frag_add_post_TIL_title, frag_add_post_TIL_location;
    private MaterialButton frag_add_post_BTN_current_location, frag_add_post_BTN_add_post;
    private ImageView frag_add_post_IV_image;
    private Activity activity;
    private Uri imageUri;
    private ItemLocation location;
    private Database db;
    private ProgressBar progressBar;
    private User currentUser;

    public AddPostFragment() {
        db = new Database();

    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root =  inflater.inflate(R.layout.fragment_add_post, container, false);
        findViews(root);
        initVars();
        return root;
    }

    private void initVars() {

        getCurrentLocation();
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
                currentUser = user;
            }
        });


        db.setPostCallBack(new PostCallBack() {
            @Override
            public void uploadPostComplete(boolean status, String msg) {
                if(status){
                    Toast.makeText(activity, "Post upload success!", Toast.LENGTH_SHORT).show();
                    currentUser.setScore(currentUser.getScore() + SCORE_PER_POST);
                    db.updateUserInfo(currentUser);
                    cleanScreen();
                }else{
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void fetchLostItems(ArrayList<LostItemPost> lostItemPosts) {

            }

        });

        db.getUserInfo(db.getCurrentUser().getUid());

        frag_add_post_IV_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        frag_add_post_BTN_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!check()){
                    Toast.makeText(activity, "You should fill all the fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String imageUrl = db.getCurrentUser().getUid() + generateRandomNumber() + "." + UtilsFunctions.getFileExtension(activity, imageUri);
                Post post = new Post()
                        .setCreate_time(new Date().getTime())
                        .setImgUrl(imageUrl)
                        .setTitle(frag_add_post_TIL_title.getEditText().getText().toString())
                        .setLocation(location)
                        .setUserUid(db.getCurrentUser().getUid());

                progressBar.setVisibility(View.VISIBLE);
                db.uploadPost(post, imageUri);
            }
        });

        frag_add_post_BTN_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });


        progressBar.setVisibility(View.INVISIBLE);
    }

    private boolean check() {
        if(frag_add_post_TIL_title.getEditText().getText().toString().equals("")){
            return false;
        }
        if(frag_add_post_TIL_location.getEditText().getText().toString().equals("")){
            return false;
        }
        return true;
    }

    private int generateRandomNumber(){
        Random rnd = new Random();
        int num = rnd.nextInt(9999999) + 100000;
        return num;
    }

    private void getCurrentLocation() {
        Geocoder geoCoder = new Geocoder(activity, Locale.getDefault()); //it is Geocoder
        Location locationGPS = getLastKnownLocation();//locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            double latitude = locationGPS.getLatitude();
            double longitude = locationGPS.getLongitude();
            List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
            String city = address.get(0).getLocality();
            String country = address.get(0).getCountryName();
            String street = address.get(0).getFeatureName();
            location = new ItemLocation()
                    .setLocationName(country + " " + city + " " + street)
                    .setLatitude(latitude)
                    .setLongitude(longitude);
            frag_add_post_TIL_location.getEditText().setText(location.getLocationName());

        } catch (IOException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(activity, "Failed to get the last known location", Toast.LENGTH_SHORT).show();
        }
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add lost item photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraResults.launch(intent);
                }
                else if (options[item].equals("Choose from Gallery")) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    gallery_results.launch(photoPickerIntent);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cleanScreen(){
       getCurrentLocation();
       frag_add_post_IV_image.setImageResource(R.drawable.attach);
       frag_add_post_TIL_title.getEditText().setText("");

    }

    private final ActivityResultLauncher<Intent> cameraResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            Intent intent = result.getData();
                            Bitmap bitmap = (Bitmap)  intent.getExtras().get("data");
                            frag_add_post_IV_image.setImageBitmap(bitmap);
                            imageUri = UtilsFunctions.getImageUri(activity, bitmap);
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(activity, "Camera canceled", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

    private final ActivityResultLauncher<Intent> gallery_results = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            try {
                                Intent intent = result.getData();
                                imageUri = intent.getData();
                                final InputStream imageStream = activity.getContentResolver().openInputStream(imageUri);
                                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                                frag_add_post_IV_image.setImageBitmap(selectedImage);
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

    private void findViews(View root) {
        frag_add_post_TIL_title = root.findViewById(R.id.frag_add_post_TIL_title);
        frag_add_post_TIL_location = root.findViewById(R.id.frag_add_post_TIL_location);
        frag_add_post_BTN_current_location = root.findViewById(R.id.frag_add_post_BTN_current_location);
        frag_add_post_BTN_add_post = root.findViewById(R.id.frag_add_post_BTN_add_post);
        frag_add_post_IV_image = root.findViewById(R.id.frag_add_post_IV_image);
        progressBar = root.findViewById(R.id.frag_add_post_progress);
    }
}