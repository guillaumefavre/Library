package com.example.guillaume.library.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.guillaume.library.Metier.Livre;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 04/10/15.
 */
public class LivreDAO {

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
    public LivreDAO(Context context) {
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
     * Ajoute un livre dans la BDD
     *
     * @param livre
     */
    public void ajouterLivre(Livre livre) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_TITRE, livre.getTitre());
        contentValues.put(DatabaseHelper.COL_AUTEUR, livre.getAuteur());
        contentValues.put(DatabaseHelper.COL_DESCRIPTION, livre.getDescription());
        long insertId = database.insert(DatabaseHelper.TABLE_LIVRE, null, contentValues);

    }


    public List<Livre> selectionnerLivres() {
        List<Livre> listeLivres = new ArrayList<Livre>();


        String[] allColumns = { DatabaseHelper.COL_ID, DatabaseHelper.COL_TITRE, DatabaseHelper.COL_AUTEUR, DatabaseHelper.COL_DESCRIPTION };


        Cursor cursor = database.query(DatabaseHelper.TABLE_LIVRE, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Livre livre = cursorToLivre(cursor);
            listeLivres.add(livre);
            cursor.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        cursor.close();

        return listeLivres;
    }

    private Livre cursorToLivre(Cursor cursor) {
        Livre livre = new Livre();
//        livre.setId(cursor.getLong(0));
        livre.setTitre(cursor.getString(1));
        livre.setAuteur(cursor.getString(2));
        livre.setDescription(cursor.getString(3));
        return livre;
    }
}
