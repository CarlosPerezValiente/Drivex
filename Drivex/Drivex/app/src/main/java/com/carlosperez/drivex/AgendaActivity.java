package com.carlosperez.drivex;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.HorarioDAO;

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
        horarioDAO = new HorarioDAO(this);

        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuario == -1) {
            TextView error = new TextView(this);
            error.setText("No se pudo obtener el ID del usuario.");
            agendaLayout.addView(error);
            return;
        }

        // Crear campo de fecha y botón
        etFecha = new EditText(this);
        etFecha.setHint("Selecciona una fecha");
        etFecha.setFocusable(false);
        etFecha.setPadding(0, 16, 0, 16);

        btnBuscar = new Button(this);
        btnBuscar.setText("Ver agenda del día");

        agendaLayout.addView(etFecha);
        agendaLayout.addView(btnBuscar);

        // Picker de fecha
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFecha.setText(formatoFecha.format(calendario.getTime()));
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Buscar al hacer clic
        btnBuscar.setOnClickListener(v -> cargarAgendaPorFecha());
    }

    private void cargarAgendaPorFecha() {
        agendaLayout.removeViews(2, agendaLayout.getChildCount() - 2); // deja fecha y botón

        String fecha = etFecha.getText().toString().trim();
        if (fecha.isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> agenda = horarioDAO.obtenerAgendaPorFecha(fecha, idUsuario);

        if (agenda.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No hay clases para este día.");
            empty.setPadding(0, 32, 0, 32);
            agendaLayout.addView(empty);
        } else {
            for (String item : agenda) {
                TextView tv = new TextView(this);
                tv.setText(item);
                tv.setTextSize(16);
                tv.setPadding(0, 16, 0, 16);
                agendaLayout.addView(tv);
            }
        }
    }
}
