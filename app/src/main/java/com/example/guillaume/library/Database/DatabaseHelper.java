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
    public static final int DATABASE_VERSION = 1;

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

    /**
     * Colonne couverture
     */
    public static final String COL_COUVERTURE = "couverture";

    /**
     * Table Livre
     */
    public static final String TABLE_CD = "CD";

    /**
     * Colonne id technique de la table CD
     */
    public static final String COL_CD_ID_TECHNIQUE = "_id";

    /**
     * Colonne identifiant album de la table CD
     */
    public static final String COL_CD_ID_ALBUM = "idAlbum";

    /**
     * Colonne artiste de la table CD
     */
    public static final String COL_CD_ARTISTE = "artiste";

    /**
     * Colonne titre de la table CD
     */
    public static final String COL_CD_TITRE_ALBUM = "titreAlbum";

    /**
     * Colonne année sortie de la table CD
     */
    public static final String COL_CD_ANNEE_SORTIE = "anneeSortie";

    /**
     * Colonne pochette de la table CD
     */
    public static final String COL_CD_POCHETTE = "pochette";



    // Création de la table livre
    private static final String CREATE_TABLE_LIVRE = "CREATE TABLE " + TABLE_LIVRE
            + "(" + COL_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_TITRE + " TEXT NOT NULL, " + COL_AUTEUR +" TEXT, " + COL_DESCRIPTION +" TEXT, "+ COL_COUVERTURE +" BLOB);";


    // Création de la table CD
    // TODO ajouter contrainte d'unicité sur COL_CD_ID_ALBUM
    private static final String CREATE_TABLE_CD = "CREATE TABLE " + TABLE_CD
            + "(" + COL_CD_ID_TECHNIQUE + "  INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_CD_ID_ALBUM + " TEXT NOT NULL, "
            + COL_CD_TITRE_ALBUM + " TEXT NOT NULL, " + COL_CD_ARTISTE +" TEXT, " + COL_CD_ANNEE_SORTIE +" TEXT, "+ COL_CD_POCHETTE +" BLOB);";


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
        sqLiteDatabase.execSQL(CREATE_TABLE_CD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), "Mise à jour de la version " + oldVersion + " vers la version " + newVersion + ", les anciennes données seront détruites.");

        // Suppression de la table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVRE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CD);

        // Création de la nouvelle version
        onCreate(sqLiteDatabase);
    }


}
