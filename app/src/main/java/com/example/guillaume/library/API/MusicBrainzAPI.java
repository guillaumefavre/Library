package com.example.guillaume.library.API;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.UtilsBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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

        String strUrl = "http://musicbrainz.org/ws/2/release?query=barcode:" + cdURLs[0] +"&fmt=json";

        JSONObject responseJson = getJSONObjectFromURL(strUrl);

        CD cd = construireCd(responseJson);

        return cd;
    }

    /**
     * Construction du CD à partir des données renvoyée par l'appel des URLs.
     *
     * @param responseJson
     * @return
     */
    private CD construireCd(JSONObject responseJson) {
        CD cd = null;

        if(responseJson == null) {
            // TODO traitement erreur
        } else {

            try {
                JSONArray releases = responseJson.getJSONArray("releases");
                JSONObject jsoRelease = releases.getJSONObject(0);


                if(jsoRelease != null) {
                    // Création du CD
                    cd = new CD();

                    // Récupération de l'id de l'album
                    recupererIdAlbum(cd, jsoRelease);

                    String url = "http://musicbrainz.org/ws/2/release-group/" + cd.getIdAlbum() +"?inc=artist-credits&fmt=json";

                    JSONObject jsoReleaseGroup = getJSONObjectFromURL(url);

                    // Récupération du titre de l'album
                    recupererTitreAlbum(cd, jsoReleaseGroup);

                    // Récupération de l'artiste de l'album
                    recupererArtisteALbum(cd, jsoReleaseGroup);

                    // Récupération de la date de sortie de l'album
                    recupererDateSortieAlbum(cd, jsoReleaseGroup);

                    // Récupération de la pochette de l'album
//                    recupererPochetteAlbum(cd);

                    // Ouverture de la connexion à la BDD
                    cdDao = new CDDao(callingActivity.get());
                    cdDao.openDatabase();

                    // AJout du CD
                    cdDao.ajouterCD(cd);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return cd;
    }


    /**
     * Méthode qui récupère l'objet JSON renvoyé par l'URL passé en apramètre.
     *
     * @param strUrl
     * @return
     */
    private JSONObject getJSONObjectFromURL(String strUrl) {

        JSONObject jsoRetour = null;

        try {
            URL url = new URL(strUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() == 200) {


                // Lecture de la réponse
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }


                jsoRetour = new JSONObject(builder.toString());

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsoRetour;
    }


    /**
     * Récupération de l'id de l'album dans le noeud release-group
     *
     * @param cd
     * @param jsoRelease
     * @throws JSONException
     */
    private void recupererIdAlbum(CD cd, JSONObject jsoRelease) throws JSONException {
        if(jsoRelease.has("release-group")) {
            JSONObject jsoReleaseGroup = jsoRelease.getJSONObject("release-group");
            if(jsoReleaseGroup.has("id")) {
                cd.setIdAlbum(jsoReleaseGroup.getString("id"));
            }
        }
    }


    /**
     * Récupération du titre de l'album
     *
     * @param cd
     * @param jsoReleaseGroup
     * @throws JSONException
     */
    private void recupererTitreAlbum(CD cd, JSONObject jsoReleaseGroup) throws JSONException {
            if(jsoReleaseGroup.has("title")) {
                cd.setTitreAlbum(jsoReleaseGroup.getString("title"));
            }
    }


    /**
     * Récupération de la date de sortie de l'album
     *
     * @param cd
     * @param jsoReleaseGroup
     * @throws JSONException
     */
    private void recupererDateSortieAlbum(CD cd, JSONObject jsoReleaseGroup) throws JSONException {
        if (jsoReleaseGroup.has("first-release-date")) {
            cd.setAnneSortie(jsoReleaseGroup.getString("first-release-date"));
        }
    }

    /**
     * Récupération de l'artiste de l'album
     *
     * @param cd
     * @param jsoReleaseGroup
     * @throws JSONException
     */
    private void recupererArtisteALbum(CD cd, JSONObject jsoReleaseGroup) throws JSONException {
        if (jsoReleaseGroup.has("artist-credit")) {
            JSONArray jsArrayArtistes = jsoReleaseGroup.getJSONArray("artist-credit");

            // On prend le premier artiste
            // TODO : gérer albums écrits par plusieurs artistes
            JSONObject jsoArtiste = jsArrayArtistes.getJSONObject(0);

            if (jsoArtiste.has("name")) {
                cd.setArtiste(jsoArtiste.getString("name"));
            }

        }
    }

    /**
     * Récupération de la pochette de l'album
     *
     * @param cd
     */
    private void recupererPochetteAlbum(CD cd) {
        // Récupération de la pochette
        String urlCover = "http://coverartarchive.org/release-group/" +cd.getIdAlbum() +"/front.jpg";
        Bitmap couverture = null;

        InputStream in = null;
        try {
            in = new URL(urlCover).openStream();
            couverture = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(couverture != null) {
            cd.setPochette(UtilsBitmap.convertBitmapToBytesArray(couverture));
        }

    }

}
