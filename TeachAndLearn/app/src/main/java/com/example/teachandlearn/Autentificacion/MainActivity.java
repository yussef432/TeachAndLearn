package com.example.teachandlearn.Autentificacion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teachandlearn.Anuncios.Anuncios_Alumnos;
import com.example.teachandlearn.Anuncios.Anuncios_Profesores;
import com.example.teachandlearn.Fragmentos.Area_Personal;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Usuario;
import com.example.teachandlearn.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    FirebaseAuth auth;
    FirebaseUser user;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.bottom_area_personal) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Area_Personal()).commit();
                    return true;
                } else if (itemId == R.id.bottom_anuncios_alumnos) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Anuncios_Alumnos()).commit();
                    return true;
                } else if (itemId == R.id.bottom_anuncios_profesores) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Anuncios_Profesores()).commit();
                    return true;
                }
                return false;
            }
        });

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = AppDatabase.getInstance(this);

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Comprobar si el usuario ya está en la base de datos local
            new Thread(() -> {
                Usuario usuario = db.usuarioDao().findByEmail(user.getEmail());
                if (usuario == null) {
                    // Usuario no está en la base de datos local, agregarlo
                    usuario = new Usuario();
                    usuario.email = user.getEmail();
                    usuario.nombre = "";
                    usuario.apellidos = ""; // Suponiendo que displayName es el "nombre"
                    usuario.telefono = ""; // No hay número de teléfono desde Firebase Auth
                    usuario.contraseña = ""; // No almacenamos la contraseña localmente
                    usuario.fotoPerfil = ""; // No hay URL de foto de perfil
                    usuario.descripcion = ""; // Sin descripción
                    db.usuarioDao().insert(usuario);
                }
            }).start();

            // Establecer el fragmento predeterminado
            bottomNavigationView.setSelectedItemId(R.id.bottom_area_personal);
        }
    }
}
