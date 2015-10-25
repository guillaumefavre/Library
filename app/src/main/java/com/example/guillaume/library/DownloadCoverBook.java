package com.example.guillaume.library;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by guillaume on 25/10/15.
 */
public class DownloadCoverBook extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;

//    private WeakReference<Activity> callingActivity = null;
//
//    public DownloadCoverBook(Activity activity) {
//        callingActivity = new WeakReference<Activity>(activity);
//    }

    public DownloadCoverBook() {
//        callingActivity = new WeakReference<Activity>(activity);
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urls[0]).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
//        final ScanCABActivity parentActivity = (ScanCABActivity) callingActivity.get();
//        parentActivity.getImvCouvertureLivre().setImageBitmap(result);
    }

}
