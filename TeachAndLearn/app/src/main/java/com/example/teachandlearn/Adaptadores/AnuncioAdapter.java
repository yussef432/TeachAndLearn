package com.example.teachandlearn.Adaptadores;

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

import com.example.teachandlearn.BBDD.Anuncio;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.Anuncios.Crear_Anuncio;
import com.example.teachandlearn.Autentificacion.MainActivity;
import com.example.teachandlearn.R;
import com.example.teachandlearn.Anuncios.Ver_Anuncio;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnuncioAdapter extends ArrayAdapter<Anuncio> {

    public AnuncioAdapter(Context context, List<Anuncio> anuncios) {
        super(context, 0, anuncios);
    }

    private static class ViewHolder {
        TextView titulo;
        TextView horas;
        TextView precio;
        TextView estado;
        TextView fecha;
        TextView tipo_anuncio;
        Button editar;
        Button eliminar;
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
            viewHolder.estado = convertView.findViewById(R.id.estado_anuncio);
            viewHolder.fecha = convertView.findViewById(R.id.fecha_anuncio);
            viewHolder.tipo_anuncio = convertView.findViewById(R.id.tipo_anuncio);
            viewHolder.editar = convertView.findViewById(R.id.editar_anuncio);
            viewHolder.eliminar = convertView.findViewById(R.id.eliminar_anuncio);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titulo.setText(anuncio.titulo);
        viewHolder.horas.setText(String.format("%d h", anuncio.horas));
        viewHolder.precio.setText(String.format("%.2f €", anuncio.precioPorHora));
        viewHolder.tipo_anuncio.setText(anuncio.tipoAnuncio);

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Verificar si la fecha de la tutoria ha pasado
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        viewHolder.fecha.setText(String.format("Fecha: %s", dateTimeFormat.format(anuncio.fechaTutoria)));
        Calendar today = Calendar.getInstance();
        try {
            Calendar fechaTutoria = Calendar.getInstance();
            fechaTutoria.setTime(dateTimeFormat.parse(dateTimeFormat.format(anuncio.fechaTutoria)));

            if (fechaTutoria.before(today)) {
                if (!"Aceptado".equals(anuncio.estado)) {
                    // Eliminar el anuncio si no tiene estado "Aceptado"
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(getContext());
                        db.anuncioDao().delete(anuncio);
                        db.reservaDao().deleteReservasByAnuncioIdIfNotReserved(anuncio.id);
                        ((MainActivity) getContext()).runOnUiThread(() -> {
                            remove(anuncio);
                            notifyDataSetChanged();
                        });
                    }).start();
                } else {
                    viewHolder.estado.setText("Anuncio Caducado");
                    convertView.setBackgroundColor(Color.parseColor("#FFCCCB")); // Rojo claro
                }
            } else {
                convertView.setBackgroundColor(Color.parseColor("#CCFFCC")); // Verde claro
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Mostrar/ocultar botones según el estado del anuncio
        if ("".equals(anuncio.estado) && userEmail.equals(anuncio.idUsuario)) {
            viewHolder.editar.setVisibility(View.VISIBLE);
            viewHolder.eliminar.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editar.setVisibility(View.GONE);
            viewHolder.eliminar.setVisibility(View.GONE);
        }

        viewHolder.editar.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Crear_Anuncio.class);
            intent.putExtra("ANUNCIO_ID", anuncio.id);
            getContext().startActivity(intent);
        });

        viewHolder.eliminar.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getContext());
                db.anuncioDao().delete(anuncio);
                ((MainActivity) getContext()).runOnUiThread(() -> {
                    remove(anuncio);
                    notifyDataSetChanged();
                    Toast.makeText(this.getContext(), "Anuncio eliminado", Toast.LENGTH_LONG).show();
                });
            }).start();
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Ver_Anuncio.class);
            intent.putExtra("ANUNCIO_ID", anuncio.id);
            getContext().startActivity(intent);
        });

        return convertView;
    }
}
