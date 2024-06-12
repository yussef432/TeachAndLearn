package com.example.teachandlearn.BBDD;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

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


    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
