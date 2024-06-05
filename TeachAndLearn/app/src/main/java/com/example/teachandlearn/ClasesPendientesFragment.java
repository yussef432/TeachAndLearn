package com.example.teachandlearn;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ClasesPendientesFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_pendientes, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_pendientes);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        db = AppDatabase.getInstance(getContext());

        loadClasesPendientes();
        swipeRefreshLayout.setOnRefreshListener(() -> loadClasesPendientes());
        return view;
    }

    private void loadClasesPendientes() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            List<Anuncio> anunciosPendientes = db.anuncioDao().findAnunciosPendientesReservadosByUserEmail(userEmail);
            List<Reserva> reservasPendientes = db.reservaDao().findPendientesByUserEmail(userEmail);
            getActivity().runOnUiThread(() -> {
                if (anunciosPendientes != null && !anunciosPendientes.isEmpty()) {
                    adapter = new ClasesAdapter(getContext(), anunciosPendientes, reservasPendientes);
                    listViewClases.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron clases pendientes", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
