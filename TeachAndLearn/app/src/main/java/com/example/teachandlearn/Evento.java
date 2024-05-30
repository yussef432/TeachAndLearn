package com.example.teachandlearn;

import java.util.Date;

public class Evento {
    private String titulo;
    private Date fechaHora;

    public Evento(String titulo, Date fechaHora) {
        this.titulo = titulo;
        this.fechaHora = fechaHora;
    }

    public String getTitulo() {
        return titulo;
    }

    public Date getFechaHora() {
        return fechaHora;
    }
}

