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

    private List<Reserva> reservas; // Agregamos una lista de reservas

    public ClasesAdapter(Context context, List<Anuncio> clases, List<Reserva> reservas) {
        super(context, 0, clases);
        this.reservas = reservas;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        fecha.setText(dateFormat.format(clase.getFechaTutoria())); // Formatear la fecha aquí

        // Buscamos la reserva correspondiente al anuncio actual
        String estadoReserva = getEstadoReserva(clase.getId());
        estado.setText(estadoReserva != null ? estadoReserva : "Sin reserva");

        view.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Ver_Anuncio.class);
            intent.putExtra("ANUNCIO_ID", clase.getId());
            getContext().startActivity(intent);
        });

        return view;
    }

    @Nullable
    private String getEstadoReserva(int anuncioId) {
        for (Reserva reserva : reservas) {
            if (reserva.getIdAnuncio() == anuncioId) {
                return reserva.getEstado();
            }
        }
        return null; // Si no se encuentra una reserva correspondiente
    }
}
