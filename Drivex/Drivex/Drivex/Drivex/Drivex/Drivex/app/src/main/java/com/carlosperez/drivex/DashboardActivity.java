package com.carlosperez.drivex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Actividad principal tras el login: menú de navegación
public class DashboardActivity extends AppCompatActivity {

    // Declaración de variables de la interfaz
    private TextView tvBienvenido;
    private Button btnAlumnos, btnCerrarSesion, btnVerAgenda;
    private int idUsuario;  // ID del usuario logueado, se pasa por el intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);  // Carga el layout de dashboard

        // Enlaza los elementos de la vista
        tvBienvenido = findViewById(R.id.tvBienvenido);
        btnAlumnos = findViewById(R.id.btnAlumnos);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnVerAgenda = findViewById(R.id.btnVerAgenda);

        // Recupera el nombre e ID del usuario que vienen desde el LoginActivity
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Si recibió el nombre, muestra el mensaje de bienvenida
        if (nombreUsuario != null) {
            tvBienvenido.setText("Bienvenido " + nombreUsuario);
        }

        // Botón para abrir la pantalla de gestión de alumnos
        btnAlumnos.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AlumnosActivity.class);
            intent.putExtra("idUsuario", idUsuario);  // Pasamos el ID para filtrar los alumnos
            startActivity(intent);
        });

        // Botón para abrir la agenda general
        btnVerAgenda.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AgendaActivity.class);
            intent.putExtra("idUsuario", idUsuario);  // Pasamos el ID para cargar la agenda de este usuario
            startActivity(intent);
        });

        // Botón para abrir la pantalla de estadísticas
        Button btnVerEstadisticas = findViewById(R.id.btnVerEstadisticas);
        btnVerEstadisticas.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EstadisticasActivity.class);
            intent.putExtra("idUsuario", idUsuario);  // También enviamos el ID para las estadísticas
            startActivity(intent);
        });

        // Botón para cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            // Limpiamos las SharedPreferences donde guardamos la sesión
            SharedPreferences prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // Eliminamos los datos de sesión
            editor.apply();

            // Volvemos al LoginActivity y limpiamos la pila de actividades
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();  // Cerramos esta actividad para evitar que vuelva atrás
        });
    }
}
