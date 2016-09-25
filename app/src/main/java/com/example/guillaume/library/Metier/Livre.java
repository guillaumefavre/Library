package com.example.guillaume.library.Metier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillaume on 26/09/15.
 */
public class Livre extends AbstractLibraryElement implements Parcelable {

    /**
     * Identifiant fonctionnel (auteur + titre formattés)
     */
    private String identifiantFonctionnel;

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
    private String couverture;



    /**
     * Constructeur
     * @param titre titre du livre
     * @param auteur auteur du livre
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
     * @return identifiantFonctionnel
     */
    public String getIdentifiantFonctionnel() {
        return identifiantFonctionnel;
    }

    /**
     *
     * @param identifiantFonctionnel identifiant du livre
     */
    public void setIdentifiantFonctionnel(String identifiantFonctionnel) {
        this.identifiantFonctionnel = identifiantFonctionnel;
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
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description description du livre
     */
    public void setDescription(String description) {
        this.description = description;
    }


    public String getCouverture() {
        return couverture;
    }

    public void setCouverture(String couverture) {
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
            livre.setCouverture(parcel.readString());
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
        parcel.writeString(couverture);
    }

}
