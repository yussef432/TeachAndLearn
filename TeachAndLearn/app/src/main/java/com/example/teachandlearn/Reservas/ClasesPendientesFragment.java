package com.example.teachandlearn.Reservas;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.teachandlearn.Adaptadores.ClasesAdapter;
import com.example.teachandlearn.BBDD.Anuncio;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Reserva;
import com.example.teachandlearn.R;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.TextView;
public class ClasesPendientesFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_pendientes, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_pendientes);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        db = AppDatabase.getInstance(getContext());
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);

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
                    textViewNoAnuncios.setVisibility(View.GONE);
                } else {
                    textViewNoAnuncios.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
