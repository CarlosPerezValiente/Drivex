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

    // Declaración de los elementos de la interfaz
    private LinearLayout agendaLayout;
    private EditText etFecha;
    private Button btnBuscar;
    private HorarioDAO horarioDAO; // Acceso a la base de datos de horarios
    private int idUsuario; // ID del usuario que consulta su agenda

    // Calendar y formato de fecha para el selector
    private final Calendar calendario = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda); // Carga el layout de la agenda
        setTitle("Agenda general"); // Título de la actividad

        // Inicialización de vistas
        agendaLayout = findViewById(R.id.agendaLayout);
        etFecha = findViewById(R.id.etFechaFiltrada);
        btnBuscar = findViewById(R.id.btnFiltrarAgenda);
        horarioDAO = new HorarioDAO(this);  // Inicializa el DAO de horarios

        // Recoge el ID del usuario que ha iniciado sesión (se pasa por intent)
        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuario == -1) {
            // Si no se ha recibido el ID, muestra error
            TextView error = new TextView(this);
            error.setText("No se pudo obtener el ID del usuario.");
            agendaLayout.addView(error);
            return;
        }

        // Configura el selector de fecha (DatePickerDialog)
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFecha.setText(formatoFecha.format(calendario.getTime()));  // Pone la fecha seleccionada en el EditText
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Configura el botón de buscar: al pulsar, carga la agenda según fecha
        btnBuscar.setOnClickListener(v -> cargarAgendaPorFecha());
    }

    // Metodo que carga la agenda según la fecha seleccionada
    private void cargarAgendaPorFecha() {
        agendaLayout.removeAllViews(); // Limpia la vista de resultados anteriores

        String fecha = etFecha.getText().toString().trim();
        if (fecha.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtiene la lista de horarios de la BD para ese día y usuario
        List<Horario> agenda = horarioDAO.obtenerAgendaPorFecha(fecha, idUsuario);

        if (agenda.isEmpty()) {
            // Si no hay clases para ese día, lo informa
            TextView empty = new TextView(this);
            empty.setText("No hay clases para este día.");
            empty.setPadding(0, 32, 0, 32);
            agendaLayout.addView(empty);
        } else {
            // Si hay clases, crea dinámicamente las "tarjetas" de cada clase
            for (Horario h : agenda) {
                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 20, 20, 20);
                card.setBackgroundColor(Color.parseColor("#EFEFEF")); // Color de fondo para la tarjeta
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 16, 0, 16);
                card.setLayoutParams(params);

                // Fecha
                TextView tvFecha = new TextView(this);
                tvFecha.setText("🗓 Fecha: " + formatearFecha(h.getFecha()));
                tvFecha.setTextSize(16);

                // Horario
                TextView tvHora = new TextView(this);
                tvHora.setText("🕐 Hora: " + h.getHoraInicio() + " - " + h.getHoraFin());
                tvHora.setTextSize(16);

                // Descripción (incluye también el nombre del alumno)
                TextView tvDescripcion = new TextView(this);
                tvDescripcion.setText("\uD83D\uDC64" + h.getDescripcion());
                tvDescripcion.setTextSize(16);

                // Añadimos todos los TextView a la tarjeta
                card.addView(tvFecha);
                card.addView(tvHora);
                card.addView(tvDescripcion);
                agendaLayout.addView(card);
            }
        }
    }

    // Metodo para formatear la fecha de la BD a un formato más amigable (DD/MM/YYYY)
    private String formatearFecha(String fechaBD) {
        try {
            SimpleDateFormat formatoBD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoUsuario = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoUsuario.format(formatoBD.parse(fechaBD));
        } catch (Exception e) {
            e.printStackTrace();
            return fechaBD;  // Si hay error, devuelve la fecha tal cual está en la BD
        }
    }
}
