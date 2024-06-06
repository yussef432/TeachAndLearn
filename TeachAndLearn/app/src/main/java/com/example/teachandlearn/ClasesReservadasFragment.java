package com.example.teachandlearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import android.widget.TextView;
public class ClasesReservadasFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinnerFilter;
    private String selectedFilter = "Todos";
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_reservadas, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_reservadas);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);

        db = AppDatabase.getInstance(getContext());
        swipeRefreshLayout.setOnRefreshListener(() -> loadClasesReservadas());
        loadClasesReservadas();

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString();
                loadClasesReservadas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void loadClasesReservadas() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long today = System.currentTimeMillis();
            List<Anuncio> anunciosReservados;
            List<Reserva> reservasReservadas = db.reservaDao().findReservadosByUserEmail(userEmail);

            if (selectedFilter.equals("Todos")) {
                anunciosReservados = db.anuncioDao().findAnunciosReservadosByUserEmail(userEmail, today);
            } else {
                anunciosReservados = db.anuncioDao().findAnunciosReservadosByUserEmailAndType(userEmail, selectedFilter, today);
            }

            getActivity().runOnUiThread(() -> {
                if (anunciosReservados != null && !anunciosReservados.isEmpty()) {
                    adapter = new ClasesAdapter(getContext(), anunciosReservados, reservasReservadas);
                    listViewClases.setAdapter(adapter);
                    textViewNoAnuncios.setVisibility(View.GONE);
                } else {
                    textViewNoAnuncios.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
