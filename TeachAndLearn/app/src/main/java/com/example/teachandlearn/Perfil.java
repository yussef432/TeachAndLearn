package com.example.teachandlearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Perfil extends Fragment {

    private TextView nombreUsuario, apellidosUsuario, emailUsuario, telefonoUsuario, descripcionUsuario;
    private ImageView fotoPerfil;
    private Button editarPerfil, cerrarSesion;
    private AppDatabase db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Usuario usuario;

    private boolean isForReserva = false; // Variable para controlar si se muestra para una reserva

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        nombreUsuario = view.findViewById(R.id.nombre_usuario);
        apellidosUsuario = view.findViewById(R.id.apellidos_usuario);
        emailUsuario = view.findViewById(R.id.email_usuario);
        telefonoUsuario = view.findViewById(R.id.telefono_usuario);
        descripcionUsuario = view.findViewById(R.id.descripcion_usuario);
        fotoPerfil = view.findViewById(R.id.foto_perfil);
        editarPerfil = view.findViewById(R.id.editar_perfil);
        cerrarSesion = view.findViewById(R.id.cerrar_sesion);

        Bundle args = getArguments();
        if (args != null) {
            // Si los argumentos están presentes, se muestra para una reserva
            isForReserva = true;
            nombreUsuario.setText(args.getString("nombre"));
            apellidosUsuario.setText(args.getString("apellidos"));
            emailUsuario.setText(args.getString("email"));
            telefonoUsuario.setText(args.getString("telefono"));
            descripcionUsuario.setText(args.getString("descripcion"));

            String fotoUrl = args.getString("fotoPerfil");
            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(this).load(fotoUrl).into(fotoPerfil);
            }

            editarPerfil.setVisibility(View.GONE);
            cerrarSesion.setVisibility(View.GONE);
        } else {
            // Si no, carga el perfil del usuario actual
            db = AppDatabase.getInstance(getContext());
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            if (user != null) {
                loadUserProfile();
            }

            editarPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), EditarPerfil.class);
                startActivity(intent);
            });

            cerrarSesion.setOnClickListener(v -> {
                auth.signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                getActivity().finish();
            });
        }

        return view;
    }

    private void loadUserProfile() {
        new Thread(() -> {
            usuario = db.usuarioDao().findByEmail(user.getEmail());
            if (usuario != null) {
                getActivity().runOnUiThread(() -> {
                    nombreUsuario.setText(usuario.nombre);
                    apellidosUsuario.setText(usuario.apellidos);
                    emailUsuario.setText(user.getEmail());  // Establece el correo electrónico desde Firebase
                    telefonoUsuario.setText(usuario.telefono);
                    descripcionUsuario.setText(usuario.descripcion);
                    if (!usuario.fotoPerfil.isEmpty()) {
                        Glide.with(this).load(usuario.fotoPerfil).into(fotoPerfil);
                    }
                });
            }
        }).start();
    }
}
