package com.example.guillaume.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by guillaume on 25/10/15.
 */
public class UtilsBitmap {


    /**
     * Méthode qui convertit un bitmap en tableau de bytes
     * @param bitmap
     * @return
     */
    public static byte[] convertBitmapToBytesArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * Méthode qui convertit un tableau de bytes en bitmap
     * @param image
     * @return
     */
    public static Bitmap convertByteArrayToBitmap(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
