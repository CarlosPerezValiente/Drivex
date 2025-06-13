package com.carlosperez.drivex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvBienvenido;
    private Button btnAlumnos, btnCerrarSesion, btnVerAgenda;
    private int idUsuario;  // 🔹 Para mantener el ID del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvBienvenido = findViewById(R.id.tvBienvenido);
        btnAlumnos = findViewById(R.id.btnAlumnos);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnVerAgenda = findViewById(R.id.btnVerAgenda);

        // 🔹 Recuperamos nombre e ID del usuario desde LoginActivity
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        if (nombreUsuario != null) {
            tvBienvenido.setText("Bienvenido " + nombreUsuario);
        }

        // 🔹 Ir a AlumnosActivity con el id del usuario
        btnAlumnos.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AlumnosActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            startActivity(intent);
        });

        btnVerAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AgendaActivity.class);
            intent.putExtra("idUsuario", idUsuario); // Añadir ID
            startActivity(intent);
        });

        Button btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas);
        btnVerEstadisticas.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EstadisticasActivity.class);
            intent.putExtra("idUsuario", idUsuario);
            startActivity(intent);
        });





        // 🔹 Cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Limpiamos la sesión
            editor.apply();

            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }
}
