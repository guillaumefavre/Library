package com.example.guillaume.library.Exceptions;

import android.database.sqlite.SQLiteConstraintException;

import com.example.guillaume.library.Metier.AbstractLibraryElement;
import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;

/**
 * Created by guillaume on 25/09/16.
 */
public class UniqueConstraintException extends SQLiteConstraintException {

    private String message = "Cet élément est déjà présent dans la librairie.";

    private AbstractLibraryElement element;

    /**
     * Constructeur
     *
     * @param element élément ayant enclenché l'exception
     */
    public UniqueConstraintException(AbstractLibraryElement element) {
        this.element = element;
        if(element instanceof CD) {
            this.message = ((CD) element).getTitreAlbum() + " est déjà présent dans la liste de CDs";
        } else if(element instanceof  Livre) {
            this.message = ((Livre) element).getTitre() + " est déjà présent dans la liste de Livres";
        }
    }

    public AbstractLibraryElement getElement() {
        return element;
    }

    public void setElement(AbstractLibraryElement element) {
        this.element = element;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
