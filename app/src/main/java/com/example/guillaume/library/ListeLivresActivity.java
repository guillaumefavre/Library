package com.example.guillaume.library;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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

import com.example.guillaume.library.Adapteurs.AdapteurListeLivres;
import com.example.guillaume.library.Comparator.CDComparator;
import com.example.guillaume.library.Comparator.LivreComparator;
import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;

import java.util.Collection;
import java.util.List;

public class ListeLivresActivity extends CommunActivity {

    /**
     * Liste des Livres à afficher
     */
    private List<Livre> listeLivres;

    /**
     * Liste view représentant la liste des livres à afficher
     */
    private ListView listeViewListeLivres;

    /**
     * Adapteur pour la liste view
     */
    private AdapteurListeLivres adapteurListeLivres;

    private LivreDAO livreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_livres);


        // ouverture connextion BDD
        livreDAO = new LivreDAO(this);
        livreDAO.openDatabase();

        listeLivres = livreDAO.selectionnerLivres();


        adapteurListeLivres = new AdapteurListeLivres(this, listeLivres);

        // Tri de la liste des livres
        adapteurListeLivres.sort(new LivreComparator());

        listeViewListeLivres = (ListView) findViewById(R.id.lstvListeLivres);
        listeViewListeLivres.setAdapter(adapteurListeLivres);


        // Clic sur un livre : on affiche le détail du livre sélectionné dans une nouvelle activité
        listeViewListeLivres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Livre livreSelectionne = (Livre) adapterView.getItemAtPosition(position);
                lancerActiviteSelectionLivre(livreSelectionne);
            }
        });

        // Autorisation de la sélection de plusieurs items
        listeViewListeLivres.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);



        listeViewListeLivres.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                // On cache la toolbar par défaut de l'écran
                getSupportActionBar().hide();

                // On affiche la toolbar spécifique suite à un clic long sur un item
                getMenuInflater().inflate(R.menu.menu_liste_livres, menu);

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

                final Livre livreSelectionne = (Livre) listeViewListeLivres.getItemAtPosition(positionItem);

                if(checked) {
                    // Ajout de l'élément sélectionné à la liste
                    adapteurListeLivres.addItemSelectionne(positionItem, livreSelectionne);
                } else {
                    // Suppression de l'élément désélectionné de la liste
                    adapteurListeLivres.removeItemSelectionne(positionItem);
                }
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                int id = menuItem.getItemId();

                // Respond to clicks on the actions in the CAB
                switch (id) {
                    case R.id.action_supprimer:
                        Collection<Livre> livresASupprimer = adapteurListeLivres.getLivresSelectionnes();
                        supprimerLivres(livresASupprimer);
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
                adapteurListeLivres.clearSelection();
                getSupportActionBar().show();
            }
        });





        // Clic long sur un livre => suppression
        listeViewListeLivres.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                final Livre livreSelectionne = (Livre) adapterView.getItemAtPosition(position);

                int suppr = livreDAO.supprimerLivre(livreSelectionne);

                if(suppr > 0) {
                    listeLivres.remove(livreSelectionne);

                    // Refresh de la vue
                    adapteurListeLivres.notifyDataSetChanged();

                    Toast.makeText(getApplicationContext(), "Suppression livre : " + livreSelectionne.getTitre(), Toast.LENGTH_SHORT).show();
                }


                return true;
            }
        });



        // Action lors du clic sur le FAB
        instancierFAB();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_livre);
        // On indique à la toolbar d'agir comme une actionbar
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }


    /**
     * Suppression d'une liste de Livres
     *
     * @param livresASupprimer
     */
    private void supprimerLivres(Collection<Livre> livresASupprimer) {

        for(Livre livre : livresASupprimer) {
            int suppr = livreDAO.supprimerLivre(livre);

            if(suppr > 0) {
                listeLivres.remove(livre);

                Toast.makeText(getApplicationContext(), "Suppression livre : " + livre.getTitre(), Toast.LENGTH_SHORT).show();
            }

            // Refresh de la vue
            adapteurListeLivres.notifyDataSetChanged();
        }
    }

    /**
     * Méthode appelée suite à un clic sur un item de la liste
     *
     * @param livreSelectionne
     */
    private void lancerActiviteSelectionLivre(Livre livreSelectionne) {
        Intent intent = new Intent(this, DetailLivreActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(Constantes.LIVRE_SELECT, livreSelectionne);
        intent.putExtras(extras);


        // Transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent,bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_liste_livres, menu);
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
