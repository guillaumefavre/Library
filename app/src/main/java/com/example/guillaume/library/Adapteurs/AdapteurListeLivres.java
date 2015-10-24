package com.example.guillaume.library.Adapteurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.R;

import java.util.List;


/**
 * Created by guillaume on 26/09/15.
 */
public class AdapteurListeLivres extends ArrayAdapter<Livre> {


    private LayoutInflater inflater;

    /**
     * Livre
     */
    private Livre livre;

    /**
     * View holder
     */
    private ViewHolder holder;


    public AdapteurListeLivres(final Context context, final List<Livre> listeLivres) {
        super(context, R.layout.layout_list_livre_item, listeLivres);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // On récupère l'objet métier correspondant à la ligne
        livre = (Livre) getItem(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_list_livre_item, null);

            holder = new ViewHolder();
            holder.titre = (TextView) convertView.findViewById(R.id.titreLivre);
            holder.auteur = (TextView) convertView.findViewById(R.id.auteurLivre);

            convertView.setTag(holder);
        }

        // On récupère le holder à partir du tag
        holder = (ViewHolder) convertView.getTag();

        // Mise à jour des données de la ligne
        holder.titre.setText((CharSequence) livre.getTitre());
        holder.auteur.setText((CharSequence) livre.getAuteur());


        return convertView;
    }


    /**
     * View Holder pour améliorer les performances
     */
    static class ViewHolder {

        private TextView auteur;

        private TextView titre;

    }
}
