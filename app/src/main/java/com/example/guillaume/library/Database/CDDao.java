package com.example.guillaume.library.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.ContactsContract;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 19/12/15.
 */
public class CDDao {

    /**
     * La BDD
     */
    private SQLiteDatabase database;

    /**
     * Classe de création et de mise à jour de la BDD
     */
    private DatabaseHelper databaseHelper;

    /**
     * Constructeur
     *
     * @param context
     */
    public CDDao(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }


    /**
     * Ouverture de la BDD
     *
     * @throws SQLiteException
     */
    public void openDatabase() throws SQLiteException {
        database = databaseHelper.getWritableDatabase();
    }

    /**
     * Fermeture de la BDD
     */
    public void closeDatabase() {
        database.close();
    }


    /**
     * Ajoute un cd dans la BDD
     *
     * @param cd
     */
    public void ajouterCD(CD cd) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_CD_ID_ALBUM, cd.getIdAlbum());
        contentValues.put(DatabaseHelper.COL_CD_TITRE_ALBUM, cd.getTitreAlbum());
        contentValues.put(DatabaseHelper.COL_CD_ARTISTE, cd.getArtiste());
        contentValues.put(DatabaseHelper.COL_CD_ANNEE_SORTIE, cd.getAnneSortie());
        contentValues.put(DatabaseHelper.COL_CD_POCHETTE, cd.getPochette());

        // Transformation de l'arraylist en jsonObject pour le stockage en base
        Gson gson = new Gson();
        String jsoListePistes = gson.toJson(cd.getListePistes());
        contentValues.put(DatabaseHelper.COL_CD_LISTE_PISTES, jsoListePistes);

        long insertId = database.insert(DatabaseHelper.TABLE_CD, null, contentValues);

    }


    public List<CD> selectionnerCDs() {
        List<CD> listeCDs = new ArrayList<CD>();


        String[] allColumns = { DatabaseHelper.COL_CD_ID_TECHNIQUE, DatabaseHelper.COL_CD_ID_ALBUM, DatabaseHelper.COL_CD_TITRE_ALBUM, DatabaseHelper.COL_CD_ARTISTE,
                DatabaseHelper.COL_CD_ANNEE_SORTIE, DatabaseHelper.COL_CD_POCHETTE, DatabaseHelper.COL_CD_LISTE_PISTES };


        Cursor cursor = database.query(DatabaseHelper.TABLE_CD, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CD cd = cursorToCD(cursor);
            listeCDs.add(cd);
            cursor.moveToNext();
        }
        cursor.close();

        return listeCDs;
    }

    private CD cursorToCD(Cursor cursor) {
        CD cd = new CD();
        cd.setIdAlbum(cursor.getString(1));
        cd.setTitreAlbum(cursor.getString(2));
        cd.setArtiste(cursor.getString(3));
        cd.setAnneSortie(cursor.getString(4));
        cd.setPochette(cursor.getBlob(5));

        String str = cursor.getString(6);
        // Désérialisation de la liste des pistes
        Gson gson = new Gson();
        List<CDPiste> listePistes = gson.fromJson(str, List.class);
        cd.setListePistes(listePistes);

        return cd;
    }
}
