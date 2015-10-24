package com.example.guillaume.library.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by guillaume on 04/10/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Nom de la base de données
     */
    public static final String DATABASE_NAME = "library.db";

    /**
     * Numéro de version de la BDD
     */
    public static final int DATABASE_VERSION = 2;

    /**
     * Table Livre
     */
    public static final String TABLE_LIVRE = "Livre";

    /**
     * Colonne id
     */
    public static final String COL_ID = "_id";

    /**
     * Colonne auteur
     */
    public static final String COL_AUTEUR = "auteur";

    /**
     * Colonne titre
     */
    public static final String COL_TITRE = "titre";

    /**
     * Colonne description
     */
    public static final String COL_DESCRIPTION = "description";


    // Création de la table livre
    private static final String CREATE_TABLE_LIVRE = "CREATE TABLE " + TABLE_LIVRE
            + "(" + COL_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITRE + " TEXT NOT NULL, " + COL_AUTEUR +" TEXT, " + COL_DESCRIPTION +" TEXT);";




    /**
     * Constructeur
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_LIVRE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Mise à jour de la version " + oldVersion + " vers la version " + newVersion + ", les anciennes données seront détruites.");

        // Suppression de la table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVRE);

        // Création de la nouvelle version
        onCreate(sqLiteDatabase);
    }


}
