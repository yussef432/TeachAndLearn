package com.example.teachandlearn.Adaptadores;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Evento {
    private String titulo;
    private Date fechaHora;
    private String fechaHoraFormateada;

    public Evento(String titulo, Date fechaHora) {
        this.titulo = titulo;
        this.fechaHora = fechaHora;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        this.fechaHoraFormateada = dateFormat.format(fechaHora);
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public String getFechaHoraFormateada() {
        return fechaHoraFormateada;
    }
}
