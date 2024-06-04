package com.example.teachandlearn;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "reserva",
        foreignKeys = {
                @ForeignKey(entity = Anuncio.class,
                        parentColumns = "id",
                        childColumns = "id_anuncio",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Usuario.class,
                        parentColumns = "email", // Cambiado a "email"
                        childColumns = "id_usuario", // Cambiado a "id_usuario"
                        onDelete = ForeignKey.CASCADE)
        })
public class Reserva {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "id_anuncio")
    public int idAnuncio;

    @ColumnInfo(name = "id_usuario")
    public String idUsuario; // Cambiado el tipo a String

    @ColumnInfo(name = "rol_usuario")
    public String rolUsuario;

    @ColumnInfo(name = "fecha_reserva")
    public String fechaReserva; // Puedes usar el tipo Date con TypeConverters

    @ColumnInfo(name = "estado") // Agregar este campo
    public String estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(int idAnuncio) {
        this.idAnuncio = idAnuncio;
    }


    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }


    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }


    public void setFechaReserva(String fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
