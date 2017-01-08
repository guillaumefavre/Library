package com.example.guillaume.library;

import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Database.LivreDAO;
import com.example.guillaume.library.Metier.AbstractLibraryElement;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.Utils.BitmapUtils;
import com.example.guillaume.library.Utils.FormatUtils;

public class AjoutActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * Spinner représentant la liste des médias que l'on peut ajouter
     */
    private Spinner spinnerTypesMedias;

    /**
     * Layout de l'ajout d'un Livre
     */
    private LinearLayout layoutAjoutLivre;

    /**
     * Layout de l'ajout d'un CD
     */
    private LinearLayout layoutAjoutCd;

    /**
     * Layout de l'ajout d'un DVD
     */
    private LinearLayout layoutAjoutDVD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout);

        spinnerTypesMedias = (Spinner) findViewById(R.id.spnlisteTypesMedias);

        layoutAjoutLivre = (LinearLayout) findViewById(R.id.layoutAjoutLivre);
        layoutAjoutCd = (LinearLayout) findViewById(R.id.layoutAjoutCd);
        layoutAjoutDVD = (LinearLayout) findViewById(R.id.layoutAjoutDVD);

        // Création de l'adapter à partir de la liste de ressources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types_medias_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTypesMedias.setAdapter(adapter);

        // Ajout du listener
        spinnerTypesMedias.setOnItemSelectedListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // On indique à la toolbar d'agir comme une actionbar
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ajout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_enregistrer) {
            String media = String.valueOf(spinnerTypesMedias.getSelectedItem());

            traitementSaisie(media);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Traitement de la saisie du nouveau media ajouté
     *
     * @param media
     */
    private void traitementSaisie(String media) {
        switch (media) {
            case "Livre":
                EditText edtAuteurLivre = (EditText) findViewById(R.id.layoutLivre_auteur);
                EditText edtTitreLivre = (EditText) findViewById(R.id.layoutLivre_titre);

                Livre livre = new Livre();
                livre.setAuteur(edtAuteurLivre.getText().toString());
                livre.setTitre(edtTitreLivre.getText().toString());

                // Couverture par défaut
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_inconnu);
                livre.setCouverture(BitmapUtils.convertBitmapEncodedBase64String(bitmap));
                enregistrerMedia(livre);
                break;
            case "CD":
                EditText edtArtisteCd = (EditText) findViewById(R.id.layoutCd_artiste);
                EditText edtTitreCd = (EditText) findViewById(R.id.layoutCd_titre);
                EditText edtAnneeCd = (EditText) findViewById(R.id.layoutCd_titre);

                CD cd = new CD();
                cd.setArtiste(edtArtisteCd.getText().toString());
                cd.setTitreAlbum(edtTitreCd.getText().toString());
                cd.setDateSortie(edtAnneeCd.getText().toString());

                enregistrerMedia(cd);
                break;
            default:
                break;
        }
    }

    /**
     * Enregistrement du nouveau media en base
     *
     * @param media
     */
    private void enregistrerMedia(AbstractLibraryElement media) {
        Toast toast = null;

        if(media instanceof Livre) {

            LivreDAO livreDao = new LivreDAO(this);
            livreDao.openDatabase();

            try {
                // Ajout du livre en base
                livreDao.ajouterLivre((Livre) media);

                toast = Toast.makeText(this, "Enregistrer livre...", Toast.LENGTH_SHORT);
            } catch (SQLiteConstraintException e) {
                // Gestion des exceptions (doublon en base...)
                toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }finally {
                // Fermeture de la connexion à la BDD
                if(livreDao != null) {
                    livreDao.closeDatabase();
                }
            }

        } else if(media instanceof CD) {
            toast = Toast.makeText(this, "Enregistrer CD...", Toast.LENGTH_SHORT);
        }

        toast.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        TextView selection = (TextView) view;


        // Affichage du bon layout en fonction de l'item sélectionné
        if(Constantes.LIVRE.equals(selection.getText())) {
            // Ajout d'un livre
            layoutAjoutLivre.setVisibility(View.VISIBLE);
            layoutAjoutCd.setVisibility(View.GONE);
            layoutAjoutDVD.setVisibility(View.GONE);
        } else if(Constantes.CD.equals(selection.getText())) {
            // Ajout d'un CD
            layoutAjoutLivre.setVisibility(View.GONE);
            layoutAjoutCd.setVisibility(View.VISIBLE);
            layoutAjoutDVD.setVisibility(View.GONE);
        } else if(Constantes.DVD.equals(selection.getText())) {
            // Ajout d'un DVD
            layoutAjoutLivre.setVisibility(View.GONE);
            layoutAjoutCd.setVisibility(View.GONE);
            layoutAjoutDVD.setVisibility(View.VISIBLE);
        }




    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
