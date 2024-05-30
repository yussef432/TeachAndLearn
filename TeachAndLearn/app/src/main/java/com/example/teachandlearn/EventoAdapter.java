package com.example.teachandlearn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {
    private List<Evento> eventos;

    public EventoAdapter(List<Evento> eventos) {
        this.eventos = eventos;
    }

    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);
        holder.titulo.setText(evento.getTitulo());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        holder.fechaHora.setText(sdf.format(evento.getFechaHora()));
    }


    @Override
    public int getItemCount() {
        return eventos.size();
    }

    static class EventoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView fechaHora;

        EventoViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.event_title);
            fechaHora = itemView.findViewById(R.id.event_time);
        }
    }
    // EventoAdapter.java
    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
        notifyDataSetChanged();
    }

}
