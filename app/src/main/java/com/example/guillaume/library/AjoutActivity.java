package com.example.guillaume.library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guillaume.library.Constantes.Constantes;

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
