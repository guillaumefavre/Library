package com.example.guillaume.library.API;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.ScanCABActivity;
import com.example.guillaume.library.UtilsBitmap;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guillaume on 26/09/15.
 */
public class GoogleBooksAPI extends AsyncTask<String, Void, Livre> {

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
    protected Livre doInBackground(String... bookURLs) {

        URL url = null;

        int responseCode = 0;


        Livre livre = null;


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

                JSONObject responseJson = new JSONObject(builder.toString());

                livre = construireLivre(responseJson);
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

        return livre;
    }

    /**
     *
     * @param jsonObject
     * @return
     */
    private Livre construireLivre(JSONObject jsonObject) {
        Livre livre = null;

        if(jsonObject == null) {
            // TODO traitement erreur
        } else {
            try {
                JSONArray items = jsonObject.getJSONArray("items");
                JSONObject jsObj1 = items.getJSONObject(0);
                JSONObject jsoVolumeInfo = jsObj1.getJSONObject("volumeInfo");

                livre = new Livre();

                // Récupération du titre
                recupererTitre(livre, jsoVolumeInfo);

                // Récupération des atuetrs du livre
                recupererAuteurs(livre, jsoVolumeInfo);

                // Récupération de la description
                recupererDescription(livre, jsoVolumeInfo);

                // Récupération de la couverture
                recupererCouverture(livre, jsoVolumeInfo);

                // TODO : à supprimer
                final ScanCABActivity parentActivity = (ScanCABActivity) callingActivity.get();
                parentActivity.getTxvBookTitle().setText(jsoVolumeInfo.getString("title"));

                livreDao.ajouterLivre(livre);

            } catch (JSONException e) {
                e.printStackTrace();
            }  finally {

                // Fermeture de la connexion à la BDD
                livreDao.closeDatabase();
            }
        }

        return livre;

    }


    /**
     * Récupération du titre du livre
     *
     * @param livre
     * @param jsoVolumeInfo
     * @throws JSONException
     */
    private void recupererTitre(Livre livre, JSONObject jsoVolumeInfo) throws JSONException {
        if(jsoVolumeInfo.has("title")) {
            livre.setTitre(jsoVolumeInfo.getString("title"));
        }
    }


    /**
     * Récupération des auteurs du livre
     *
     * @param livre
     * @param jsoVolumeInfo
     * @throws JSONException
     */
    private void recupererAuteurs(Livre livre, JSONObject jsoVolumeInfo) throws JSONException {

        if(jsoVolumeInfo.has("authors")) {
            JSONArray jsArrayauteurs = jsoVolumeInfo.getJSONArray("authors");

            String strAuteurs = "";

            // Récupération des auteurs multiples
            for(int i=0; i < jsArrayauteurs.length(); i++) {
                if(i > 0) {
                    strAuteurs = strAuteurs +", ";
                }
                strAuteurs = strAuteurs +jsArrayauteurs.get(i);
            }

            livre.setAuteur(strAuteurs);
        }
    }


    /**
     * Récupération de la description du livre
     *
     * @param livre
     * @param jsoVolumeInfo
     * @throws JSONException
     */
    private void recupererDescription(Livre livre, JSONObject jsoVolumeInfo) throws JSONException {
        if(jsoVolumeInfo.has("description")) {
            livre.setDescription(jsoVolumeInfo.getString("description"));
        }
    }



    /**
     * Récupération de la couverture du livre
     *
     * @param livre
     * @param jsoVolumeInfo
     * @throws JSONException
     */
    private void recupererCouverture(Livre livre, JSONObject jsoVolumeInfo) throws JSONException {
        if(jsoVolumeInfo.has("imageLinks")) {
            JSONObject jsoImageLinks = jsoVolumeInfo.getJSONObject("imageLinks");
            if(jsoImageLinks.has("smallThumbnail")) {
                // Récupération de la couverture
                String urlCover = jsoImageLinks.getString("smallThumbnail");
                Bitmap couverture = null;

                InputStream in = null;
                try {
                    in = new URL(urlCover).openStream();
                    couverture = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(couverture != null) {
                    livre.setCouverture(UtilsBitmap.convertBitmapToBytesArray(couverture));
                }

            }

        }
    }


}
