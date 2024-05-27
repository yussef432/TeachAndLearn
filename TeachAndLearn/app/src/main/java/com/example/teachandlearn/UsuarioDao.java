package com.example.teachandlearn;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsuarioDao {
    @Insert
    void insert(Usuario usuario);

    @Query("SELECT * FROM usuario WHERE email = :email")
    Usuario findByEmail(String email);

    @Update
    void update(Usuario usuario);
}
