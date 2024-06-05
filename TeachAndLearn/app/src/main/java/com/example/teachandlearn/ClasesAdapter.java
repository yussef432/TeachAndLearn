package com.example.teachandlearn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClasesAdapter extends ArrayAdapter<Anuncio> {

    private List<Reserva> reservas;

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
        TextView caducada = view.findViewById(R.id.caducada);
        Button btnCancelarPeticion = view.findViewById(R.id.btn_cancelar_peticion);

        titulo.setText(clase.getTitulo());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        fecha.setText(dateFormat.format(clase.getFechaTutoria()));

        String estadoReserva = getEstadoReserva(clase.getId());
        estado.setText(estadoReserva != null ? estadoReserva : "Sin reserva");

        // Verificar si la fecha de la tutoría ha pasado
        Date fechaTutoria = clase.getFechaTutoria();
        Date currentDate = new Date();
        if (fechaTutoria.before(currentDate)) {
            // La fecha de la tutoría ha pasado
            caducada.setVisibility(View.VISIBLE);
            view.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo claro
        } else {
            // La fecha de la tutoría no ha pasado
            caducada.setVisibility(View.GONE);
            view.setBackgroundColor(Color.parseColor("#CCFFCC")); // Verde claro
        }

        if ("Pendiente".equals(estadoReserva)) {
            btnCancelarPeticion.setVisibility(View.VISIBLE);
            btnCancelarPeticion.setOnClickListener(v -> {
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(getContext());
                    Reserva reserva = getReservaByAnuncioId(clase.getId());
                    if (reserva != null) {
                        db.reservaDao().delete(reserva);
                        ((MainActivity) getContext()).runOnUiThread(() -> {
                            reservas.remove(reserva);
                            remove(clase);
                            notifyDataSetChanged();
                            Toast.makeText(getContext(), "Petición de reserva eliminada correctamente", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            });
        } else {
            btnCancelarPeticion.setVisibility(View.GONE);
        }

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
        return null;
    }

    @Nullable
    private Reserva getReservaByAnuncioId(int anuncioId) {
        for (Reserva reserva : reservas) {
            if (reserva.getIdAnuncio() == anuncioId) {
                return reserva;
            }
        }
        return null;
    }
}
