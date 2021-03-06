package com.example.guillaume.library.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.guillaume.library.Exceptions.UniqueConstraintException;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.example.guillaume.library.Utils.FormatUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
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
    public void ajouterCD(CD cd) throws UniqueConstraintException {

        ContentValues contentValues = new ContentValues();
        if(StringUtils.isBlank(cd.getIdAlbum())) {
            cd.setIdAlbum(FormatUtils.genererIdentifiantLivre(cd.getArtiste(), cd.getTitreAlbum()));
        }
        contentValues.put(DatabaseHelper.COL_CD_ID_ALBUM, cd.getIdAlbum());

        contentValues.put(DatabaseHelper.COL_CD_TITRE_ALBUM, cd.getTitreAlbum());
        contentValues.put(DatabaseHelper.COL_CD_ARTISTE, cd.getArtiste());
        contentValues.put(DatabaseHelper.COL_CD_ANNEE_SORTIE, cd.getDateSortie());
        contentValues.put(DatabaseHelper.COL_CD_POCHETTE, cd.getPochette());

        // Transformation de l'arraylist en jsonObject pour le stockage en base
        Gson gson = new Gson();
        String jsoListePistes = gson.toJson(cd.getListePistes());
        contentValues.put(DatabaseHelper.COL_CD_LISTE_PISTES, jsoListePistes);

        try {
            long insertId = database.insertOrThrow(DatabaseHelper.TABLE_CD, null, contentValues);
        } catch(SQLiteConstraintException ex) {
            if(ex.getMessage().contains("UNIQUE constraint failed")) {
                throw new UniqueConstraintException(cd);
            } else {
                throw ex;
            }
        }


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
        cd.setDateSortie(cursor.getString(4));
        cd.setPochette(cursor.getString(5));

        String str = cursor.getString(6);
        // Désérialisation de la liste des pistes
        Gson gson = new Gson();

        Type listType = new TypeToken<ArrayList<CDPiste>>() {}.getType();
        List<CDPiste> listePistes = gson.fromJson(str, listType);

//        CDPiste[] listePistes = gson.fromJson(str, CDPiste[].class);

        cd.setListePistes(listePistes);

        return cd;
    }

    public int supprimerCD(CD cdASupprimer) {

        // Suppression du livre
        return database.delete(DatabaseHelper.TABLE_CD, DatabaseHelper.COL_CD_ID_ALBUM + "=" +"'" +cdASupprimer.getIdAlbum() +"'", null);
    }
}
