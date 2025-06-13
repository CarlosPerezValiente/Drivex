package com.carlosperez.drivex;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.carlosperez.drivex.dao.AlumnoDAO;
import com.carlosperez.drivex.dao.HorarioDAO;
import com.carlosperez.drivex.database.DrivexDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    private LinearLayout layoutEstadisticas;
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        layoutEstadisticas = findViewById(R.id.layoutEstadisticas);
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        AlumnoDAO alumnoDAO = new AlumnoDAO(this);
        HorarioDAO horarioDAO = new HorarioDAO(this);

        int totalAlumnos = alumnoDAO.contarAlumnosPorUsuario(idUsuario);
        int totalHorarios = horarioDAO.contarHorariosPorUsuario(idUsuario);
        int clasesHoy = horarioDAO.contarClasesHoy(idUsuario);

        mostrarEstadistica("Total de alumnos:", totalAlumnos);
        mostrarEstadistica("Total de horarios:", totalHorarios);
        mostrarEstadistica("Clases programadas hoy:", clasesHoy);
    }

    private void mostrarEstadistica(String titulo, int cantidad) {
        TextView tv = new TextView(this);
        tv.setText(titulo + " " + cantidad);
        tv.setTextSize(18);
        tv.setPadding(0, 16, 0, 16);
        layoutEstadisticas.addView(tv);
    }
}
