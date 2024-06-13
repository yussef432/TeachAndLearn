package com.example.teachandlearn.Autentificacion;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teachandlearn.BBDD.AppDatabase;
import com.example.teachandlearn.BBDD.Usuario;
import com.example.teachandlearn.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextContrasenia, editTextConfirmarContrasenia, editTextNombre, editTextApellidos, editTextTelefono, editTextDescripcion;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextContrasenia = findViewById(R.id.contrasenia);
        editTextConfirmarContrasenia = findViewById(R.id.confirmar_contrasenia);
        editTextNombre = findViewById(R.id.nombre);
        editTextApellidos = findViewById(R.id.apellidos);
        editTextTelefono = findViewById(R.id.telefono);
        editTextDescripcion = findViewById(R.id.descripcion);

        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, contrasenia, confirmarContrasenia, nombre, apellidos, telefono, descripcion;
                email = String.valueOf(editTextEmail.getText());
                contrasenia = String.valueOf(editTextContrasenia.getText());
                confirmarContrasenia = String.valueOf(editTextConfirmarContrasenia.getText());
                nombre = String.valueOf(editTextNombre.getText());
                apellidos = String.valueOf(editTextApellidos.getText());
                telefono = String.valueOf(editTextTelefono.getText());
                descripcion = String.valueOf(editTextDescripcion.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasenia) || TextUtils.isEmpty(confirmarContrasenia) ||
                        TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellidos) || TextUtils.isEmpty(telefono) ||
                        TextUtils.isEmpty(descripcion)) {
                    Toast.makeText(Register.this, "Todos los campos deben estar rellenados", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Register.this, "El email debe tener un formato válido (ej. usuario@dominio.com)", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (contrasenia.length() < 6) {
                    Toast.makeText(Register.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!contrasenia.equals(confirmarContrasenia)) {
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (telefono.length() < 9) {
                    Toast.makeText(Register.this, "El número de teléfono debe tener al menos 9 dígitos", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, contrasenia)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Usuario usuario = new Usuario();
                                    usuario.email = user.getEmail();
                                    usuario.nombre = nombre;
                                    usuario.apellidos = apellidos;
                                    usuario.telefono = telefono;
                                    usuario.descripcion = descripcion;
                                    usuario.fotoPerfil = "";
                                    new Thread(() -> {
                                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                                        db.usuarioDao().insert(usuario);
                                        runOnUiThread(() -> {
                                            Toast.makeText(Register.this, "Cuenta creada.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        });
                                    }).start();
                                } else {
                                    Toast.makeText(Register.this, "Autenticación fallida compruebe si el email ya existe.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
