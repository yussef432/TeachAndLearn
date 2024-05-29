// Calendario.java
package com.example.teachandlearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.decorators.EventDecorator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class Calendario extends Fragment {

    private MaterialCalendarView calendarView;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        db = AppDatabase.getInstance(getContext());

        loadEvents();

        return view;
    }

    private void loadEvents() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Load reservations and accepted ads
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                List<Anuncio> anuncios = db.anuncioDao().findAcceptedByUserEmail(userEmail);
                List<Reserva> reservas = db.reservaDao().findReservadosByUserEmail(userEmail);

                getActivity().runOnUiThread(() -> {
                    // Add events to calendar
                    for (Anuncio anuncio : anuncios) {
                        CalendarDay day = CalendarDay.from(anuncio.fechaTutoria);
                        calendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.colorProfesor), day));
                    }
                    for (Reserva reserva : reservas) {
                        Anuncio anuncio = db.anuncioDao().findById(reserva.idAnuncio);
                        if (anuncio != null) {
                            CalendarDay day = CalendarDay.from(anuncio.fechaTutoria);
                            calendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.colorAlumno), day));
                        }
                    }
                });
            }
        });
    }

    private static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, CalendarDay date) {
            this.color = color;
            this.dates = new HashSet<>();
            this.dates.add(date);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }
}
