package com.example.guillaume.library;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.guillaume.library.API.GoogleBooksAPI;
import com.example.guillaume.library.API.MusicBrainzAPI;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;


public class CommunActivity extends AppCompatActivity {

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

            if(scanContent!=null) {

                // Première recherche : cd
                CD cd = rechercherCD(scanContent);

                if(cd != null) {
                    // Un cd a été ajouté : on en informe l'utilisateur
                    Toast toast = Toast.makeText(this, cd.getTitreAlbum() + " (de " +cd.getArtiste() + ") ajouté à la liste des CDs", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    // Pas de cd trouvé, on cherche si un livre correspond au code à barre
                    Livre livre = rechercherLivre(scanContent);

                    if(livre != null) {
                        // Un livre a été ajouté : on en informe l'utilisateur
                        Toast toast = Toast.makeText(this, livre.getTitre() + " (de " +livre.getAuteur() + ") ajouté à la liste des livres", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }


            } else {
                Toast toast = Toast.makeText(this, "CAB non valide !", Toast.LENGTH_SHORT);
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
}
