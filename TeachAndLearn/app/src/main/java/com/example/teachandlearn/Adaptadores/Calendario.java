package com.example.teachandlearn.Adaptadores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teachandlearn.Adaptadores.Evento;
import com.example.teachandlearn.Adaptadores.EventoAdapter;
import com.example.teachandlearn.BBDD.Anuncio;
import com.example.teachandlearn.BBDD.AnuncioDao;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Reserva;
import com.example.teachandlearn.BBDD.ReservaDao;
import com.example.teachandlearn.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Calendario extends Fragment {

    private CalendarView calendarView;
    private TextView tvMessage;
    private TextView tvDates;

    private RecyclerView rvEventos;
    private EventoAdapter eventoAdapter;
    private List<Evento> eventosList = new ArrayList<>();

    private AnuncioDao anuncioDao;
    private ReservaDao reservaDao;

    private Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        tvMessage = view.findViewById(R.id.tv_message);
        tvDates = view.findViewById(R.id.tv_dates);
        rvEventos = view.findViewById(R.id.rv_eventos);

        rvEventos.setLayoutManager(new LinearLayoutManager(getContext()));
        eventoAdapter = new EventoAdapter(eventosList);
        rvEventos.setAdapter(eventoAdapter);

        // Obtener el DAO de Anuncio y Reserva
        anuncioDao = AppDatabase.getInstance(getContext()).anuncioDao();
        reservaDao = AppDatabase.getInstance(getContext()).reservaDao();

        cargarEventos();


        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String dateString = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            mostrarEventosDelDia(dateString);

        });

        return view;
    }

    private void cargarEventos() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Date currentDate = new Date();

        executor.execute(() -> {
            List<Anuncio> anunciosAceptados = anuncioDao.findAcceptedByUserEmail(userEmail);
            List<Reserva> reservas = reservaDao.findReservadosByUserEmail(userEmail);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            List<String> fechas = new ArrayList<>();
            List<Evento> eventosTempList = new ArrayList<>();

            for (Anuncio anuncio : anunciosAceptados) {
                if (anuncio.fechaTutoria.after(currentDate)) {
                    String fechaString = dateFormat.format(anuncio.fechaTutoria);
                    fechas.add(fechaString);
                    eventosTempList.add(new Evento(anuncio.titulo, anuncio.fechaTutoria));
                }
            }

            for (Reserva reserva : reservas) {
                Anuncio anuncio = anuncioDao.findById(reserva.idAnuncio);
                if (anuncio != null && anuncio.fechaTutoria.after(currentDate)) {
                    String fechaString = dateFormat.format(anuncio.fechaTutoria);
                    fechas.add(fechaString);
                    eventosTempList.add(new Evento(anuncio.titulo, anuncio.fechaTutoria));
                }
            }

            eventosTempList.sort((e1, e2) -> e1.getFechaHora().compareTo(e2.getFechaHora()));
            fechas.sort(String::compareTo);

            requireActivity().runOnUiThread(() -> {

                eventosList.clear();
                eventosList.addAll(eventosTempList);
                tvDates.setText(fechas.toString().replace("[", "").replace("]", ""));
                if (eventosList.isEmpty()) {
                    tvMessage.setText("No tienes clases");
                } else {
                    tvMessage.setText("Tienes clases estos días:");
                }
                eventoAdapter.notifyDataSetChanged();
            });
        });
    }

    private void mostrarEventosDelDia(String dateString) {
        executor.execute(() -> {
            List<Evento> eventosDelDia = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            for (Evento evento : eventosList) {
                String eventoFecha = dateFormat.format(evento.getFechaHora());
                if (eventoFecha.equals(dateString)) {
                    eventosDelDia.add(evento);
                }
            }

            requireActivity().runOnUiThread(() -> {

                eventoAdapter = new EventoAdapter(eventosDelDia);
                rvEventos.setAdapter(eventoAdapter);
            });
        });
    }
}
