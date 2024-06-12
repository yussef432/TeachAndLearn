package com.example.teachandlearn.BBDD;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuario")
public class Usuario {
    @PrimaryKey
    @NonNull
    public String email;

    public String apellidos;
    public String nombre;
    public String telefono;
    public String contraseña;

    @ColumnInfo(name = "foto_perfil")
    public String fotoPerfil;

    public String descripcion;
}
