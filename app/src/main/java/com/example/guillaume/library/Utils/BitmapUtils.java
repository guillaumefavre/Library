package com.example.guillaume.library.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by guillaume on 25/10/15.
 */
public class BitmapUtils {


    /**
     * Méthode qui convertit un bitmap en String encodée en BAse64
     *
     * @param bitmap
     * @return
     */
    public static String convertBitmapToBytesArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] byteArray = stream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * Méthode qui convertit un String Base64 en bitmap
     *
     * @param encodedImage
     * @return
     */
    public static Bitmap convertByteArrayToBitmap(String encodedImage) {

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
