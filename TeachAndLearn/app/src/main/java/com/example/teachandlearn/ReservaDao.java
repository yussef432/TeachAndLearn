package com.example.teachandlearn;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface ReservaDao {
    @Insert
    void insert(Reserva reserva);



    @Query("SELECT * FROM reserva WHERE id_anuncio = :anuncioId")
    List<Reserva> findByAnuncioId(int anuncioId);

    @Query("UPDATE reserva SET estado = :estado WHERE id = :reservaId")
    void updateReservaEstado(int reservaId, String estado);
    @Query("UPDATE reserva SET estado = :estado WHERE id = :reservaId AND id_usuario = :userEmail")
    void updateReservado(int reservaId, String estado, String userEmail);
    @Query("UPDATE reserva SET estado = :estado WHERE id_anuncio = :anuncioid AND id_usuario != :userEmail")
    void updateRechazado(int anuncioid, String estado, String userEmail);

    @Query("SELECT EXISTS(SELECT 1 FROM reserva WHERE id_anuncio = :anuncioId AND id_usuario = :userEmail)")
    boolean isAnuncioReservedByUser(int anuncioId, String userEmail);



    @Transaction
    default void reserveAnuncio(int anuncioId, String userEmail, AnuncioDao anuncioDao, String tipoAnuncio) {
        if (!isAnuncioReservedByUser(anuncioId, userEmail)) {
            Reserva reserva = new Reserva();
            reserva.setIdAnuncio(anuncioId);
            reserva.setIdUsuario(userEmail);
            reserva.setRolUsuario(tipoAnuncio);
            reserva.setFechaReserva(new Date().toString());
            reserva.setEstado("Pendiente");
            insert(reserva);

            Anuncio anuncio = anuncioDao.findAnuncioById(anuncioId);
            if (anuncio != null) {
                anuncio.setEstado("Reservado");
                anuncioDao.update(anuncio);
            }
        }
    }
    @Query("SELECT reserva.id AS id, reserva.id_anuncio AS idAnuncio, reserva.id_usuario " +
            "AS idUsuario, reserva.rol_usuario AS rolUsuario, reserva.fecha_reserva " +
            "AS fechaReserva, reserva.estado AS estado, usuario.nombre AS nombre," +
            " usuario.apellidos AS apellidos, usuario.telefono AS telefono, " +
            "usuario.descripcion AS descripcion, usuario.foto_perfil AS fotoPerfil " +
            "FROM reserva JOIN usuario " +
            "ON reserva.id_usuario = usuario.email WHERE reserva.id_anuncio = :anuncioId")
    List<ReservaConUsuario> getReservasWithUsuario(int anuncioId);
    @Query("SELECT * FROM reserva WHERE id_usuario = :userEmail AND estado = 'Pendiente'")
    List<Reserva> findPendientesByUserEmail(String userEmail);
    @Query("SELECT * FROM reserva WHERE id_usuario = :userEmail AND estado = 'Rechazado'")
    List<Reserva> findReservasRechazadasByUserEmail(String userEmail);

    @Query("SELECT * FROM reserva WHERE id_usuario = :userEmail AND estado = 'Reservado'")
    List<Reserva> findReservadosByUserEmail(String userEmail);


}
