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

public class ClasesReservadasFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_reservadas, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_reservadas);

        db = AppDatabase.getInstance(getContext());

        loadClasesReservadas();

        return view;
    }

    private void loadClasesReservadas() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long today = System.currentTimeMillis();
            List<Anuncio> anunciosReservados = db.anuncioDao().findAnunciosReservadosByUserEmail(userEmail, today);
            List<Reserva> reservasReservadas = db.reservaDao().findReservadosByUserEmail(userEmail);
            getActivity().runOnUiThread(() -> {
                if (anunciosReservados != null && !anunciosReservados.isEmpty()) {
                    adapter = new ClasesAdapter(getContext(), anunciosReservados, reservasReservadas);
                    listViewClases.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron clases reservadas", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
