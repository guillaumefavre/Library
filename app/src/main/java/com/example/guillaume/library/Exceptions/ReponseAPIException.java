package com.example.guillaume.library.Exceptions;

/**
 * Created by guillaume on 21/05/16.
 */
public class ReponseAPIException extends Throwable {

    String message = "Une erreur est survenue lors de la récupération des données";

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
