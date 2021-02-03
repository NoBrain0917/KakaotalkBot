package com.lml.talkbot.Class;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageDB {
    private static Bitmap profileImage=null;


    public ImageDB(Bitmap profileImage){
        super();
        this.profileImage = profileImage;
    }

    public String getProfileImage(){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] ba=baos.toByteArray();
        return Base64.encodeToString(ba, 0);
    }
    public static Bitmap getProfileImageBitmap(){
        return profileImage;
    }


}
