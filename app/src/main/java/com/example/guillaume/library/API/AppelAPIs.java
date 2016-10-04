package com.example.guillaume.library.API;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.guillaume.library.CommunActivity;
import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Exceptions.ReponseAPIException;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
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

    /**
     * Popup affichant un message pendant le chargement des infos
     */
    private ProgressDialog dialog;

    /**
     * application context.
     */
    private CommunActivity activity;

    /**
     * Lien avec la table CD
     */
    private CDDao cdDao;

    /**
     * CD en cours de traitement
     */
    private CD cd;

    /**
     * Livre en cours de traitement
     */
    private Livre livre;

    /**
     * Exception utilisée en cas d'erreur lors de la récupération des informations de l'élément flashé
     */
    private ReponseAPIException reponseAPIException = null;

    /**
     * Exception utilisée en cas de violation d'une contrainte sur la BDD
     */
    private SQLiteConstraintException sqLiteConstraintException = null;


    /**
     * Constructeur
     *
     * @param activity activité
     */
    public AppelAPIs(CommunActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }


    /**
     * Affichage des messages de chargement
     */
    protected void onPreExecute() {
        dialog.setTitle("Récupération des informations");
        dialog.setMessage("Veuillez patienter...");
        dialog.show();
    }

    /**
     * Recherche des informations à partir du CAB flashé
     *
     * @param params
     * @return
     */
    protected Boolean doInBackground(final String... params) {

        try {
            CDApi cdApi = new CDApi();
            cd = cdApi.traitementCd(activity, params[0]);

            if(cd == null) {
                LivreAPI livreApi = new LivreAPI();
                livre = livreApi.traitementLivre(activity, params[0]);
            }
        } catch (ReponseAPIException e) {
            e.printStackTrace();
            reponseAPIException = e;
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            sqLiteConstraintException = e;
        }

        return false;
    }

    /**
     *
     *
     * @param success
     */
    @Override
    protected void onPostExecute(final Boolean success) {

        // Suppression de la pop-up de progression si elle est affichée
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Toast affichant l'élément ajouté
        Toast toast = null;


        if(cd != null) {
            toast = Toast.makeText(activity, cd.getTitreAlbum() + " (de " +cd.getArtiste() + ") ajouté à la liste des CDs", Toast.LENGTH_LONG);
        } else if(livre != null) {
            toast = Toast.makeText(activity, livre.getTitre() + " (de " +livre.getAuteur() + ") ajouté à la liste des livres", Toast.LENGTH_LONG);
        } else if(reponseAPIException != null) {
            toast = Toast.makeText(activity, reponseAPIException.getMessage(), Toast.LENGTH_LONG);
        } else if(sqLiteConstraintException != null) {
            toast = Toast.makeText(activity, sqLiteConstraintException.getMessage(), Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(activity, "Aucune donnée trouvée", Toast.LENGTH_LONG);
        }

        toast.show();
    }
}
