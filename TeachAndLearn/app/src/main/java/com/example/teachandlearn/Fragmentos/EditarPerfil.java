package com.example.teachandlearn.Fragmentos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.teachandlearn.Autentificacion.Login;
import com.example.teachandlearn.Autentificacion.MainActivity;
import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Usuario;
import com.example.teachandlearn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class EditarPerfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewEditarFotoPerfil;
    private EditText editTextNombre, editTextApellidos, editTextTelefono, editTextDescripcion;
    private AppDatabase db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        imageViewEditarFotoPerfil = findViewById(R.id.editar_foto_perfil);
        editTextNombre = findViewById(R.id.editar_nombre_usuario);
        editTextApellidos = findViewById(R.id.editar_apellidos_usuario);
        editTextTelefono = findViewById(R.id.editar_telefono_usuario);
        editTextDescripcion = findViewById(R.id.editar_descripcion_usuario);

        db = AppDatabase.getInstance(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        } else {
            loadUserProfile();
        }

        imageViewEditarFotoPerfil.setOnClickListener(v -> openImagePicker());
    }

    private void loadUserProfile() {
        new Thread(() -> {
            usuario = db.usuarioDao().findByEmail(user.getEmail());
            if (usuario != null) {
                runOnUiThread(() -> {
                    editTextNombre.setText(usuario.nombre);
                    editTextApellidos.setText(usuario.apellidos);
                    editTextTelefono.setText(usuario.telefono);
                    editTextDescripcion.setText(usuario.descripcion);

                    if (usuario.fotoPerfil != null && !usuario.fotoPerfil.isEmpty()) {
                        Log.d("EditarPerfil", "Loading image URL: " + usuario.fotoPerfil);
                        Glide.with(this).load(usuario.fotoPerfil).into(imageViewEditarFotoPerfil);
                    }
                });
            }
        }).start();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageViewEditarFotoPerfil.setImageBitmap(bitmap);
                usuario.fotoPerfil = uri.toString(); // Update the photo URL
                Log.d("EditarPerfil", "Selected image URL: " + usuario.fotoPerfil);
                Glide.with(this).load(uri).into(imageViewEditarFotoPerfil);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickGuardarPerfil(View view) {
        String nombre = editTextNombre.getText().toString();
        String apellidos = editTextApellidos.getText().toString();
        String telefono = editTextTelefono.getText().toString();
        String descripcion = editTextDescripcion.getText().toString();

        if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben estar rellenados", Toast.LENGTH_SHORT).show();
            return;
        }

        if (telefono.length() < 9) {
            Toast.makeText(this, "El número de teléfono debe tener al menos 9 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        usuario.nombre = nombre;
        usuario.apellidos = apellidos;
        usuario.telefono = telefono;
        usuario.descripcion = descripcion;

        new Thread(() -> {
            db.usuarioDao().update(usuario);
            runOnUiThread(() -> {
                Toast.makeText(EditarPerfil.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }

    public void onClickVolverPerfil(View view) {
        finish();
    }
}
