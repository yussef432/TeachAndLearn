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

import java.util.List;

public class AnunciosPendientesFragment extends Fragment {

    private ListView listViewAnuncios;
    private AppDatabase db;
    private AnuncioAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncios_pendientes, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios_pendientes);

        db = AppDatabase.getInstance(getContext());

        loadAnunciosPendientes();

        return view;
    }

    private void loadAnunciosPendientes() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            List<Anuncio> anuncios = db.anuncioDao().findPendientesByUserEmail(userEmail);
            getActivity().runOnUiThread(() -> {
                if (anuncios != null && !anuncios.isEmpty()) {
                    adapter = new AnuncioAdapter(getContext(), anuncios);
                    listViewAnuncios.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron anuncios pendientes", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}
