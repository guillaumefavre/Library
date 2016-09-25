package com.example.guillaume.library.Utils;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by guillaume on 25/09/16.
 */
public class FormatUtils {

    public static String genererIdentifiantLivre(final String auteurLivre, final String titreLivre) {

        String identifiant, auteurFormat, titreFormat  = "";

        // Mise en majuscule des premières lettres de chaque mot
        auteurFormat = WordUtils.capitalizeFully(auteurLivre);
        titreFormat = WordUtils.capitalizeFully(titreLivre);

        // Suppression des espaces et des apostrophes
        auteurFormat = auteurFormat.replaceAll(" ", "");
        auteurFormat = auteurFormat.replaceAll("\'", "");
        titreFormat = titreFormat.replaceAll(" ", "");
        titreFormat = titreFormat.replaceAll("\'", "");

        identifiant = auteurFormat + titreFormat;

        return identifiant;

    }
}
