package com.example.guillaume.library.API;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.guillaume.library.CommunActivity;
import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.R;
import com.example.guillaume.library.UtilsBitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guillaume on 13/02/16.
 */
public class AppelAPIs extends AsyncTask<String, Void, Boolean> {

    /** progress dialog to show user that the backup is processing. */
    private ProgressDialog dialog;
    /** application context. */
    private CommunActivity activity;

    private LivreDAO livreDao;

    private CDDao cdDao;

    private CD cd;

    private Livre livre;


    public AppelAPIs(CommunActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }



    protected void onPreExecute() {
        dialog.setTitle("Récupération des informations");
        dialog.setMessage("Veuillez patienter");
        dialog.show();
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        Toast toast = null;

        if(cd != null) {
            toast = Toast.makeText(activity, cd.getTitreAlbum() + " (de " +cd.getArtiste() + ") ajouté à la liste des CDs", Toast.LENGTH_LONG);
        } else if(livre != null) {
            toast = Toast.makeText(activity, livre.getTitre() + " (de " +livre.getAuteur() + ") ajouté à la liste des livres", Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(activity, "Aucune donnée trouvée", Toast.LENGTH_LONG);
        }

        if(toast != null) {
            toast.show();
        }

    }

    protected Boolean doInBackground(final String... params) {


        cd = rechercherCD(params[0]);

        if(cd == null) {
            livre = rechercherLivre(params[0]);
        }


//        if(cd != null) {
//            // Un cd a été ajouté : on en informe l'utilisateur
//            Toast toast = Toast.makeText(this, cd.getTitreAlbum() + " (de " +cd.getArtiste() + ") ajouté à la liste des CDs", Toast.LENGTH_LONG);
//            toast.show();
//        } else {
//            // Pas de cd trouvé, on cherche si un livre correspond au code à barre
//            Livre livre = rechercherLivre(scanContent);
//
//            if(livre != null) {
//                // Un livre a été ajouté : on en informe l'utilisateur
//                Toast toast = Toast.makeText(this, livre.getTitre() + " (de " +livre.getAuteur() + ") ajouté à la liste des livres", Toast.LENGTH_LONG);
//                toast.show();
//            } else {
//                Toast toast = Toast.makeText(this, "Aucune donnée trouvée !", Toast.LENGTH_SHORT);
//                toast.show();
//                    }
//                }
//
//
//            } else {
//                Toast toast = Toast.makeText(this, "CAB non valide !", Toast.LENGTH_SHORT);
//                toast.show();
//            }



        return false;
    }


    /**
     * Appel de l'URL permetatnt de récupérer les informations d'un livre
     *
     * @param cab
     * @return
     */
    private Livre rechercherLivre(String cab) {


        Livre livre = null;

        try {

            URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + cab);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");


            if(connection.getResponseCode() == 200) {

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
//                final ScanCABActivity parentActivity = (ScanCABActivity) callingActivity.get();
//                parentActivity.getTxvBookTitle().setText(jsoVolumeInfo.getString("title"));

                // ouverture connextion BDD
                livreDao = new LivreDAO(activity);
                livreDao.openDatabase();

                livreDao.ajouterLivre(livre);

            } catch (JSONException e) {
                e.printStackTrace();
            }  finally {

                // Fermeture de la connexion à la BDD
                if(livreDao != null) {
                    livreDao.closeDatabase();

                }
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
                } else {
                    affecterCouvertureDefaut(livre);
                }

            }

        } else {
            affecterCouvertureDefaut(livre);
        }
    }


    /**
     * Méthode qui affecte une couverture par défaut à un livre pour lequel la couverture n'a pas été trouvée
     *
     * @param livre
     */
    private void affecterCouvertureDefaut(Livre livre) {
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_inconnu);
        livre.setCouverture(UtilsBitmap.convertBitmapToBytesArray(bitmap));
    }



    private CD rechercherCD(String cab) {

        CD cd = null;


        try {
            URL url = new URL("http://musicbrainz.org/ws/2/release?query=barcode:" + cab +"&fmt=json");

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


                JSONObject reponseJson = new JSONObject(builder.toString());

                cd = construireCd(reponseJson);

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
                    recupererPochetteAlbum(cd);

                    // Récupération des pistes
                    recupererPistes(cd);

                    // Ouverture de la connexion à la BDD
                    cdDao = new CDDao(activity);
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

        // Récupération de l'id de l'album
        if(jsoRelease.has("id")) {
            cd.setIdAlbumEdition(jsoRelease.getString("id"));
        }

        // Récupération de l'id d'un album, commun à toutes ses rééditions (pour récupérer la date de sortie originale)
        if(jsoRelease.has("release-group")) {
            JSONObject jsoReleaseGroup = jsoRelease.getJSONObject("release-group");
            if(jsoReleaseGroup.has("id")) {
                cd.setIdAlbum(jsoReleaseGroup.getString("id"));
            }
        }
    }

    /**
     * Récupération de l'id de l'album dans le noeud release-group
     *
     * @param cd
     * @param jsoRelease
     * @throws JSONException
     */
    private void recupererIdAlbumCouvertureTracklist(CD cd, JSONObject jsoRelease) throws JSONException {
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
    private void recupererPochetteAlbum(CD cd) throws JSONException {
        // Récupération de la pochette
        String url = "http://coverartarchive.org/release/" +cd.getIdAlbumEdition();

        JSONObject responseJson = getJSONObjectFromURL(url);

        String urlCover = "";

        if(responseJson != null) {
            JSONArray images = responseJson.getJSONArray("images");

            boolean couvertureTrouvee = false;

            int i = 0;

            while(!couvertureTrouvee && i < images.length()) {
                JSONObject image = images.getJSONObject(i);

                if(image.has("front")) {
                    boolean frontCover = image.getBoolean("front");

                    if(frontCover) {
                        couvertureTrouvee = true;
                        if(image.has("thumbnails")) {
                            JSONObject jsoThumbnails = image.getJSONObject("thumbnails");

                            if(jsoThumbnails.has("small")) {
                                urlCover = jsoThumbnails.getString("small");
                            }
                        }
                    }
                }

                i++;
            }


            Bitmap pochette = null;

            InputStream in = null;
            try {
                in = new URL(urlCover).openStream();
                pochette = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(pochette != null) {
                cd.setPochette(UtilsBitmap.convertBitmapToBytesArray(pochette));
            } else {
                affecterPochetteDefaut(cd);
            }

        } else {
            affecterPochetteDefaut(cd);
        }

    }

    /**
     * Méthode qui affecte une pochette par défaut à un CD pour lequel la pochette n'a pas été trouvée
     *
     * @param cd
     */
    private void affecterPochetteDefaut(CD cd) {
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_album_defaut);
        cd.setPochette(UtilsBitmap.convertBitmapToBytesArray(bitmap));
    }

    /**
     * Récupération des pistes du CD
     *
     * @param cd
     */
    private void recupererPistes(CD cd) throws JSONException {
        // Récupération de la pochette
        String url = "https://musicbrainz.org/ws/2/release/" +cd.getIdAlbumEdition() +"?inc=recordings&fmt=json";

        JSONObject responseJson = getJSONObjectFromURL(url);

        if(responseJson != null) {
            JSONArray media = responseJson.getJSONArray("media");

            JSONObject jsoMedia = media.getJSONObject(0);

            if(jsoMedia.has("tracks")) {
                JSONArray tracks = jsoMedia.getJSONArray("tracks");
                ArrayList<CDPiste> listePistes = new ArrayList<CDPiste>();

                for(int i=0; i < tracks.length(); i++) {

                    JSONObject track = tracks.getJSONObject(i);

                    CDPiste piste = new CDPiste();
                    if(track.has("number") && track.has("title")) {
                        piste.setNumeroPiste(Integer.parseInt(track.getString("number")));
                        piste.setTitreChanson(track.getString("title"));
                        listePistes.add(piste);
                    }
                }
                cd.setListePistes(listePistes);

            }
        }

    }

}
