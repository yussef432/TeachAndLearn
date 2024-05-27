package com.example.teachandlearn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ClasesAdapter extends ArrayAdapter<Anuncio> {

    public ClasesAdapter(Context context, List<Anuncio> clases) {
        super(context, 0, clases);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Anuncio clase = getItem(position);
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_clase, parent, false);
        }

        TextView titulo = view.findViewById(R.id.titulo_clase);
        TextView fecha = view.findViewById(R.id.fecha_clase);
        TextView estado = view.findViewById(R.id.estado_clase);

        titulo.setText(clase.getTitulo());
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        fecha.setText(dateFormat.format(clase.getFechaTutoria())); // Formatear la fecha aquí
        estado.setText(clase.getEstado());

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Ver_Anuncio.class);
            intent.putExtra("ANUNCIO_ID", clase.getId());
            getContext().startActivity(intent);
        });

        return view;
    }
}
