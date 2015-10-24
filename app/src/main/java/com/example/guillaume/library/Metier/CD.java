package com.example.guillaume.library.Metier;

/**
 * Created by guillaume on 13/08/15.
 */
public class CD {

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
    private int anneSortie;

    /**
     * Pochette de l'album (int vers l'image mipmap)
     */
    private int pochette;

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
     * @param pochette
     */
    public CD(final String artiste, final String titreAlbum, final int pochette) {
        this.artiste = artiste;
        this.titreAlbum = titreAlbum;
        this.pochette = pochette;
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

    public int getAnneSortie() {
        return anneSortie;
    }

    public void setAnneSortie(int anneSortie) {
        this.anneSortie = anneSortie;
    }

    public int getPochette() {
        return pochette;
    }

    public void setPochette(int pochette) {
        this.pochette = pochette;
    }
}
