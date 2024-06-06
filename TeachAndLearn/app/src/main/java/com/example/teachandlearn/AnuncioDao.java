package com.example.teachandlearn;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AnuncioDao {

    @Insert
    void insert(Anuncio anuncio);

    @Query("SELECT * FROM anuncio WHERE id = :anuncioId")
    Anuncio findById(int anuncioId);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail " +
            "AND estado != 'Aceptado' " +
            "AND fecha_tutoria >= :today " +
            "AND id NOT IN (SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail) ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC, " +
            "CASE WHEN :filter = 'FechaAsc' THEN fecha_tutoria END ASC, " +
            "CASE WHEN :filter = 'FechaDesc' THEN fecha_tutoria END DESC")
    List<Anuncio> findAnunciosByTypeAndNotUser(String tipo, String userEmail, String filter, long today);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail " +
            "AND estado != 'Aceptado'" +
            "AND fecha_tutoria >= :today AND id NOT IN (SELECT id_anuncio FROM reserva " +
            "WHERE id_usuario = :userEmail) AND " +
            "(titulo LIKE :query OR descripcion LIKE :query) ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC, " +
            "CASE WHEN :filter = 'FechaAsc' THEN fecha_tutoria END ASC, " +
            "CASE WHEN :filter = 'FechaDesc' THEN fecha_tutoria END DESC")
    List<Anuncio> searchAnuncios(String tipo, String userEmail, String query, String filter, long today);
    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Aceptado'")
    List<Anuncio> findAcceptedByUserEmail(String userEmail);

    @Update
    void update(Anuncio anuncio);

    @Delete
    void delete(Anuncio anuncio);

    @Query("SELECT * FROM anuncio WHERE (estado = 'Reservado' OR estado = 'Aceptado') AND id IN " +
            "(SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail AND estado = 'Reservado') " +
            "AND fecha_tutoria >= :today AND tipo_anuncio = :tipo ORDER BY CASE " +
            "WHEN fecha_tutoria >= :today THEN 0 ELSE 1 END, fecha_tutoria asc")
    List<Anuncio> findAnunciosReservadosByUserEmailAndType(String userEmail, String tipo, long today);


    @Query("SELECT EXISTS(SELECT 1 FROM reserva WHERE id_anuncio = :anuncioId AND id_usuario = :userEmail)")
    boolean isAnuncioReservedByUser(int anuncioId, String userEmail);
    @Query("SELECT EXISTS(SELECT 1 FROM anuncio WHERE id = :anuncioId AND estado = 'Aceptado')")
    boolean anuncioAceptado(int anuncioId);
    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = :estado")
    List<Anuncio> findByUserEmailAndEstado(String userEmail, String estado);

    @Query("SELECT * FROM anuncio WHERE id = :anuncioId")
    Anuncio findAnuncioById(int anuncioId);

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Pendiente'")
    List<Anuncio> findPendientesByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Aceptado' " +
            "ORDER BY CASE WHEN fecha_tutoria >= :today THEN 0 ELSE 1 END, fecha_tutoria desc")
    List<Anuncio> findAceptadosByUserEmail(String userEmail, long today);

    @Query("SELECT * FROM anuncio WHERE estado = 'Pendiente' AND id IN " +
            "(SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail AND estado = 'Pendiente')")
    List<Anuncio> findAnunciosPendientesReservadosByUserEmail(String userEmail);
    @Query("SELECT * FROM anuncio WHERE estado = 'Aceptado' AND id IN " +
            "(SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail AND estado = 'Rechazado')" +
            "  AND fecha_tutoria >= :today ")
    List<Anuncio> findAnunciosRechazadosReservadosByUserEmail(String userEmail, long today);

    @Query("SELECT * FROM anuncio WHERE estado = 'Reservado' OR estado = 'Aceptado' AND id IN " +
            "(SELECT id_anuncio FROM reserva " +
            "WHERE id_usuario = :userEmail AND estado = 'Reservado')ORDER BY CASE " +
            "WHEN fecha_tutoria >= :today THEN 0 ELSE 1 END, fecha_tutoria asc")
    List<Anuncio> findAnunciosReservadosByUserEmail(String userEmail, long today);

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Aceptado' " +
            "AND tipo_anuncio = :tipo " +
            "ORDER BY CASE WHEN fecha_tutoria >= :today THEN 0 ELSE 1 END, fecha_tutoria DESC")
    List<Anuncio> findAceptadosByUserEmailAndType(String userEmail, String tipo, long today);


}
