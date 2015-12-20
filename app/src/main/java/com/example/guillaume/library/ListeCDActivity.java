package com.example.guillaume.library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.guillaume.library.Adapteurs.AdapteurListeCD;
import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Metier.CD;

import java.util.ArrayList;
import java.util.List;

public class ListeCDActivity extends AppCompatActivity {

    /**
     * Liste des CDs à afficher
     */
    private List<CD> listeCDs;

    /**
     * Liste view représentant la liste des CDs à afficher
     */
    private ListView listeViewListeCD;

    /**
     * Adapteur pour la liste view
     */
    private AdapteurListeCD adapteurListeCD;


    /**
     * CD Dao
     */
    private CDDao cdDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_cd);

        // ouverture connextion BDD
        cdDao = new CDDao(this);
        cdDao.openDatabase();


        listeCDs = cdDao.selectionnerCDs();



        adapteurListeCD = new AdapteurListeCD(this, listeCDs);

        listeViewListeCD = (ListView) findViewById(R.id.lstvListeCd);
        listeViewListeCD.setAdapter(adapteurListeCD);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_liste_cd, menu);
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
