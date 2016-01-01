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
    private String titreChanson;


    public int getNumeroPiste() {
        return numeroPiste;
    }

    public void setNumeroPiste(int numeroPiste) {
        this.numeroPiste = numeroPiste;
    }

    public String getTitreChanson() {
        return titreChanson;
    }

    public void setTitreChanson(String titreChanson) {
        this.titreChanson = titreChanson;
    }

    /**
     * Permet de regénérer l'objet
     */
    public static final Parcelable.Creator<CDPiste> CREATOR = new Creator<CDPiste>() {
        @Override
        public CDPiste createFromParcel(Parcel parcel) {
            CDPiste cdPiste = new CDPiste();
            cdPiste.setNumeroPiste(parcel.readInt());
            cdPiste.setTitreChanson(parcel.readString());
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
        parcel.writeString(titreChanson);
    }
}
