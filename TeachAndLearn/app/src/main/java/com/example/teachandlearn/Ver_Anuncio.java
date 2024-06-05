package com.example.teachandlearn;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Ver_Anuncio extends AppCompatActivity {

    private TextView titulo, descripcion, horas, precio, fechaTutoria, fechaCreacion, tipoAnuncio;
    private Button btnReservar, btnVerPerfilAnunciante;
    private LinearLayout reservasLayout;
    private AppDatabase db;
    private int anuncioId;
    private Anuncio anuncio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_anuncio);

        titulo = findViewById(R.id.ver_titulo_anuncio);
        descripcion = findViewById(R.id.ver_descripcion_anuncio);
        horas = findViewById(R.id.ver_horas_anuncio);
        precio = findViewById(R.id.ver_precio_por_hora);
        fechaTutoria = findViewById(R.id.ver_fecha_tutoria);
        fechaCreacion = findViewById(R.id.ver_fecha_creacion);
        tipoAnuncio = findViewById(R.id.ver_tipo_anuncio);
        btnReservar = findViewById(R.id.btn_reservar_anuncio);
        reservasLayout = findViewById(R.id.reservas_layout);
        btnVerPerfilAnunciante = findViewById(R.id.btn_ver_perfil_anunciante);

        db = AppDatabase.getInstance(this);

        anuncioId = getIntent().getIntExtra("ANUNCIO_ID", -1);

        if (anuncioId != -1) {
            loadAnuncio();
        } else {
            Toast.makeText(this, "Error al cargar el anuncio", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnVerPerfilAnunciante.setOnClickListener(v -> togglePerfilAnuncianteCard());
    }

    private void loadAnuncio() {
        new Thread(() -> {
            anuncio = db.anuncioDao().findById(anuncioId);
            if (anuncio != null) {
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                boolean isReservedByUser = db.anuncioDao().isAnuncioReservedByUser(anuncioId, userEmail);
                boolean anuncioAceptado = db.anuncioDao().anuncioAceptado(anuncioId);

                runOnUiThread(() -> {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd 'de' MMMM 'a las' HH:mm 'de' yyyy", new Locale("es", "ES"));
                    String fechaTutoriaFormatted = dateFormat.format(anuncio.getFechaTutoria());
                    String fechaCreacionFormatted = dateFormat.format(anuncio.getFechaCreacion());
                    titulo.setText(anuncio.getTitulo());
                    descripcion.setText(anuncio.getDescripcion());
                    horas.setText(String.format("%d h", anuncio.getHoras()));
                    precio.setText(String.format("%.2f €", anuncio.getPrecioPorHora()));
                    fechaTutoria.setText(fechaTutoriaFormatted);
                    fechaCreacion.setText(fechaCreacionFormatted);
                    tipoAnuncio.setText(anuncio.getTipoAnuncio());

                    if (userEmail.equals(anuncio.getIdUsuario())) {
                        btnReservar.setVisibility(View.GONE);
                        btnVerPerfilAnunciante.setVisibility(View.GONE);
                        loadReservas();
                    } else {
                        if (isReservedByUser) {
                            btnReservar.setVisibility(View.GONE);
                        } else {
                            btnReservar.setVisibility(View.VISIBLE);
                            setupReservarButton();
                        }
                    }

                });
            }
        }).start();
    }

    private void loadReservas() {
        new Thread(() -> {
            List<ReservaConUsuario> reservas = db.reservaDao().getReservasWithUsuario(anuncioId);
            boolean anuncioAceptado = db.anuncioDao().anuncioAceptado(anuncioId);
            runOnUiThread(() -> {
                reservasLayout.setVisibility(View.VISIBLE);
                for (ReservaConUsuario reserva : reservas) {
                    if (anuncioAceptado && !"Reservado".equals(reserva.estado)) {
                        continue;
                    }
                    View reservaView = LayoutInflater.from(this).inflate(R.layout.item_reserva, reservasLayout, false);
                    TextView nombreReserva = reservaView.findViewById(R.id.nombre_reserva);
                    TextView emailReserva = reservaView.findViewById(R.id.email_reserva);
                    Button verPerfil = reservaView.findViewById(R.id.ver_perfil);
                    Button aceptarReserva = reservaView.findViewById(R.id.aceptar_reserva);

                    nombreReserva.setText(reserva.nombre);
                    emailReserva.setText(reserva.idUsuario);

                    verPerfil.setOnClickListener(v -> {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Perfil perfilFragment = (Perfil) fragmentManager.findFragmentByTag("perfilFragment");

                        if (perfilFragment != null && perfilFragment.isVisible()) {
                            fragmentManager.popBackStack();
                        } else {
                            perfilFragment = new Perfil();
                            Bundle args = new Bundle();
                            args.putString("nombre", reserva.nombre);
                            args.putString("apellidos", reserva.apellidos);
                            args.putString("email", reserva.idUsuario);
                            args.putString("telefono", reserva.telefono);
                            args.putString("descripcion", reserva.descripcion);
                            args.putString("fotoPerfil", reserva.fotoPerfil);
                            perfilFragment.setArguments(args);

                            fragmentManager.beginTransaction()
                                    .replace(R.id.profile_container, perfilFragment, "perfilFragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });


                    if (anuncioAceptado && "Reservado".equals(reserva.estado)) {
                        aceptarReserva.setVisibility(View.GONE);
                    } else {
                        aceptarReserva.setOnClickListener(v -> {
                            new Thread(() -> {
                                AnuncioDao anuncioDao = db.anuncioDao();
                                db.reservaDao().updateReservado(reserva.id, "Reservado", reserva.idUsuario);
                                db.reservaDao().updateRechazado(anuncio.id, "Rechazado", reserva.idUsuario);
                                anuncio.setEstado("Aceptado");
                                anuncioDao.update(anuncio);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Reserva aceptada", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                });
                            }).start();
                        });
                    }

                    reservasLayout.addView(reservaView);
                }
            });
        }).start();
    }

    private void setupReservarButton() {
        btnReservar.setOnClickListener(v -> reservarAnuncio());
    }

    private void reservarAnuncio() {
        AnuncioDao anuncioDao = db.anuncioDao();
        new Thread(() -> {
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            db.runInTransaction(() -> {
                Reserva reserva = new Reserva();
                reserva.setIdAnuncio(anuncioId);
                reserva.setIdUsuario(userEmail);
                reserva.setEstado("Pendiente");
                db.reservaDao().insert(reserva);

                anuncio.setEstado("Pendiente");
                anuncioDao.update(anuncio);
            });

            runOnUiThread(() -> {
                Toast.makeText(Ver_Anuncio.this, "Peticion de reserva enviada con éxito", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
    private void togglePerfilAnuncianteCard() {
        View cardViewAnunciante = findViewById(R.id.cardview_anunciante);
        if (cardViewAnunciante.getVisibility() == View.VISIBLE) {
            cardViewAnunciante.setVisibility(View.GONE);
        } else {
            cardViewAnunciante.setVisibility(View.VISIBLE);
            mostrarInformacionAnunciante();
        }
    }

//

    private void mostrarInformacionAnunciante() {
        new Thread(() -> {
            Usuario anunciante = db.usuarioDao().findByEmail(anuncio.getIdUsuario());
            if (anunciante != null) {
                runOnUiThread(() -> {
                    TextView nombreAnunciante = findViewById(R.id.nombre_anunciante);
                    TextView apellidosAnunciante = findViewById(R.id.apellidos_anunciante);
                    TextView emailAnunciante = findViewById(R.id.email_anunciante);
                    TextView telefonoAnunciante = findViewById(R.id.telefono_anunciante);
                    TextView descripcionAnunciante = findViewById(R.id.descripcion_anunciante);

                    nombreAnunciante.setText(anunciante.nombre);
                    apellidosAnunciante.setText(anunciante.apellidos);
                    emailAnunciante.setText(anunciante.email);
                    telefonoAnunciante.setText(anunciante.telefono);
                    descripcionAnunciante.setText(anunciante.descripcion);
                });
            }
        }).start();
    }



    public void onClickVolver(View view) {
        finish();
    }
}
