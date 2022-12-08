package com.project.lostandfind.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;


public class UtilsFunctions {
    public static String getFileExtension(Activity activity, Uri uri){
        ContentResolver contentResolver = activity.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
