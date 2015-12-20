package com.example.guillaume.library.API;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Metier.CD;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guillaume on 19/12/15.
 */
public class MusicBrainzAPI extends AsyncTask<String, Void, CD> {

    // WeakReference permet d'envoyer au GarbageCollector
    // l'objet si un appel téléphonique arrive par exemple
    // afin d'éviter une éventuelle fuite mémoire, qui aurait
    // lieu si l'AsyncTask pointerait toujours vers l'activité
    // qui n'existe plus
    // CF. http://mathias-seguy.developpez.com/cours/android/handler_async_memleak/
    private WeakReference<Activity> callingActivity = null;

    private CDDao cdDao;

    public MusicBrainzAPI(Activity activity) {
        callingActivity = new WeakReference<Activity>(activity);
    }


    @Override
    protected CD doInBackground(String... cdURLs) {

        URL url = null;

        CD cd = null;

        try {
            url = new URL("http://musicbrainz.org/ws/2/release?query=barcode:" + cdURLs[0]);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            if(connection.getResponseCode() == 200) {


                // Lecture de la réponse
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }

                JSONObject responseJson = new JSONObject(builder.toString());

                cd = construireCd(responseJson);

            }



        } catch (MalformedURLException e) {
            Log.d(getClass().getName(), "MalformedURLException lors de la récupération de la réponse de l'API Music Brainz");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(getClass().getName(), "IOException lors de la récupération de la réponse de l'API Music Brainz");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException lors de la récupération de la réponse de l'API Google Books");
            e.printStackTrace();
        }

        return cd;
    }

    private CD construireCd(JSONObject responseJson) {
        CD cd = null;

        if(responseJson == null) {
            // TODO traitement erreur
        } else {

        }


        return cd;
    }
}
