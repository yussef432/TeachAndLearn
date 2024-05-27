package com.example.teachandlearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Ingrese Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contrasenia)) {
                    Toast.makeText(Register.this, "Ingrese Contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmarContrasenia)) {
                    Toast.makeText(Register.this, "Confirme su Contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!contrasenia.equals(confirmarContrasenia)) {
                    Toast.makeText(Register.this, "Las Contraseñas no Coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nombre)) {
                    Toast.makeText(Register.this, "Ingrese Nombre", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(apellidos)) {
                    Toast.makeText(Register.this, "Ingrese Apellidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(telefono)) {
                    Toast.makeText(Register.this, "Ingrese Teléfono", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(descripcion)) {
                    Toast.makeText(Register.this, "Ingrese Descripción", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(Register.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
