package com.example.guillaume.library;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guillaume.library.Adapteurs.AdapteurListeCD;
import com.example.guillaume.library.Comparator.CDComparator;
import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Database.CDDao;
import com.example.guillaume.library.Metier.CD;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListeCDActivity extends CommunActivity {

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

        // Tri de la liste des CDs
        adapteurListeCD.sort(new CDComparator());

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


        // AUtorisation de la sélection de plusieurs items
        listeViewListeCD.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listeViewListeCD.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /**
             * Liste des CDs sélectionnés
             */
            private List<CD> listeCDSelectionnes;

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                // On cache la toolbar par défaut de l'écran
                getSupportActionBar().hide();

                // On affiche la toolbar spécifique suite à un clic long sur un item
                getMenuInflater().inflate(R.menu.menu_liste_cd, menu);

                listeCDSelectionnes = new ArrayList<CD>();

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int positionItem, long id, boolean checked) {

                final CD cdSelectionne = (CD) listeViewListeCD.getItemAtPosition(positionItem);

                if(checked) {
                    // Ajout de l'élément sélectionné à la liste
                    listeCDSelectionnes.add(cdSelectionne);
                } else {
                    // Suppression de l'élément désélectionné de la liste
                    listeCDSelectionnes.remove(cdSelectionne);
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                int id = menuItem.getItemId();

                // Respond to clicks on the actions in the CAB
                switch (id) {
                    case R.id.action_supprimer:
                        supprimerCds(listeCDSelectionnes);
                        actionMode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.

                // On réaffiche la toolbar par défaut de l'écran
                getSupportActionBar().show();
            }
        });


        // Action lors du clic sur le FAB
        instancierFAB();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cd);
        // On indique à la toolbar d'agir comme une actionbar
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    /**
     * Suppression d'une liste de CDs
     *
     * @param cdsASupprimer
     */
    private void supprimerCds(List<CD> cdsASupprimer) {

        for(CD cd : cdsASupprimer) {
            int suppr = cdDao.supprimerCD(cd);

            if(suppr > 0) {
                listeCDs.remove(cd);

                Toast.makeText(getApplicationContext(), "Suppression cd : " + cd.getTitreAlbum(), Toast.LENGTH_SHORT).show();
            }

            // Refresh de la vue
            adapteurListeCD.notifyDataSetChanged();
        }
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
//        getMenuInflater().inflate(R.menu.menu_liste_cd, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
