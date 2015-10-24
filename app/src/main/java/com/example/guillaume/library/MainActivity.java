package com.example.guillaume.library;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnLivre;

    private ImageButton btnMusique;

    private ImageButton btnFilm;

    private ImageButton btnCodeBarre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Action sur le clic du bouton musique
        btnMusique = (ImageButton) findViewById(R.id.btnMusique);
        btnMusique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lancerActiviteListeCD();
            }
        });

        // Action sur le clic du bouton livre
        btnLivre = (ImageButton) findViewById(R.id.btnLivre);
        btnLivre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lancerActiviteListeLivres();
            }
        });

        // Action sur le clic du bouton code barre
        btnCodeBarre = (ImageButton) findViewById(R.id.btnCodeBarre);
        btnCodeBarre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lancerActiviteScanCAB();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accueil, menu);
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
     * Méthode qui lance l'activité affichant la liste des CDs
     */
    private void lancerActiviteListeCD() {
        Intent intent = new Intent(this, ListeCDActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode qui lance l'activité affichant la liste des livres
     */
    private void lancerActiviteListeLivres() {
        Intent intent = new Intent(this, ListeLivresActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode qui lance l'activité permettant de scanner un code à barres
     */
    private void lancerActiviteScanCAB() {
        Intent intent = new Intent(this, ScanCABActivity.class);
        startActivity(intent);
    }
}
