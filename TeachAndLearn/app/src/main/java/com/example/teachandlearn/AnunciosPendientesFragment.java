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
import android.widget.TextView;
public class AnunciosPendientesFragment extends Fragment {

    private ListView listViewAnuncios;
    private AppDatabase db;
    private AnuncioAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncios_pendientes, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios_pendientes);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        db = AppDatabase.getInstance(getContext());
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);

        loadAnunciosPendientes();
        swipeRefreshLayout.setOnRefreshListener(() -> loadAnunciosPendientes());

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
                    textViewNoAnuncios.setVisibility(View.GONE);
                } else {
                    textViewNoAnuncios.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }
}
