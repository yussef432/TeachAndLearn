package com.example.teachandlearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnunciosAceptadosFragment extends Fragment {

    private ListView listViewAnuncios;
    private AppDatabase db;
    private AnuncioAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncios_aceptados, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios_aceptados);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        db = AppDatabase.getInstance(getContext());

        loadAnunciosAceptados();


        swipeRefreshLayout.setOnRefreshListener(() -> loadAnunciosAceptados());


        return view;
    }

    private void loadAnunciosAceptados() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long today = System.currentTimeMillis();
            List<Anuncio> anuncios = db.anuncioDao().findAceptadosByUserEmail(userEmail, today);
            getActivity().runOnUiThread(() -> {
                if (anuncios != null && !anuncios.isEmpty()) {
                    adapter = new AnuncioAdapter(getContext(), anuncios);
                    listViewAnuncios.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "No se encontraron anuncios aceptados", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
