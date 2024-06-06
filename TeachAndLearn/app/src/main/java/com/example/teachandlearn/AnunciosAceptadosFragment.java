package com.example.teachandlearn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner spinnerFilter;
    private String selectedFilter = "Todos";
    private TextView textViewNoAnuncios;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncios_aceptados, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios_aceptados);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        spinnerFilter = view.findViewById(R.id.spinner_filter);
        textViewNoAnuncios = view.findViewById(R.id.text_view_no_anuncios);


        db = AppDatabase.getInstance(getContext());

        loadAnunciosAceptados();

        swipeRefreshLayout.setOnRefreshListener(() -> loadAnunciosAceptados());

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFilter = parent.getItemAtPosition(position).toString();
                loadAnunciosAceptados();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return view;
    }

    private void loadAnunciosAceptados() {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            long today = System.currentTimeMillis();
            List<Anuncio> anuncios;
            if (selectedFilter.equals("Todos")) {
                anuncios = db.anuncioDao().findAceptadosByUserEmail(userEmail, today);
            } else {
                anuncios = db.anuncioDao().findAceptadosByUserEmailAndType(userEmail, selectedFilter, today);
            }
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
