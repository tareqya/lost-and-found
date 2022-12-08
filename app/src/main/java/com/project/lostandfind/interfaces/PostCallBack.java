package com.project.lostandfind.interfaces;

import com.project.lostandfind.Utils.LostItemPost;

import java.util.ArrayList;

public interface PostCallBack {
    void uploadPostComplete(boolean status, String msg);
    void fetchLostItems(ArrayList<LostItemPost> lostItemPosts);

}
