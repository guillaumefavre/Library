package com.example.guillaume.library.Comparator;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;

import java.util.Comparator;

/**
 * Created by guillaume on 27/11/16.
 */

public class LivreComparator implements Comparator<Livre> {

    /**
     * Tri en fonction des noms des auteurs puis des titres des livres
     *
     * @param livre1
     * @param livre2
     * @return
     */
    @Override
    public int compare(Livre livre1, Livre livre2) {

        // Premier tri à partir du nom de l'auteur
        int compareAuteur = livre1.getAuteur().compareTo(livre2.getAuteur());

        if(compareAuteur != 0) {
            return compareAuteur;
        } else {
            // Si l'auteur est identique, tri en fonction du titre du livre
            return livre1.getTitre().toString().compareTo(livre2.getTitre().toString());
        }
    }
}
