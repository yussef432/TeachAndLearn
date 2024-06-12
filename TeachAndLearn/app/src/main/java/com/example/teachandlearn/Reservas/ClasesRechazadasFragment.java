package com.example.teachandlearn.Reservas;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.teachandlearn.Adaptadores.ClasesAdapter;
import com.example.teachandlearn.BBDD.Anuncio;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Reserva;
import com.example.teachandlearn.R;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

import android.widget.TextView;
public class ClasesRechazadasFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_rechazadas, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_rechazadas);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);

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
                    textViewNoAnuncios.setVisibility(View.GONE);
                } else {
                    textViewNoAnuncios.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
