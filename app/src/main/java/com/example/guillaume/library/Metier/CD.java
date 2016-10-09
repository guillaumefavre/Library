package com.example.guillaume.library.Metier;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume on 13/08/15.
 */
public class CD extends AbstractLibraryElement implements Parcelable {

    /**
     * Identifiant de l'album
     */
    private String idAlbum;


    /**
     * Identifiant d'une release particulière de l'album
     */
    private String idAlbumEdition;

    /**
     * Artiste ayant sorti l'album
     */
    private String artiste;

    /**
     * Titre de l'album
     */
    private String titreAlbum;

    /**
     * Date de sortie de l'album
     */
    private String dateSortie;

    /**
     * Pochette de l'album (int vers l'image mipmap)
     */
    private String pochette;


    /**
     * Liste des pistes
     */
    private List<CDPiste> listePistes;

    /**
     * Constructeur vide
     */
    public CD() {
    }

    /**
     * Constructeur
     *
     * @param artiste artiste
     * @param titreAlbum titre de l'album
     */
    public CD(final String artiste, final String titreAlbum) {
        this.artiste = artiste;
        this.titreAlbum = titreAlbum;
    }

    /**
     *
     * @return idAlbum
     */
    public String getIdAlbum() {
        return idAlbum;
    }

    /**
     *
     * @param idAlbum identifiant de l'album
     */
    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    /**
     *
     * @return idAlbumEdition identifiant de l'album
     */
    public String getIdAlbumEdition() {
        return idAlbumEdition;
    }

    /**
     *
     * @param idAlbumEdition identifiant de l'album
     */
    public void setIdAlbumEdition(String idAlbumEdition) {
        this.idAlbumEdition = idAlbumEdition;
    }

    /**
     *
     * @return artiste
     */
    public String getArtiste() {
        return artiste;
    }

    /**
     *
     * @param artiste artiste
     */
    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    /**
     *
     * @return titreAlbum
     */
    public String getTitreAlbum() {
        return titreAlbum;
    }

    /**
     *
     * @param titreAlbum titre de l'album
     */
    public void setTitreAlbum(String titreAlbum) {
        this.titreAlbum = titreAlbum;
    }

    /**
     *
     * @return dateSortie
     */
    public String getDateSortie() {
        return dateSortie;
    }

    /**
     *
     * @param dateSortie année de sortie de l'album
     */
    public void setDateSortie(String dateSortie) {
        this.dateSortie = dateSortie;
    }

    /**
     *
     * @return pochette
     */
    public String getPochette() {
        return pochette;
    }

    /**
     *
     * @param pochette pochette de l'album
     */
    public void setPochette(String pochette) {
        this.pochette = pochette;
    }


    /**
     *
     * @return listePistes
     */
    public List<CDPiste> getListePistes() {
        return listePistes;
    }

    /**
     *
     * @param listePistes liste des pistes de l'album
     */
    public void setListePistes(List<CDPiste> listePistes) {
        this.listePistes = listePistes;
    }

    /**
     * Permet de regénérer l'objet
     */
    public static final Parcelable.Creator<CD> CREATOR = new Creator<CD>() {
        @Override
        public CD createFromParcel(Parcel parcel) {
            CD cd = new CD();
            cd.setTitreAlbum(parcel.readString());
            cd.setArtiste(parcel.readString());
            cd.setDateSortie(parcel.readString());

            // Pochette
            cd.setPochette(parcel.readString());

            // Liste pistes
            cd.setListePistes(new ArrayList<CDPiste>());
            parcel.readTypedList(cd.getListePistes(), CDPiste.CREATOR);
            return cd;
        }

        @Override
        public CD[] newArray(int i) {
            return new CD[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titreAlbum);
        parcel.writeString(artiste);
        parcel.writeString(dateSortie);
        parcel.writeString(pochette);
        parcel.writeTypedList(listePistes);
    }
}
