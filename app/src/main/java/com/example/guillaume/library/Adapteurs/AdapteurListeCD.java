package com.example.guillaume.library.Adapteurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.R;

import java.util.List;

/**
 * Created by guillaume on 13/08/15.
 */
public class AdapteurListeCD extends ArrayAdapter<CD> {

    private LayoutInflater inflater;

    /**
     * CD
     */
    private CD cd;

    /**
     * View holder
     */
    private ViewHolder holder;


    public AdapteurListeCD(final Context context, final List<CD> listeCDs) {
        super(context, R.layout.layout_liste_cd_item, listeCDs);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // On récupère l'objet métier correspondant à la ligne
        cd = (CD) getItem(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_liste_cd_item, null);

            holder = new ViewHolder();
            holder.nomArtiste = (TextView) convertView.findViewById(R.id.nomArtiste);
            holder.titreAlbum = (TextView) convertView.findViewById(R.id.titreAlbum);
            holder.pochette = (ImageView) convertView.findViewById(R.id.imvPochetteAlbum);

            convertView.setTag(holder);
        }

        // On récupère le holder à partir du tag
        holder = (ViewHolder) convertView.getTag();

        // Mise à jour des données de la ligne
        holder.nomArtiste.setText((CharSequence) cd.getArtiste());
        holder.titreAlbum.setText((CharSequence) cd.getTitreAlbum());
        holder.pochette.setImageResource(cd.getPochette());


        return convertView;
    }


    /**
     * View Holder pour améliorer les performances
     */
    static class ViewHolder {

        private TextView nomArtiste;

        private TextView titreAlbum;

        private ImageView pochette;
    }
}
