package com.example.guillaume.library;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guillaume.library.Constantes.Constantes;
import com.example.guillaume.library.Metier.Livre;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    /**
     * Layout d'un Livre
     */
    private LinearLayout layoutLivre;

    private TextView txvTitreLivre;

    private TextView txvAuteurLivre;

    private TextView txvDescription;

    private ImageView imvCouvertureLivre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        layoutLivre = (LinearLayout) findViewById(R.id.layoutLivre);

        txvTitreLivre = (TextView) findViewById(R.id.txv_titre_livre);

        txvAuteurLivre = (TextView) findViewById(R.id.txv_auteur_livre);

        imvCouvertureLivre = (ImageView) findViewById(R.id.imvCouvertureLivre);

        txvDescription = (TextView) findViewById(R.id.txv_description_livre);

        Livre livre = (Livre) getIntent().getParcelableExtra(Constantes.LIVRE_SELECT);

        txvTitreLivre.setText(livre.getTitre());
        txvAuteurLivre.setText(livre.getAuteur());
        txvDescription.setText(livre.getDescription());
        imvCouvertureLivre.setImageBitmap(UtilsBitmap.convertByteArrayToBitmap(livre.getCouverture()));

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

    public ImageView getImvCouvertureLivre() {
        return imvCouvertureLivre;
    }
}
