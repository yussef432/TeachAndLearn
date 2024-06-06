package com.example.teachandlearn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Mis_Anuncios extends Fragment {

    private ListView listViewAnuncios;
    private FloatingActionButton fabAddAnuncio;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppDatabase db;
    private List<Anuncio> anuncios;
    private AnuncioAdapter adapter;
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_anuncios, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios);
        fabAddAnuncio = view.findViewById(R.id.fab_add_anuncio);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);

        db = AppDatabase.getInstance(getContext());

        loadAnuncios();

        fabAddAnuncio.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Crear_Anuncio.class);
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> loadAnuncios());

        return view;
    }

    private void loadAnuncios() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            anuncios = db.anuncioDao().findByUserEmailAndEstado(userEmail, "");
            getActivity().runOnUiThread(() -> {
                if (anuncios != null && !anuncios.isEmpty()) {
                    adapter = new AnuncioAdapter(getContext(), anuncios);
                    listViewAnuncios.setAdapter(adapter);
                    textViewNoAnuncios.setVisibility(View.GONE);
                } else {
                    textViewNoAnuncios.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
