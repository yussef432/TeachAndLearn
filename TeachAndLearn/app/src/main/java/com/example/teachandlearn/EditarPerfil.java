package com.example.teachandlearn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
                    if (!usuario.fotoPerfil.isEmpty()) {
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
                usuario.fotoPerfil = uri.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClickGuardarPerfil(View view) {
        usuario.nombre = editTextNombre.getText().toString();
        usuario.apellidos = editTextApellidos.getText().toString();
        usuario.telefono = editTextTelefono.getText().toString();
        usuario.descripcion = editTextDescripcion.getText().toString();

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