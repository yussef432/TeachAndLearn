package com.example.teachandlearn;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Crear_Anuncio extends AppCompatActivity {

    private EditText tituloAnuncio, horasAnuncio, precioPorHora, descripcionAnuncio;
    private TextView fechaTutoria;
    private Spinner tipoAnuncio;
    private Button guardarAnuncio;
    private AppDatabase db;
    String estado = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_anuncio);

        tituloAnuncio = findViewById(R.id.titulo_anuncio);
        fechaTutoria = findViewById(R.id.fecha_tutoria);
        tipoAnuncio = findViewById(R.id.tipo_anuncio);
        horasAnuncio = findViewById(R.id.horas_anuncio);
        precioPorHora = findViewById(R.id.precio_por_hora);
        descripcionAnuncio = findViewById(R.id.descripcion_anuncio);
        guardarAnuncio = findViewById(R.id.guardar_anuncio);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipo_anuncio, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoAnuncio.setAdapter(adapter);

        db = AppDatabase.getInstance(this);

        // Configurar el DatePicker para la fecha de tutoría
        fechaTutoria.setOnClickListener(v -> showDatePickerDialog());

        // Agregar TextWatchers para los campos de precio y horas
        precioPorHora.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("€")) {
                    precioPorHora.setText(String.format("%s €", s.toString()));
                    precioPorHora.setSelection(precioPorHora.getText().length() - 2); // Poner el cursor antes del símbolo €
                }
            }
        });

        horasAnuncio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().contains("h")) {
                    horasAnuncio.setText(String.format("%s h", s.toString()));
                    horasAnuncio.setSelection(horasAnuncio.getText().length() - 2); // Poner el cursor antes del símbolo h
                }
            }
        });

        guardarAnuncio.setOnClickListener(v -> saveAnuncio());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Crear_Anuncio.this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    fechaTutoria.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveAnuncio() {
        try {
            // Obtener los datos del anuncio...
            String titulo = tituloAnuncio.getText().toString();
            String fechaT = fechaTutoria.getText().toString();
            String tipo = tipoAnuncio.getSelectedItem().toString();
            int horas = Integer.parseInt(horasAnuncio.getText().toString().replace(" h", ""));
            float precio = Float.parseFloat(precioPorHora.getText().toString().replace(" €", ""));
            String descripcion = descripcionAnuncio.getText().toString();

            // Validar fechas
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaTutoriaDate = dateFormat.parse(fechaT);

            // Obtener la fecha de creación (fecha actual)
            Date fechaCreacionDate = new Date();

            if (fechaTutoriaDate.before(fechaCreacionDate)) {
                Toast.makeText(this, "La fecha de tutoría no puede ser anterior a la fecha de creación.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el ID del usuario actual mediante Firebase Authentication
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_SHORT).show();
                return;
            }
            String email = currentUser.getEmail();

            // Crear el objeto Anuncio y establecer el ID del usuario
            Anuncio anuncio = new Anuncio();
            anuncio.idUsuario = email;
            anuncio.titulo = titulo;
            anuncio.fechaCreacion = fechaCreacionDate;
            anuncio.fechaTutoria = fechaTutoriaDate;
            anuncio.horas = horas;
            anuncio.precioPorHora = precio;
            anuncio.descripcion = descripcion;
            anuncio.tipoAnuncio = tipo;
            anuncio.estado = estado;

            new Thread(() -> {
                db.anuncioDao().insert(anuncio);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Anuncio guardado", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el anuncio. Debe rellenar todos los campos correctamente. Código del error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickVolver(View view) {
        finish();
    }
}
