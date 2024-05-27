package com.example.teachandlearn;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Date;

@Entity(tableName = "anuncio",
        foreignKeys = @ForeignKey(entity = Usuario.class,
                parentColumns = "email",
                childColumns = "id_usuario",
                onDelete = ForeignKey.CASCADE))
public class Anuncio {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int horas;

    @ColumnInfo(name = "tipo_anuncio")
    public String tipoAnuncio;

    @ColumnInfo(name = "id_usuario")
    public String idUsuario;

    public String descripcion;
    public String titulo;

    @ColumnInfo(name = "precio_por_hora")
    public float precioPorHora;

    @ColumnInfo(name = "fecha_tutoria")
    @TypeConverters({Converters.class})
    public Date fechaTutoria;

    @ColumnInfo(name = "fecha_creacion")
    @TypeConverters({Converters.class})
    public Date fechaCreacion;

    @ColumnInfo(name = "estado")
    public String estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public String getTipoAnuncio() {
        return tipoAnuncio;
    }

    public void setTipoAnuncio(String tipoAnuncio) {
        this.tipoAnuncio = tipoAnuncio;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public float getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(float precioPorHora) {
        this.precioPorHora = precioPorHora;
    }

    public Date getFechaTutoria() {
        return fechaTutoria;
    }

    public void setFechaTutoria(Date fechaTutoria) {
        this.fechaTutoria = fechaTutoria;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUserEmail() {
        return idUsuario;
    }
}
