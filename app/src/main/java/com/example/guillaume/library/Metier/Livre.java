package com.example.guillaume.library.Metier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillaume on 26/09/15.
 */
public class Livre extends AbstractLibraryElement implements Parcelable {

    /**
     * Titre du livre
     */
    private String titre;

    /**
     * Auteur du livre
     */
    private String auteur;

    /**

     * Description, résumé
     */
    private String description;

    /**
     * Couverture du livre
     */
    private byte[] couverture;



    /**
     * Constructeur
     * @param titre
     * @param auteur
     */
    public Livre(String titre, String auteur) {
        this.titre = titre;

        this.auteur = auteur;
    }

    /**
     * Constructeur vide
     */
    public Livre() {

    }

    /**
     *
     * @return auteur
     */
    public String getAuteur() {
        return auteur;
    }

    /**
     *
     * @param auteur auteur
     */
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    /**
     *
     * @return titre
     */
    public String getTitre() {
        return titre;
    }

    /**
     *
     * @param titre titre
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    public byte[] getCouverture() {
        return couverture;
    }

    public void setCouverture(byte[] couverture) {
        this.couverture = couverture;
    }

    /**
     * Permet de regénérer l'objet
     */
    public static final Parcelable.Creator<Livre> CREATOR = new Creator<Livre>() {
        @Override
        public Livre createFromParcel(Parcel parcel) {
            Livre livre = new Livre();
            livre.setTitre(parcel.readString());
            livre.setAuteur(parcel.readString());
            livre.setDescription(parcel.readString());
            livre.setCouverture(new byte[parcel.readInt()]);
            parcel.readByteArray(livre.getCouverture());
            return livre;
        }

        @Override
        public Livre[] newArray(int i) {
            return new Livre[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titre);
        parcel.writeString(auteur);
        parcel.writeString(description);
        if(couverture != null) {
            parcel.writeInt(couverture.length);
            parcel.writeByteArray(couverture);
        }

    }

}
