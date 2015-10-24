package com.example.guillaume.library.API;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.ScanCABActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guillaume on 26/09/15.
 */
public class GoogleBooksAPI extends AsyncTask<String, Void, JSONObject> {

    // WeakReference permet d'envoyer au GarbageCollector
    // l'objet si un appel téléphonique arrive par exemple
    // afin d'éviter une éventuelle fuite mémoire, qui aurait
    // lieu si l'AsyncTask pointerait toujours vers l'activité
    // qui n'existe plus
    // CF. http://mathias-seguy.developpez.com/cours/android/handler_async_memleak/
    private WeakReference<Activity> callingActivity = null;


    private LivreDAO livreDao;


    public GoogleBooksAPI(Activity activity) {
        callingActivity = new WeakReference<Activity>(activity);
    }


    @Override
    protected JSONObject doInBackground(String... bookURLs) {

        URL url = null;

        int responseCode = 0;

        JSONObject responseJson = null;


        try {
            url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + bookURLs[0]);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if(connection.getResponseCode() == 200) {

                // ouverture connextion BDD
                livreDao = new LivreDAO(callingActivity.get());
                livreDao.openDatabase();

                InputStream inputStream = connection.getInputStream();

                // Lecture de la réponse
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = responseReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = responseReader.readLine();
                }

                responseJson = new JSONObject(builder.toString());
            }


            // Déconnexion
            connection.disconnect();


        } catch (MalformedURLException e) {
            Log.d(getClass().getName(), "MalformedURLException lors de la récupération de la réponse de l'API Google Books");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(getClass().getName(), "IOException lors de la récupération de la réponse de l'API Google Books");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(getClass().getName(), "JSONException lors de la récupération de la réponse de l'API Google Books");
            e.printStackTrace();
        }

        return responseJson;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if(jsonObject == null) {
            // TODO traitement erreur
        } else {
            try {
                JSONArray items = jsonObject.getJSONArray("items");
                JSONObject jsObj1 = items.getJSONObject(0);
                JSONObject jsoVolumeInfo = jsObj1.getJSONObject("volumeInfo");



                JSONArray jsArrayauteurs = jsoVolumeInfo.getJSONArray("authors");

                String strAuteurs = "";
                for(int i=0; i < jsArrayauteurs.length(); i++) {
                    if(i > 0) {
                        strAuteurs = strAuteurs +", ";
                    }
                    strAuteurs = strAuteurs +jsArrayauteurs.get(i);
                }

                final ScanCABActivity parentActivity = (ScanCABActivity) callingActivity.get();

                parentActivity.getTxvBookTitle().setText(jsoVolumeInfo.getString("title"));
                parentActivity.getTxvBookAuthor().setText(strAuteurs);


                Livre livre = new Livre();
                livre.setTitre(jsoVolumeInfo.getString("title"));
                livre.setAuteur(strAuteurs);
                livre.setDescription(jsoVolumeInfo.getString("description"));

                livreDao.ajouterLivre(livre);


                livreDao.closeDatabase();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
