package com.example.guillaume.library.Adapteurs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.CDPiste;
import com.example.guillaume.library.R;
import com.example.guillaume.library.Utils.BitmapUtils;

import java.util.List;

/**
 * Created by guillaume on 09/10/16.
 */

public class AdapteurListePistesCD extends ArrayAdapter<CDPiste> {

    private LayoutInflater inflater;

    /**
     * piste
     */
    private CDPiste pisteCD;

    /**
     * View holder
     */
    private ViewHolder holder;

    public AdapteurListePistesCD(final Context context, final List<CDPiste> listePistes) {
        super(context, R.layout.layout_cd_piste_item, listePistes);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // On récupère l'objet métier correspondant à la ligne
        pisteCD = (CDPiste) getItem(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_cd_piste_item, null);

            holder = new ViewHolder();
            holder.numeroPiste = (TextView) convertView.findViewById(R.id.numeroPiste);
            holder.titrePiste = (TextView) convertView.findViewById(R.id.titrePiste);
            holder.dureePiste = (TextView) convertView.findViewById(R.id.dureePiste);

            convertView.setTag(holder);
        }

        // On récupère le holder à partir du tag
        holder = (ViewHolder) convertView.getTag();

        // Mise à jour des données de la ligne
        holder.numeroPiste.setText((CharSequence) String.valueOf(pisteCD.getNumeroPiste()));
        holder.titrePiste.setText((CharSequence) pisteCD.getTitre());
        holder.dureePiste.setText((CharSequence) pisteCD.getDuree());


        return convertView;
    }




    /**
     * View Holder pour améliorer les performances
     */
    static class ViewHolder {

        private TextView numeroPiste;

        private TextView titrePiste;

        private TextView dureePiste;
    }
}
