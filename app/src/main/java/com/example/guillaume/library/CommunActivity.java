package com.example.guillaume.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guillaume.library.API.AppelAPIs;
import com.example.guillaume.library.API.GoogleBooksAPI;
import com.example.guillaume.library.API.MusicBrainzAPI;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class CommunActivity extends AppCompatActivity {

    private LivreDAO livreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commun);
    }

    protected void instancierFAB() {
        FloatingActionButton fabFlashCab = (FloatingActionButton)  findViewById(R.id.fabFlashCab);
        fabFlashCab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lancerScan();
            }
        });
    }

    /**
     * Méthode appelée lors d'un clic sur le bouton de scan
     */
    private void lancerScan() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(scanResult != null) {
            String scanContent = scanResult.getContents();

            // just call here the task
            AppelAPIs task = new AppelAPIs(this);
            task.execute(scanContent);

//            GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI(this);
//            googleBooksAPI.execute(scanContent).get();


//            if(scanContent!=null) {
//
//                // Première recherche : cd
//                CD cd = rechercherCD(scanContent);
//
//                if(cd != null) {
//                    // Un cd a été ajouté : on en informe l'utilisateur
//                    Toast toast = Toast.makeText(this, cd.getTitreAlbum() + " (de " +cd.getArtiste() + ") ajouté à la liste des CDs", Toast.LENGTH_LONG);
//                    toast.show();
//                } else {
//                    // Pas de cd trouvé, on cherche si un livre correspond au code à barre
//                    Livre livre = rechercherLivre(scanContent);
//
//                    if(livre != null) {
//                        // Un livre a été ajouté : on en informe l'utilisateur
//                        Toast toast = Toast.makeText(this, livre.getTitre() + " (de " +livre.getAuteur() + ") ajouté à la liste des livres", Toast.LENGTH_LONG);
//                        toast.show();
//                    } else {
//                        Toast toast = Toast.makeText(this, "Aucune donnée trouvée !", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }
//                }
//
//
//            } else {
//                Toast toast = Toast.makeText(this, "CAB non valide !", Toast.LENGTH_SHORT);
//                toast.show();
//            }


        } else {
            Toast toast = Toast.makeText(this, "Aucune donnée reçue lors du scan", Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    /**
     * Recherche d'un livre correspondant au code barre scanContent en appelant l'API GoogleBooks
     *
     * @param scanContent code barre
     * @return livre
     */
    private Livre rechercherLivre(String scanContent) {
        Livre livre = null;

        GoogleBooksAPI googleBooksAPI = new GoogleBooksAPI(this);
        try {
            livre = googleBooksAPI.execute(scanContent).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return livre;
    }


    /**
     * Recherche d'un CD correspondant au code barre scanContent en appelant l'API MusicBrainz
     *
     * @param scanContent code barre
     * @return CD
     */
    private CD rechercherCD(String scanContent) {
        CD cd = null;

        MusicBrainzAPI musicBrainzAPI = new MusicBrainzAPI(this);

        try {
            cd = musicBrainzAPI.execute(scanContent).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return cd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_commun, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class ProgressTask extends AsyncTask<String, Void, Boolean> {

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private CommunActivity activity;



        public ProgressTask(CommunActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }



        protected void onPreExecute() {
            this.dialog.setMessage("Progress start");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

        protected Boolean doInBackground(final String... args) {

            URL url = null;

            Livre livre = null;

            try {
//                9782266239066

                String cab = args[0];
                url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + cab);

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

//            return livre;


            return false;
        }


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
                livreDao = new LivreDAO(this);
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
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_inconnu);
        livre.setCouverture(UtilsBitmap.convertBitmapToBytesArray(bitmap));
    }



}

