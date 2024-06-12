package com.example.teachandlearn.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachandlearn.R;

import java.util.List;

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
        holder.fechaHora.setText(evento.getFechaHoraFormateada());
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
