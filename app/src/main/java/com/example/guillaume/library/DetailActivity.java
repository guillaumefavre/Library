package com.example.guillaume.library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Metier.Livre;

public class DetailActivity extends AppCompatActivity {

    /**
     * Layout d'un Livre
     */
    private LinearLayout layoutLivre;

    private TextView txvTitreLivre;

    private TextView txvAuteurLivre;

    private TextView txvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        layoutLivre = (LinearLayout) findViewById(R.id.layoutLivre);

        txvTitreLivre = (TextView) findViewById(R.id.txv_titre_livre);

        txvAuteurLivre = (TextView) findViewById(R.id.txv_auteur_livre);

        txvDescription = (TextView) findViewById(R.id.txv_description_livre);

        Livre livre = (Livre) getIntent().getParcelableExtra(Constantes.LIVRE_SELECT);

        txvTitreLivre.setText(livre.getTitre());
        txvAuteurLivre.setText(livre.getAuteur());
        txvDescription.setText(livre.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
