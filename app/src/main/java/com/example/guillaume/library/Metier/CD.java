package com.example.guillaume.library.Metier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillaume on 13/08/15.
 */
public class CD implements Parcelable {

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
     * Année de sortie de l'album
     */
    private String anneSortie;

    /**
     * Pochette de l'album (int vers l'image mipmap)
     */
    private byte[] pochette;

    /**
     * Constructeur vide
     */
    public CD() {

    }

    /**
     * Constructeur
     *
     * @param artiste
     * @param titreAlbum
     */
    public CD(final String artiste, final String titreAlbum) {
        this.artiste = artiste;
        this.titreAlbum = titreAlbum;
    }

    /**
     * Constructeur
     *
     * @param artiste
     * @param titreAlbum
     */
    public CD(final String artiste, final String titreAlbum, final int pochette) {
        this.artiste = artiste;
        this.titreAlbum = titreAlbum;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }


    public String getIdAlbumEdition() {
        return idAlbumEdition;
    }

    public void setIdAlbumEdition(String idAlbumEdition) {
        this.idAlbumEdition = idAlbumEdition;
    }

    public String getArtiste() {
        return artiste;
    }

    /**
     *
     * @param artiste
     */
    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    /**
     *
     * @return
     */
    public String getTitreAlbum() {
        return titreAlbum;
    }

    /**
     *
     * @param titreAlbum
     */
    public void setTitreAlbum(String titreAlbum) {
        this.titreAlbum = titreAlbum;
    }

    /**
     *
     * @return
     */
    public String getAnneSortie() {
        return anneSortie;
    }

    /**
     *
     * @param anneSortie
     */
    public void setAnneSortie(String anneSortie) {
        this.anneSortie = anneSortie;
    }

    public byte[] getPochette() {
        return pochette;
    }

    /**
     *
     * @param pochette
     */
    public void setPochette(byte[] pochette) {
        this.pochette = pochette;
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
            cd.setPochette(new byte[parcel.readInt()]);
            parcel.readByteArray(cd.getPochette());
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
        if(pochette != null) {
            parcel.writeInt(pochette.length);
            parcel.writeByteArray(pochette);
        }

    }
}
