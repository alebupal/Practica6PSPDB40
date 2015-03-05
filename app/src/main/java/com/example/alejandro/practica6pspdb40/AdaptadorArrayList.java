package com.example.alejandro.practica6pspdb40;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alejandro on 10/10/2014.
 */
public class AdaptadorArrayList extends ArrayAdapter<Pelicula> {
    private Context contexto;
    private ArrayList<Pelicula> lista;
    private int recurso;
    private static LayoutInflater i;

    public static class ViewHolder {
        public TextView tvTitulo, tvAnio, tvGenero;
        public int posicion;
    }
    public AdaptadorArrayList(Context context, int resource, ArrayList<Pelicula> objects) {
        super(context, resource, objects);
        this.contexto = context;
        this.lista = objects;
        this.recurso = resource;
        this.i = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = i.inflate(recurso, null);
        }
        vh = new ViewHolder();
        vh.tvTitulo = (TextView) convertView.findViewById(R.id.tvTitulo);
        vh.tvGenero = (TextView) convertView.findViewById(R.id.tvGenero);
        vh.tvAnio = (TextView) convertView.findViewById(R.id.tvAnio);
        convertView.setTag(vh);

        vh = (ViewHolder) convertView.getTag();


        vh.posicion = position;
        vh.tvTitulo.setText(lista.get(position).getTitulo());
        vh.tvAnio.setText(lista.get(position).getAnio().toString());
        vh.tvGenero.setText(lista.get(position).getGenero());


        return convertView;
    }

}
