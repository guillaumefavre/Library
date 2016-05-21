package com.example.guillaume.library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.guillaume.library.API.AppelAPIs;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Classe gérant les composants communs (ex : FAB permettant de flasher un CAB)
 */
public class CommunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commun);
    }

    protected void instancierFAB() {
        FloatingActionButton fabFlashCab = (FloatingActionButton)  findViewById(R.id.fabFlashCab);
        fabFlashCab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lancerScan();
            }
        });
    }

    /**
     * Méthode appelée lors d'un clic sur le bouton de scan
     */
    private void lancerScan() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(scanResult != null) {
            String scanContent = scanResult.getContents();

            // just call here the task
            AppelAPIs task = new AppelAPIs(this);
            task.execute(scanContent);

        } else {
            Toast toast = Toast.makeText(this, "Aucune donnée reçue lors du scan", Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_commun, menu);
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

