package com.example.guillaume.library.API;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.guillaume.library.CommunActivity;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.R;
import com.example.guillaume.library.Utils.BitmapUtils;
import com.example.guillaume.library.Utils.FormatUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by guillaume on 04/10/16.
 */

public class LivreAPI {

    /**
     * application context.
     */
    private Context context;

    /**
     * Lien avec la table Livre
     */
    private LivreDAO livreDao;


    public Livre traitementLivre(Context context, String cab) {

        this.context = context;


        // Recherche du livre via l'API
        Livre livre = rechercherLivre(cab);

        if(livre != null) {
            // Enregistrement du livre en base
            enregistrerLivreBdd(livre);
        }

        return livre;
    }

    /**
     * Appel de l'URL permetatnt de récupérer les informations d'un livre
     *
     * @param cab
     * @return
     */
    private Livre rechercherLivre(String cab) throws SQLiteConstraintException {


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
    private Livre construireLivre(JSONObject jsonObject) throws SQLiteConstraintException {
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

                recupererIdentifiantLivre(livre);

                // Récupération de la description
                recupererDescription(livre, jsoVolumeInfo);

                // Récupération de la couverture
                recupererCouverture(livre, jsoVolumeInfo);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return livre;

    }

    /**
     * Enregistrement d'un livre en base
     *
     * @param livre livre à enregistrer
     */
    private void enregistrerLivreBdd(Livre livre) {

        // ouverture connextion BDD
        livreDao = new LivreDAO(context);
        livreDao.openDatabase();

        livreDao.ajouterLivre(livre);

        // Fermeture de la connexion à la BDD
        if(livreDao != null) {
            livreDao.closeDatabase();

        }
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
     * Récupération de l'identifiant fonctionnel du livre
     *
     * @param livre
     */
    private void recupererIdentifiantLivre(final Livre livre) {
        livre.setIdentifiantFonctionnel(FormatUtils.genererIdentifiantLivre(livre.getAuteur(), livre.getTitre()));
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
                    livre.setCouverture(BitmapUtils.convertBitmapEncodedBase64String(couverture));
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
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_inconnu);
        livre.setCouverture(BitmapUtils.convertBitmapEncodedBase64String(bitmap));
    }
}
