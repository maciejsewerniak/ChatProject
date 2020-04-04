package com.example.chat_project.Communication.local.resources.communication.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Picture extends CommunicationData {
    static final long serialVersionUID = 1L;
    private String serializedImage;

    public Bitmap getImage() {
        byte[] imageBytes = Base64.decode(this.serializedImage, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public void setImage(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        this.serializedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
