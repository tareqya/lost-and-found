package com.project.lostandfind.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.lostandfind.R;
import com.project.lostandfind.Utils.Database;
import com.project.lostandfind.Utils.LostItemPost;
import com.project.lostandfind.interfaces.LostItemCallBack;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class LostItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity activity;
    private ArrayList<LostItemPost> posts;
    private LostItemCallBack lostItemCallBack;
    private Database db;
    public LostItemAdapter(Activity activity, ArrayList<LostItemPost> posts) {
        this.activity = activity;
        this.posts = posts;
        this.db = new Database();
    }

    public LostItemAdapter setPosts(ArrayList<LostItemPost> posts) {
        this.posts = posts;
        return this;
    }

    public void setLostItemCallBack(LostItemCallBack lostItemCallBack){
        this.lostItemCallBack = lostItemCallBack;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lost_item, viewGroup, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostViewHolder postViewHolder = (PostViewHolder) holder;
        LostItemPost lostItemPost = getItem(position);
        postViewHolder.lost_TV_Title.setText(lostItemPost.getPost().getTitle());
        postViewHolder.lost_TV_name.setText(lostItemPost.getUser().getName());
        Glide
            .with(activity)
            .load(lostItemPost.getPost().getImgUrl())
            .centerCrop()
            .placeholder(R.drawable.attach)
            .into(postViewHolder.lost_IV_postImage);

        String uid = db.getCurrentUser().getUid();
        if (uid.equals(lostItemPost.getUser().getUid())){
            postViewHolder.lost_IV_remove.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public LostItemPost getItem(int position){
        return posts.get(position);
    }
    public class PostViewHolder extends RecyclerView.ViewHolder {
        public TextView lost_TV_Title;
        public ImageView lost_IV_postImage;
        public MaterialButton lost_BTN_call;
        public MaterialButton lost_BTN_openInMap;
        public TextView lost_TV_name;
        public ImageView lost_IV_remove;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            this.lost_TV_Title = itemView.findViewById(R.id.lost_TV_Title);
            this.lost_IV_postImage = itemView.findViewById(R.id.lost_IV_postImage);
            this.lost_BTN_call = itemView.findViewById(R.id.lost_BTN_call);
            this.lost_BTN_openInMap = itemView.findViewById(R.id.lost_BTN_openInMap);
            this.lost_TV_name = itemView.findViewById(R.id.lost_TV_name);
            this.lost_IV_remove = itemView.findViewById(R.id.lost_IV_remove);
            initVars();
        }

        private void initVars() {
            this.lost_BTN_openInMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LostItemPost lostItemPost = getItem(getAdapterPosition());
                    lostItemCallBack.onMapButtonPress(lostItemPost);
                }
            });

            this.lost_BTN_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LostItemPost lostItemPost = getItem(getAdapterPosition());
                    lostItemCallBack.onCallButtonPress(lostItemPost);
                }
            });

            this.lost_IV_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LostItemPost lostItemPost = getItem(getAdapterPosition());
                    lostItemCallBack.onRemovePress(lostItemPost);
                }
            });
        }
    }

}
