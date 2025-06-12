package com.carlosperez.drivex;

import android.app.DatePickerDialog;
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

public class AgendaActivity extends AppCompatActivity {

    private LinearLayout agendaLayout;
    private EditText etFecha;
    private Button btnBuscar;
    private HorarioDAO horarioDAO;
    private int idUsuario;

    private final Calendar calendario = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        agendaLayout = findViewById(R.id.agendaLayout);
        etFecha = findViewById(R.id.etFechaFiltrada);
        btnBuscar = findViewById(R.id.btnFiltrarAgenda);
        horarioDAO = new HorarioDAO(this);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuario == -1) {
            TextView error = new TextView(this);
            error.setText("No se pudo obtener el ID del usuario.");
            agendaLayout.addView(error);
            return;
        }

        // Selector de fecha
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFecha.setText(formatoFecha.format(calendario.getTime()));
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Botón buscar
        btnBuscar.setOnClickListener(v -> cargarAgendaPorFecha());
    }

    private void cargarAgendaPorFecha() {
        agendaLayout.removeAllViews();

        String fecha = etFecha.getText().toString().trim();
        if (fecha.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Horario> agenda = horarioDAO.obtenerAgendaPorFecha(fecha, idUsuario);

        if (agenda.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No hay clases para este día.");
            empty.setPadding(0, 32, 0, 32);
            agendaLayout.addView(empty);
        } else {
            for (Horario h : agenda) {
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

                TextView tvDescripcion = new TextView(this);
                tvDescripcion.setText(h.getDescripcion());
                tvDescripcion.setTextSize(16);

                card.addView(tvFecha);
                card.addView(tvHora);
                card.addView(tvDescripcion);
                agendaLayout.addView(card);
            }
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
