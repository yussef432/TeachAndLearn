package com.example.teachandlearn.Anuncios;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.teachandlearn.Adaptadores.AnuncioAdapter;
import com.example.teachandlearn.BBDD.Anuncio;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;

public class Anuncios_Alumnos extends Fragment {

    private ListView listViewAnuncios;
    private FloatingActionButton fabAddAnuncio;
    private AppDatabase db;
    private AnuncioAdapter adapter;
    private SearchView searchView;
    private Spinner spinnerFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anuncios_alumnos, container, false);

        listViewAnuncios = view.findViewById(R.id.list_view_anuncios);
        fabAddAnuncio = view.findViewById(R.id.fab_add_anuncio);
        searchView = view.findViewById(R.id.search_view);
        spinnerFilter = view.findViewById(R.id.spinner_filter);

        db = AppDatabase.getInstance(getContext());

        fabAddAnuncio.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Crear_Anuncio.class);
            startActivity(intent);
        });

        // Configuración del Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);

        // Cargar anuncios
        loadAnunciosAlumnos(null, "default");

        // Configuración del SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadAnunciosAlumnos(query, spinnerFilter.getSelectedItem().toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadAnunciosAlumnos(newText, spinnerFilter.getSelectedItem().toString());
                return false;
            }
        });


        // Configuración del Spinner
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadAnunciosAlumnos(searchView.getQuery().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadAnunciosAlumnos(searchView.getQuery().toString(), "default");
            }
        });

        return view;
    }

    private void loadAnunciosAlumnos(String query, String filter) {
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            List<Anuncio> anuncios;
            long today = new Date().getTime();
            if (TextUtils.isEmpty(query)) {
                anuncios = db.anuncioDao().findAnunciosByTypeAndNotUser("Alumno", userEmail, filter, today);

            } else {
                anuncios = db.anuncioDao().searchAnuncios("Alumno", userEmail, "%" + query + "%", filter, today);
            }
            getActivity().runOnUiThread(() -> {
                if (anuncios != null && !anuncios.isEmpty()) {
                    adapter = new AnuncioAdapter(getContext(), anuncios);
                    listViewAnuncios.setAdapter(adapter);
                } else {
                    listViewAnuncios.setAdapter(null);
                }
            });
        }).start();
    }
}
