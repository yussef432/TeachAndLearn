package com.example.teachandlearn;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClasesRechazadasFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_rechazadas, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_rechazadas);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        db = AppDatabase.getInstance(getContext());
        swipeRefreshLayout.setOnRefreshListener(() -> loadClasesRechazadas());

        loadClasesRechazadas();

        return view;
    }

    private void loadClasesRechazadas() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long today = new Date().getTime();
            List<Anuncio> anunciosRechazados = db.anuncioDao().findAnunciosRechazadosReservadosByUserEmail(userEmail, today);
            List<Reserva> reservasRechazadas = db.reservaDao().findReservasRechazadasByUserEmail(userEmail);
            getActivity().runOnUiThread(() -> {
                if (anunciosRechazados != null && !anunciosRechazados.isEmpty()) {
                    adapter = new ClasesAdapter(getContext(), anunciosRechazados, reservasRechazadas);
                    listViewClases.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron clases rechazadas", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
