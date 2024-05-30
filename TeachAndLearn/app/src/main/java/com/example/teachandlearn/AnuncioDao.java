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

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userId")
    List<Anuncio> findByUserId(int userId);

    @Query("SELECT * FROM anuncio WHERE id = :anuncioId")
    Anuncio findById(int anuncioId);

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado!= 'Reservado'")
    List<Anuncio> findByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail")
    List<Anuncio> findAnunciosByTypeAndNotUser(String tipo, String userEmail);


    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail AND id NOT IN (SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail) ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC")
    List<Anuncio> findAnunciosByTypeAndNotUser(String tipo, String userEmail, String filter);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail AND " +
            "(titulo LIKE :query OR descripcion LIKE :query) ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC")
    List<Anuncio> searchAnuncios(String tipo, String userEmail, String query, String filter);
    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Aceptado'")
    List<Anuncio> findAcceptedByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC")
    List<Anuncio> getAllFiltered(String tipo, String userEmail, String filter);

    @Query("SELECT * FROM anuncio WHERE tipo_anuncio = :tipo AND id_usuario != :userEmail AND " +
            "(titulo LIKE :query OR descripcion LIKE :query) ORDER BY " +
            "CASE WHEN :filter = 'HorasAsc' THEN horas END ASC, " +
            "CASE WHEN :filter = 'HorasDesc' THEN horas END DESC, " +
            "CASE WHEN :filter = 'PrecioAsc' THEN precio_por_hora END ASC, " +
            "CASE WHEN :filter = 'PrecioDesc' THEN precio_por_hora END DESC")
    List<Anuncio> searchFiltered(String tipo, String userEmail, String query, String filter);

    @Update
    void update(Anuncio anuncio);

    @Delete
    void delete(Anuncio anuncio);

    // Nuevo método para encontrar anuncios reservados por el usuario
    @Query("SELECT * FROM anuncio WHERE id IN (SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail)")
    List<Anuncio> findAnunciosReservadosByUsuario(String userEmail);
    @Query("SELECT EXISTS(SELECT 1 FROM reserva WHERE id_anuncio = :anuncioId AND id_usuario = :userEmail)")
    boolean isAnuncioReservedByUser(int anuncioId, String userEmail);
    @Query("SELECT EXISTS(SELECT 1 FROM anuncio WHERE id = :anuncioId AND estado = 'Aceptado')")
    boolean anuncioAceptado(int anuncioId);
    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = :estado")
    List<Anuncio> findByUserEmailAndEstado(String userEmail, String estado);

    @Query("SELECT * FROM anuncio WHERE id = :anuncioId")
    Anuncio findAnuncioById(int anuncioId);

    @Update
    void updateAnuncio(Anuncio anuncio);
    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Pendiente'")
    List<Anuncio> findPendientesByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE id_usuario = :userEmail AND estado = 'Aceptado'")
    List<Anuncio> findAceptadosByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE estado = 'Pendiente' AND id IN " +
            "(SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail AND estado = 'Pendiente')")
    List<Anuncio> findAnunciosPendientesReservadosByUserEmail(String userEmail);
    @Query("SELECT * FROM anuncio WHERE estado = 'Aceptado' AND id IN " +
            "(SELECT id_anuncio FROM reserva WHERE id_usuario = :userEmail AND estado = 'Rechazado')")
    List<Anuncio> findAnunciosRechazadosReservadosByUserEmail(String userEmail);

    @Query("SELECT * FROM anuncio WHERE estado = 'Reservado' OR estado = 'Aceptado' AND id IN " +
            "(SELECT id_anuncio FROM reserva " +
            "WHERE id_usuario = :userEmail AND estado = 'Reservado')")
    List<Anuncio> findAnunciosReservadosByUserEmail(String userEmail);
    @Query("SELECT * FROM anuncio WHERE strftime('%d/%m/%Y', fecha_tutoria) = :dateString")
    List<Anuncio> findAnunciosByDate(String dateString);
    @Query("SELECT * FROM anuncio")
    List<Anuncio> getAll();
}
