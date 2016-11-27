package com.example.guillaume.library.Adapteurs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guillaume.library.Metier.CD;
import com.example.guillaume.library.Metier.Livre;
import com.example.guillaume.library.R;
import com.example.guillaume.library.Utils.BitmapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    /**
     * Map représentant les éléments sélectionnés
     */
    private Map<Integer, Livre> itemsSelectionnes = new HashMap<Integer, Livre>();


    public AdapteurListeLivres(final Context context, final List<Livre> listeLivres) {
        super(context, R.layout.layout_liste_livre_item, listeLivres);
        inflater = LayoutInflater.from(context);
    }

    /**
     * Ajout d'un élément dans la map des items sélectionnés
     * @param position
     * @param value
     */
    public void addItemSelectionne(int position, Livre value) {
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
     * Récupération des livres sélectionnés
     * @return
     */
    public Collection<Livre> getLivresSelectionnes() {
        return itemsSelectionnes.values();
    }


    public void clearSelection() {
        itemsSelectionnes = new HashMap<Integer, Livre>();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // On récupère l'objet métier correspondant à la ligne
        livre = (Livre) getItem(position);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_liste_livre_item, null);

            holder = new ViewHolder();
            holder.titre = (TextView) convertView.findViewById(R.id.titreLivre);
            holder.auteur = (TextView) convertView.findViewById(R.id.auteurLivre);
            holder.couverture = (ImageView) convertView.findViewById(R.id.imvCouverturePochette);

            convertView.setTag(holder);
        }


        // Couleur par défaut
        convertView.setBackgroundColor(Color.parseColor("#EEEEEE"));

        // On récupère le holder à partir du tag
        holder = (ViewHolder) convertView.getTag();

        // Mise à jour des données de la ligne
        holder.titre.setText((CharSequence) livre.getTitre());
        holder.auteur.setText((CharSequence) livre.getAuteur());
        if(livre.getCouverture() != null) {
            holder.couverture.setImageBitmap(BitmapUtils.convertEncodedBase64StringToBitmap(livre.getCouverture()));
        } else {
            // TODO gérer couverture absente
            holder.couverture.setImageBitmap(null);
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

        private TextView auteur;

        private TextView titre;

        private ImageView couverture;

    }
}
