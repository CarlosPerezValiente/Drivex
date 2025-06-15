package com.carlosperez.drivex;

import android.animation.LayoutTransition;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.carlosperez.drivex.dao.AlumnoDAO;
import com.carlosperez.drivex.dao.HorarioDAO;
import com.carlosperez.drivex.model.Horario;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {

    private LinearLayout statsLayout;
    private HorarioDAO horarioDAO;
    private AlumnoDAO alumnoDAO;
    private int idUsuario;
    private String nombreUsuario;
    private boolean alumnosMostrados = false;
    private LinearLayout contenedorAlumnos; // Lo sacamos a nivel global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);
        setTitle("Estadísticas");

        statsLayout = findViewById(R.id.statsLayout);
        statsLayout.setLayoutTransition(new LayoutTransition());

        horarioDAO = new HorarioDAO(this);
        alumnoDAO = new AlumnoDAO(this);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombreUsuario");

        if (idUsuario == -1) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        statsLayout.removeAllViews();

        int totalAlumnos = alumnoDAO.obtenerTodosPorUsuario(idUsuario).size();
        int totalClases = horarioDAO.obtenerTotalClases(idUsuario);
        int clasesFuturas = horarioDAO.obtenerTotalClasesFuturas(idUsuario);
        int clasesPasadas = horarioDAO.obtenerTotalClasesPasadas(idUsuario);
        Horario proximaClase = horarioDAO.obtenerProximaClase(idUsuario);
        String[] fechas = horarioDAO.obtenerRangoFechas(idUsuario);

        float porcentajeOcupacion = totalClases > 0 ? (clasesPasadas * 100f / totalClases) : 0;
        float mediaPorAlumno = totalAlumnos > 0 ? (float) totalClases / totalAlumnos : 0;

        TextView titulo = new TextView(this);

        titulo.setTextSize(24);
        titulo.setPadding(0, 0, 0, 40);
        statsLayout.addView(titulo);

        CardView cardAlumnos = crearTarjeta("👨‍🎓 Total de alumnos: " + totalAlumnos);
        LinearLayout layoutAlumnos = (LinearLayout) cardAlumnos.getChildAt(0);

        Button btnAlumnos = new Button(this);
        btnAlumnos.setText("Ver alumnos");
        btnAlumnos.setTextColor(0xFFFFFFFF);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(30);
        drawable.setColor(0xFF1976D2);
        btnAlumnos.setBackground(drawable);
        btnAlumnos.setPadding(32, 12, 32, 12);
        LinearLayout.LayoutParams paramsBoton = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsBoton.setMargins(0, 20, 0, 0);
        btnAlumnos.setLayoutParams(paramsBoton);
        layoutAlumnos.addView(btnAlumnos);

        contenedorAlumnos = new LinearLayout(this);
        contenedorAlumnos.setOrientation(LinearLayout.VERTICAL);
        contenedorAlumnos.setVisibility(View.GONE);
        layoutAlumnos.addView(contenedorAlumnos);
        statsLayout.addView(cardAlumnos);

        btnAlumnos.setOnClickListener(v -> {
            if (!alumnosMostrados) {
                mostrarListaAlumnos(contenedorAlumnos);
                alumnosMostrados = true;
                contenedorAlumnos.setVisibility(View.VISIBLE);
            } else {
                alumnosMostrados = false;
                contenedorAlumnos.setVisibility(View.GONE);
            }
        });

        statsLayout.addView(crearTarjeta("📅 Total de clases registradas: " + totalClases));
        statsLayout.addView(crearTarjeta("⏳ Clases futuras: " + clasesFuturas));
        statsLayout.addView(crearTarjeta("✅ Clases realizadas: " + clasesPasadas));
        statsLayout.addView(crearTarjeta("📈 Ocupación: " + Math.round(porcentajeOcupacion) + "%"));
        statsLayout.addView(crearTarjeta("📊 Media de clases por alumno: " + String.format(Locale.US, "%.2f", mediaPorAlumno)));
        statsLayout.addView(crearTarjeta("📅 Primer clase: " + formatearFecha(fechas[0])));
        statsLayout.addView(crearTarjeta("📅 Última clase: " + formatearFecha(fechas[1])));

        if (proximaClase != null) {
            String textoClase = "🚗 Próxima clase:\n\n" +
                    "👤 Alumno: " + proximaClase.getDescripcion() + "\n" +
                    "📅 Fecha: " + formatearFecha(proximaClase.getFecha()) + "\n" +
                    "⏰ Hora: " + proximaClase.getHoraInicio() + " - " + proximaClase.getHoraFin();
            statsLayout.addView(crearTarjeta(textoClase));
        } else {
            statsLayout.addView(crearTarjeta("🚗 No hay clases próximas."));
        }
    }

    private void mostrarListaAlumnos(LinearLayout contenedorAlumnos) {
        contenedorAlumnos.removeAllViews();
        List<String> alumnos = alumnoDAO.obtenerTodosPorUsuario(idUsuario);
        for (String alumno : alumnos) {
            contenedorAlumnos.addView(crearTarjetaAlumno(alumno));
        }
    }

    private CardView crearTarjeta(String texto) {
        CardView card = new CardView(this);
        card.setRadius(24);
        card.setCardElevation(8);
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(0xFFFFFFFF);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextSize(18);
        layout.addView(tv);
        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 32, 0, 0);
        card.setLayoutParams(params);

        return card;
    }

    private CardView crearTarjetaAlumno(String alumno) {
        CardView card = new CardView(this);
        card.setRadius(20);
        card.setCardElevation(6);
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(0xFFE3F2FD);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 28, 28, 28);

        TextView tv = new TextView(this);
        tv.setText("👤 " + alumno);
        tv.setTextSize(16);
        layout.addView(tv);
        card.addView(layout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 16, 0, 0);
        card.setLayoutParams(params);

        return card;
    }

    private String formatearFecha(String fechaBD) {
        try {
            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoUsuario.format(formatoBD.parse(fechaBD));
        } catch (Exception e) {
            e.printStackTrace();
            return fechaBD;
        }
    }
}
