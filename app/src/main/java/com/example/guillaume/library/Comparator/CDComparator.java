package com.example.guillaume.library.Comparator;

import com.example.guillaume.library.Metier.CD;

import java.util.Comparator;

/**
 * Created by guillaume on 13/10/16.
 */
public class CDComparator implements Comparator<CD> {

    /**
     * Tri en fonction des noms des artistes puis des dates de sortie du CD
     *
     * @param cd1
     * @param cd2
     * @return
     */
    @Override
    public int compare(CD cd1, CD cd2) {

        int compareArtiste = cd1.getArtiste().compareTo(cd2.getArtiste());

        if(compareArtiste != 0) {
            return compareArtiste;
        } else {
            return cd1.getDateSortie().toString().compareTo(cd2.getDateSortie().toString());
        }
    }
}
