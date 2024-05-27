package com.example.teachandlearn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AnuncioAdapter extends ArrayAdapter<Anuncio> {

    public AnuncioAdapter(Context context, List<Anuncio> anuncios) {
        super(context, 0, anuncios);
    }

    private static class ViewHolder {
        TextView titulo;
        TextView horas;
        TextView precio;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Anuncio anuncio = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_anuncio, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.titulo = convertView.findViewById(R.id.titulo_anuncio);
            viewHolder.horas = convertView.findViewById(R.id.horas_anuncio);
            viewHolder.precio = convertView.findViewById(R.id.precio_anuncio);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titulo.setText(anuncio.titulo);
        viewHolder.horas.setText(String.format("%d h", anuncio.horas));
        viewHolder.precio.setText(String.format("%.2f €", anuncio.precioPorHora));

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Ver_Anuncio.class);
            intent.putExtra("ANUNCIO_ID", anuncio.id);
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
