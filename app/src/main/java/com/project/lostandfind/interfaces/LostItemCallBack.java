package com.project.lostandfind.interfaces;

import com.project.lostandfind.Utils.LostItemPost;

public interface LostItemCallBack {

    void onCallButtonPress(LostItemPost post);
    void onMapButtonPress(LostItemPost post);
    void onRemovePress(LostItemPost post);
}
