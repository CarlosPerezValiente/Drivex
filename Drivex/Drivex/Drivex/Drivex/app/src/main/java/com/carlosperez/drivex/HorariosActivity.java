package com.carlosperez.drivex;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.carlosperez.drivex.dao.HorarioDAO;
import com.carlosperez.drivex.model.Horario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HorariosActivity extends AppCompatActivity {

    private LinearLayout contenedorHorarios;
    private EditText etFecha, etHoraInicio, etHoraFin;
    private Button btnAgregarHorario;
    private HorarioDAO horarioDAO;
    private int idAlumno;
    private final Calendar calendario = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        // Enlazar vistas
        contenedorHorarios = findViewById(R.id.contenedorHorarios);
        etFecha = findViewById(R.id.etFecha);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        // Desactivar la escritura manual en los campos de horas
        etHoraInicio.setKeyListener(null);
        etHoraFin.setKeyListener(null);

// TimePicker para Hora de Inicio
        etHoraInicio.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                etHoraInicio.setText(horaSeleccionada);
            }, hour, minute, true).show();
        });

// TimePicker para Hora de Fin
        etHoraFin.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                etHoraFin.setText(horaSeleccionada);
            }, hour, minute, true).show();
        });

        btnAgregarHorario = findViewById(R.id.btnAgregarHorario);

        // Obtener el ID del alumno
        idAlumno = getIntent().getIntExtra("idAlumno", -1);
        if (idAlumno == -1) {
            Toast.makeText(this, "ID de alumno no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Instanciar DAO
        horarioDAO = new HorarioDAO(this);

        // Mostrar date picker al hacer clic en la fecha
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFecha.setText(formatoFecha.format(calendario.getTime()));
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Botón de agregar horario
        btnAgregarHorario.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString().trim();
            String inicio = etHoraInicio.getText().toString().trim();
            String fin = etHoraFin.getText().toString().trim();

            if (fecha.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Horario nuevo = new Horario();
            nuevo.setIdAlumno(idAlumno);
            nuevo.setFecha(fecha);
            nuevo.setHoraInicio(inicio);
            nuevo.setHoraFin(fin);

            boolean exito = horarioDAO.insertarHorario(nuevo);
            if (exito) {
                Toast.makeText(this, "Horario guardado", Toast.LENGTH_SHORT).show();
                etFecha.setText("");
                etHoraInicio.setText("");
                etHoraFin.setText("");
                cargarHorarios();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });

        cargarHorarios();
    }

    private void cargarHorarios() {
        contenedorHorarios.removeAllViews();
        List<Horario> lista = horarioDAO.obtenerHorariosPorAlumno(idAlumno);

        if (lista.isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("No hay horarios registrados para este alumno.");
            contenedorHorarios.addView(vacio);
        }

        for (Horario h : lista) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(20, 20, 20, 20);
            card.setBackgroundColor(Color.parseColor("#EFEFEF"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            card.setLayoutParams(params);

            TextView tvFecha = new TextView(this);
            tvFecha.setText("🗓 Fecha: " + formatearFecha(h.getFecha()));
            tvFecha.setTextSize(16);

            TextView tvHora = new TextView(this);
            tvHora.setText("🕐 Hora: " + h.getHoraInicio() + " - " + h.getHoraFin());
            tvHora.setTextSize(16);

            card.addView(tvFecha);
            card.addView(tvHora);
            contenedorHorarios.addView(card);
        }
    }

    private String formatearFecha(String fechaBD) {
        try {
            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoUsuario.format(formatoBD.parse(fechaBD));
        } catch (Exception e) {
            e.printStackTrace();
            return fechaBD;  // Si hay error, mostramos tal cual
        }
    }


}
