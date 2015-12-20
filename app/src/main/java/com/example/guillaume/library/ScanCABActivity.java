package com.example.guillaume.library;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guillaume.library.API.GoogleBooksAPI;
import com.example.guillaume.library.API.MusicBrainzAPI;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class ScanCABActivity extends AppCompatActivity {

    private Button btnScan;

    private TextView txvScanFormat;

    private TextView txvScanContent;

    private TextView txvBookTitle;


    private TextView txvBookAuthor;

    private ImageView imvCouvertureLivre;


    private LivreDAO livreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_cab);

        txvScanFormat = (TextView) findViewById(R.id.txv_scan_format);
        txvScanContent = (TextView) findViewById(R.id.txv_scan_content);

        txvBookTitle = (TextView) findViewById(R.id.txv_book_title);
        txvBookAuthor = (TextView) findViewById(R.id.txv_book_author);
        imvCouvertureLivre = (ImageView) findViewById(R.id.imvCouvertureLivre);


        // Ouverture de la BDD
        livreDao = new LivreDAO(this);
        livreDao.openDatabase();

        // Action sur le clic du bouton musique
        btnScan = (Button) findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lancerScan();
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_cab, menu);
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
            String scanFormat = scanResult.getFormatName();
            txvScanContent.setText("Contenu = "+scanContent);
            txvScanFormat.setText("Format = "+scanFormat);

            if(scanContent!=null && scanFormat!=null) {

                // Première recherche : livre
                Livre livre = rechercherLivre(scanContent);


                // Si pas de livre trouvé, on cherche un CD
                if(livre == null) {
                    CD cd = rechercherCD(scanContent);

                }

            } else {
                Toast toast = Toast.makeText(this, "CAB non valide (pas un livre) !", Toast.LENGTH_SHORT);
                toast.show();
        }


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



    /**
     *
     * @return txvBookTitle
     */
    public TextView getTxvBookTitle() {
        return txvBookTitle;
    }

    /**
     *
     * @return txvBookAuthor
     */
    public TextView getTxvBookAuthor() {
        return txvBookAuthor;
    }


    @Override
    protected void onResume() {
        livreDao.openDatabase();
        super.onResume();
    }

    @Override
    protected void onPause() {
        livreDao.closeDatabase();
        super.onPause();
    }


    public ImageView getImvCouvertureLivre() {
        return imvCouvertureLivre;
    }

}
