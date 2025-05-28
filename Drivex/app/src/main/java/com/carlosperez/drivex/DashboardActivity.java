package com.carlosperez.drivex;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvBienvenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvBienvenido = findViewById(R.id.tvBienvenido);

        // Aquí recibimos el nombre del usuario que viene de la pantalla de login
        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        if (nombreUsuario != null) {
            tvBienvenido.setText("Bienvenido " + nombreUsuario);
        }
    }
}
