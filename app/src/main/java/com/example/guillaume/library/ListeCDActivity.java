package com.example.guillaume.library;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guillaume.library.Adapteurs.AdapteurListeCD;
import com.example.guillaume.library.Constantes.Constantes;
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

        // Clic sur un cd : on affiche le détail du cd sélectionné dans une nouvelle activité
        listeViewListeCD.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final CD cdSelectionne = (CD) adapterView.getItemAtPosition(position);
                lancerActiviteSelectionCD(cdSelectionne);
            }
        });

        // Clic long sur un livre
        listeViewListeCD.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                final CD cdSelectionne = (CD) adapterView.getItemAtPosition(position);

//                Toast.makeText(getApplicationContext(), "Clic long cd : " + cdSelectionne.getTitreAlbum(), Toast.LENGTH_SHORT).show();

                int suppr = cdDao.supprimerCD(cdSelectionne);

                if(suppr > 0) {
                    listeCDs.remove(cdSelectionne);

                    // Refresh de la vue
                    adapteurListeCD.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), "Suppression cd : " + cdSelectionne.getTitreAlbum(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
    }


    /**
     * Méthode appelée suite à un clic sur un item de la liste
     *
     * @param cdSelectionne
     */
    private void lancerActiviteSelectionCD(CD cdSelectionne) {
        Intent intent = new Intent(this, DetailCDActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(Constantes.CD_SELECT, cdSelectionne);
        intent.putExtras(extras);
        startActivity(intent);
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
