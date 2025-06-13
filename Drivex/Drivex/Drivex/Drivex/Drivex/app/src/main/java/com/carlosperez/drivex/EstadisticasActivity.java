package com.carlosperez.drivex;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.carlosperez.drivex.dao.AlumnoDAO;
import com.carlosperez.drivex.dao.HorarioDAO;
import com.carlosperez.drivex.model.Horario;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    private LinearLayout statsLayout;
    private AlumnoDAO alumnoDAO;
    private HorarioDAO horarioDAO;
    private int idUsuario;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        statsLayout = findViewById(R.id.statsLayout);
        alumnoDAO = new AlumnoDAO(this);
        horarioDAO = new HorarioDAO(this);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        if (idUsuario == -1) {
            mostrarError("Error al obtener usuario");
            return;
        }

        mostrarTitulo("Estadísticas de " + nombreUsuario);
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        int totalAlumnos = alumnoDAO.obtenerPorUsuario(idUsuario).size();
        int totalHorarios = horarioDAO.obtenerTotalHorariosPorUsuario(idUsuario);
        int alumnosSinHorario = alumnoDAO.obtenerAlumnosSinHorario(idUsuario).size();
        String proximaClase = obtenerProximaClase();

        añadirTarjeta("👥 Total de alumnos", String.valueOf(totalAlumnos));
        añadirTarjeta("📅 Total de clases programadas", String.valueOf(totalHorarios));
        añadirTarjeta("❗ Alumnos sin horario asignado", String.valueOf(alumnosSinHorario));
        añadirTarjeta("🕑 Próxima clase", proximaClase);
    }

    private void mostrarTitulo(String texto) {
        TextView titulo = new TextView(this);
        titulo.setText(texto);
        titulo.setTextSize(20);
        titulo.setPadding(0, 0, 0, 40);
        statsLayout.addView(titulo);
    }

    private void añadirTarjeta(String titulo, String valor) {
        CardView card = new CardView(this);
        card.setCardElevation(8);
        card.setRadius(20);
        card.setUseCompatPadding(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 16);
        card.setLayoutParams(params);

        LinearLayout contenido = new LinearLayout(this);
        contenido.setPadding(32, 32, 32, 32);
        contenido.setOrientation(LinearLayout.VERTICAL);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(titulo);
        tvTitulo.setTextSize(16);
        tvTitulo.setPadding(0, 0, 0, 8);

        TextView tvValor = new TextView(this);
        tvValor.setText(valor);
        tvValor.setTextSize(24);
        tvValor.setTextColor(getResources().getColor(android.R.color.black));

        contenido.addView(tvTitulo);
        contenido.addView(tvValor);
        card.addView(contenido);
        statsLayout.addView(card);
    }

    private void mostrarError(String mensaje) {
        TextView error = new TextView(this);
        error.setText(mensaje);
        statsLayout.addView(error);
    }

    private String obtenerProximaClase() {
        List<Horario> horarios = horarioDAO.obtenerHorariosProximos(idUsuario);
        if (horarios.isEmpty()) {
            return "No hay clases programadas.";
        } else {
            Horario h = horarios.get(0);
            String fechaFormateada = formatearFecha(h.getFecha());
            return fechaFormateada + " | " + h.getHoraInicio() + " - " + h.getHoraFin();
        }
    }

    private String formatearFecha(String fechaBD) {
        try {
            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoUsuario.format(formatoBD.parse(fechaBD));
        } catch (Exception e) {
            return fechaBD;
        }
    }
}
