package com.project.lostandfind.Utils;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.project.lostandfind.interfaces.AuthCallBack;
import com.project.lostandfind.interfaces.PostCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;

public class Database {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private AuthCallBack authCallBack;
    private PostCallBack postCallBack;

    public Database(){
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://lost-and-found-5eca3-default-rtdb.europe-west1.firebasedatabase.app");
        mStorage = FirebaseStorage.getInstance();
    }

    public void setAuthCallBack(AuthCallBack authCallBack){
        this.authCallBack = authCallBack;
    }

    public void setPostCallBack(PostCallBack postCallBack){
        this.postCallBack = postCallBack;
    }

    public void logout() {
        mAuth.signOut();
    }

    public void login(User user){
       mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()) {
                   authCallBack.onLoginComplete(true, "");
               }else{
                   authCallBack.onLoginComplete(false, task.getException().getMessage());
               }
           }
       }) ;
    }

    public void createNewUser(User user){
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            authCallBack.onCreateAccountComplete(true, "");
                        }else{
                            authCallBack.onCreateAccountComplete(false,
                                    task.getException().getMessage());
                        }
                    }
                });
    }

    public void updateUserInfo(User user) {
        mDatabase.getReference("Users").child(user.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            authCallBack.updateUserInfoComplete(true, "");
                        }else{
                            authCallBack.updateUserInfoComplete(false, task.getException().getMessage());
                        }
                    }
                });
    }

    public FirebaseUser getCurrentUser(){
        return this.mAuth.getCurrentUser();
    }

    public void updateUserProfileImage(String imageName, Uri uri){
        StorageReference ref = mStorage.getReference("profiles_images/"+imageName);
        ref.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                     ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                         @Override
                         public void onComplete(@NonNull Task<Uri> task) {
                             FirebaseUser user = getCurrentUser();
                             UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                             .setPhotoUri(task.getResult())
                             .build();
                             user.updateProfile(profileUpdates);
                         }
                     });
                }

            }
        });

    }

    public void getUserInfo(String uid){
        mDatabase.getReference("Users/"+uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null)
                    user.setUid(uid);

                authCallBack.fetchUserInfoComplete(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void uploadPost(Post post, Uri uri) {

        mStorage.getReference("lost_items_images/"+post.getImgUrl()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    mDatabase.getReference("Posts").push().setValue(post);
                    postCallBack.uploadPostComplete(true, "");
                }else {
                    postCallBack.uploadPostComplete(false, "Failed to upload post picture");
                }
            }
        });
    }


    public void fetchLostItems(){
        mDatabase.getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<LostItemPost> posts = new ArrayList<>();
                for(DataSnapshot snap : snapshot.getChildren()){
                    LostItemPost lostItemPost = new LostItemPost();
                    //fetch post info
                    lostItemPost.setPost(snap.getValue(Post.class));
                    lostItemPost.getPost().setUid(snap.getKey());
                    //fetch post image
                    String imagePath = "lost_items_images/" + lostItemPost.getPost().getImgUrl();
                    Task<Uri> downloadImageTask = mStorage.getReference(imagePath).getDownloadUrl();

                    //fetch user info
                    String path = "Users/" + lostItemPost.getPost().getUserUid();
                    Task<DataSnapshot> fetchUserTask =  mDatabase.getReference(path).get();

                    //wait until fetch image and user info finish
                    while(!fetchUserTask.isComplete() || !downloadImageTask.isComplete());

                    User user = fetchUserTask.getResult().getValue(User.class);
                    user.setUid(lostItemPost.getPost().getUserUid());
                    lostItemPost.setUser(user);
                    lostItemPost.getPost().setImgUrl(downloadImageTask.getResult().toString());
                    posts.add(lostItemPost);
                }

                Collections.reverse(posts);//sort by find time (new first)
                postCallBack.fetchLostItems(posts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ArrayList<LostItemPost> posts = new ArrayList<>();
                postCallBack.fetchLostItems(posts);
            }
        });
    }

    public void removePost(LostItemPost post) {
        this.mDatabase.getReference("Posts/"+post.getPost().getUid()).removeValue();
    }
}
