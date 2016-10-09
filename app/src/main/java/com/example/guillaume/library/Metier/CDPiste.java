package com.example.guillaume.library.Metier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by guillaume on 24/12/15.
 */
public class CDPiste implements Parcelable {

    /**
     * Numéro de la piste sur le CD
     */
    private int numeroPiste;

    /**
     * Nom de la chanson
     */
    private String titre;

    /**
     * Durée de la piste (format mm:ss)
     */
    private String duree;

    /**
     *
     * @return numeroPiste
     */
    public int getNumeroPiste() {
        return numeroPiste;
    }

    /**
     *
     * @param numeroPiste numéro de la piste
     */
    public void setNumeroPiste(int numeroPiste) {
        this.numeroPiste = numeroPiste;
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
     * @param titre titre de la chanson
     */
    public void setTitre(String titre) {
        this.titre = titre;
    }

    /**
     *
     * @return
     */
    public String getDuree() {
        return duree;
    }

    /**
     *
     * @param duree
     */
    public void setDuree(String duree) {
        this.duree = duree;
    }

    /**
     * Permet de regénérer l'objet
     */
    public static final Parcelable.Creator<CDPiste> CREATOR = new Creator<CDPiste>() {
        @Override
        public CDPiste createFromParcel(Parcel parcel) {
            CDPiste cdPiste = new CDPiste();
            cdPiste.setNumeroPiste(parcel.readInt());
            cdPiste.setTitre(parcel.readString());
            cdPiste.setDuree(parcel.readString());
            return cdPiste;
        }

        @Override
        public CDPiste[] newArray(int i) {
            return new CDPiste[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(numeroPiste);
        parcel.writeString(titre);
        parcel.writeString(duree);
    }
}
