package com.example.guillaume.library.Adapteurs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.R;
import com.example.guillaume.library.Utils.BitmapUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * Map représentant les éléments sélectionnés
     */
    private Map<Integer, CD> itemsSelectionnes = new HashMap<Integer, CD>();

    /**
     * Liste des CDs sélectionnés
     */
    private List<CD> listeCDsSelectionnes = new ArrayList<>();


    /**
     * Constructeur
     * @param context
     * @param listeCDs
     */
    public AdapteurListeCD(final Context context, final List<CD> listeCDs) {
        super(context, R.layout.layout_liste_cd_item, listeCDs);
        inflater = LayoutInflater.from(context);
    }

    /**
     * Ajout d'un élément dans la map des items sélectionnés
     * @param position
     * @param value
     */
    public void addItemSelectionne(int position, CD value) {
        itemsSelectionnes.put(position, value);
        notifyDataSetChanged();
    }


    /**
     * Suppression d'un item sélectionné
     * @param position
     */
    public void removeItemSelectionne(int position) {
        itemsSelectionnes.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Récupération des CDs sélectionnés
     * @return
     */
    public Collection<CD> getCDsSelectionnes() {
        return itemsSelectionnes.values();
    }


    public void clearSelection() {
        itemsSelectionnes = new HashMap<Integer, CD>();
        notifyDataSetChanged();
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

        // Couleur par défaut
        convertView.setBackgroundColor(Color.parseColor("#EEEEEE"));


        // On récupère le holder à partir du tag
        holder = (ViewHolder) convertView.getTag();

        // Mise à jour des données de la ligne
        holder.nomArtiste.setText((CharSequence) cd.getArtiste());
        holder.titreAlbum.setText((CharSequence) cd.getTitreAlbum());

        if(cd.getPochette() != null) {
            // Récupération du bitmap d'origine à partir de la pochette
            Bitmap bitmapOriginal = BitmapUtils.convertEncodedBase64StringToBitmap(cd.getPochette());

            // Redéfinition de la taille du bitmap
            Bitmap bitmapReduit = Bitmap.createScaledBitmap(bitmapOriginal, (int)(bitmapOriginal.getWidth()*0.6), (int)(bitmapOriginal.getHeight()*0.6), true);
            holder.pochette.setImageBitmap(bitmapReduit);
        } else {
            // TODO gérer pochette absente
            holder.pochette.setImageBitmap(null);
        }

        // Sélection d'un item, changement de la couleur de fond
        if (itemsSelectionnes.get(position) != null) {
            convertView.setBackgroundColor(Color.GRAY);
        }

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
