package com.example.guillaume.library.API;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Exceptions.ReponseAPIException;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.example.guillaume.library.R;
import com.example.guillaume.library.Utils.BitmapUtils;

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

/**
 * Created by guillaume on 04/10/16.
 */

public class CDApi {


    /**
     * application context.
     */
    private Context context;

    /**
     * Lien avec la table Livre
     */
    private CDDao cdDao;


    public CD traitementCd(Context context, String cab) throws ReponseAPIException {

        this.context = context;


        // Recherche du livre via l'API
        CD cd = rechercherCD(cab);

        if(cd != null) {
            // Enregistrement du livre en base
            enregistrerLivreBdd(cd);
        }

        return cd;
    }








    private CD rechercherCD(String cab) throws ReponseAPIException, SQLiteConstraintException {

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
//            throw new ReponseAPIException();
        } catch (SQLiteConstraintException e) {
            throw e;
        }


        return cd;
    }


    /**
     * Construction du CD à partir des données renvoyée par l'appel des URLs.
     *
     * @param responseJson
     * @return
     */
    private CD construireCd(JSONObject responseJson) throws ReponseAPIException, SQLiteConstraintException {
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

                    if(jsoReleaseGroup != null) {

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
                    }




                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return cd;
    }

    /**
     * Enregistrement d'un CD en base
     *
     * @param cd cd à enregistrer
     */
    private void enregistrerLivreBdd(CD cd) {

        // Ouverture de la connexion à la BDD
        cdDao = new CDDao(context);
        cdDao.openDatabase();

        // Ajout du CD à la base
        cdDao.ajouterCD(cd);

        // Fermeture de la connexion à la BDD
        if(cdDao != null) {
            cdDao.closeDatabase();

        }
    }


    /**
     * Méthode qui récupère l'objet JSON renvoyé par l'URL passé en apramètre.
     *
     * @param strUrl
     * @return
     */
    private JSONObject getJSONObjectFromURL(String strUrl) throws ReponseAPIException {

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

            } else {
                // TODO gestion autres codes retours
                throw new ReponseAPIException();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ReponseAPIException();
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
            cd.setDateSortie(jsoReleaseGroup.getString("first-release-date"));
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
    private void recupererPochetteAlbum(CD cd) throws JSONException, ReponseAPIException {
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
                cd.setPochette(BitmapUtils.convertBitmapEncodedBase64String(pochette));
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
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_album_defaut);
        cd.setPochette(BitmapUtils.convertBitmapEncodedBase64String(bitmap));
    }

    /**
     * Récupération des pistes du CD
     *
     * @param cd
     */
    private void recupererPistes(CD cd) throws JSONException, ReponseAPIException {
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
                        piste.setTitre(track.getString("title"));

                        if(track.has("length")) {
                            String duree = calculerDuree(Integer.valueOf(track.getString("length")));
                            piste.setDuree(duree);
                        }

                        listePistes.add(piste);
                    }
                }
                cd.setListePistes(listePistes);

            }
        }

    }

    /**
     * Calcul de la durée d'une piste (au format mm:ss) à partir d'un nombre de millisecondes
     *
     * @param dureeMilliSecondes durée en millisecondes
     * @return duree (format mm:ss)
     */
    private String calculerDuree(int dureeMilliSecondes) {
        String duree;

        int dureeSecondes = dureeMilliSecondes / 1000;
        int minutes = dureeSecondes / 60;
        int secondes = dureeSecondes % 60;

        // Formatage pour que le nombre de secondes soit sur deux chiffres
        duree = String.valueOf(minutes) + ":" + String.format("%02d", secondes);

        return duree;
    }

}
