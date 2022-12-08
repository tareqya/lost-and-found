package com.project.lostandfind.interfaces;

import com.project.lostandfind.Utils.User;

public interface AuthCallBack {
    void onCreateAccountComplete(boolean status, String msg);
    void updateUserInfoComplete(boolean status, String msg);
    void onLoginComplete(boolean status, String msg);
    void fetchUserInfoComplete(User user);
}
