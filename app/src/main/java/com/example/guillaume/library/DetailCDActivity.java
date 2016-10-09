package com.example.guillaume.library;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guillaume.library.Adapteurs.AdapteurListeCD;
import com.example.guillaume.library.Adapteurs.AdapteurListePistesCD;
import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.example.guillaume.library.Utils.BitmapUtils;

public class DetailCDActivity extends AppCompatActivity {

    /**
     * Layout d'un Livre
     */
    private LinearLayout layout;

    private TextView txvLigne1;

    private TextView txvLigne2;

    private TextView txvLigne3;

    private ImageView imvCouverturePochette;

    /**
     * Liste view représentant la liste des pistes du CD
     */
    private ListView listeViewListePistes;

    /**
     * Adapteur pour la liste view
     */
    private AdapteurListePistesCD adapteurListePistesCD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_cd);

        CD cd = (CD) getIntent().getParcelableExtra(Constantes.CD_SELECT);

        layout = (LinearLayout) findViewById(R.id.layoutEnTete);

        txvLigne1 = (TextView) findViewById(R.id.txv_ligne1);

        txvLigne2 = (TextView) findViewById(R.id.txv_ligne2);

        txvLigne3 = (TextView) findViewById(R.id.txv_ligne3);

        imvCouverturePochette = (ImageView) findViewById(R.id.imvCouverturePochette);

        // listview des pistes
        adapteurListePistesCD = new AdapteurListePistesCD(this, cd.getListePistes());
        listeViewListePistes = (ListView) findViewById(R.id.lstvPistes);
        listeViewListePistes.setAdapter(adapteurListePistesCD);


        txvLigne1.setText(cd.getTitreAlbum());
        txvLigne2.setText(cd.getArtiste());
        txvLigne3.setText(cd.getDateSortie());


        if(cd.getPochette() != null) {
            imvCouverturePochette.setImageBitmap(BitmapUtils.convertEncodedBase64StringToBitmap(cd.getPochette()));
        }
    }


    private String afficherListePistes(CD cd) {

        String listePistes = "";

        if(cd.getListePistes() != null) {
            for(final CDPiste piste : cd.getListePistes()) {
                listePistes += piste.getNumeroPiste() + " " + piste.getTitre() +" " + piste.getDuree() + "\n";
            }
        }

        return listePistes;
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
