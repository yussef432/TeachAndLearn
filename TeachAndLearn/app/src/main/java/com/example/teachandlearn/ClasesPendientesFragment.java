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

public class ClasesPendientesFragment extends Fragment {

    private ListView listViewClases;
    private AppDatabase db;
    private ClasesAdapter adapter;
    private List<Anuncio> anuncios;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases_pendientes, container, false);

        listViewClases = view.findViewById(R.id.list_view_clases_pendientes);

        db = AppDatabase.getInstance(getContext());

        loadClasesPendientes();

        return view;
    }

    private void loadClasesPendientes() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            List<Anuncio> anuncios = db.anuncioDao().findAnunciosPendientesReservadosByUserEmail(userEmail);
            getActivity().runOnUiThread(() -> {
                if (anuncios != null && !anuncios.isEmpty()) {
                    adapter = new ClasesAdapter(getContext(), anuncios);
                    listViewClases.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron clases pendientes", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
