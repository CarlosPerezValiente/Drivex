package com.carlosperez.drivex;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.AlumnoDAO;
import com.carlosperez.drivex.dao.HorarioDAO;
import com.carlosperez.drivex.model.Horario;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Actividad que gestiona los horarios de un alumno concreto
public class HorariosActivity extends AppCompatActivity {

    // Declaración de variables de interfaz
    private LinearLayout contenedorHorarios;
    private EditText etFecha, etHoraInicio, etHoraFin, etDescripcion;
    private Button btnAgregarHorario;
    private HorarioDAO horarioDAO;
    private AlumnoDAO alumnoDAO;

    private int idAlumno;  // ID del alumno al que pertenecen los horarios
    private final Calendar calendario = Calendar.getInstance();
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);
        setTitle("Horario del alumno");

        alumnoDAO = new AlumnoDAO(this);

        // Enlaza las vistas de la interfaz
        contenedorHorarios = findViewById(R.id.contenedorHorarios);
        etFecha = findViewById(R.id.etFecha);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        etDescripcion = findViewById(R.id.etDescripcion);

        // Desactiva teclado manual para horas (solo seleccionables por TimePicker)
        etHoraInicio.setKeyListener(null);
        etHoraFin.setKeyListener(null);

        // Seleccionador de Hora de Inicio
        etHoraInicio.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                etHoraInicio.setText(horaSeleccionada);
            }, hour, minute, true).show();
        });

        // Seleccionador de Hora de Fin
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

        // Recibimos el ID del alumno al que vamos a asignar los horarios
        idAlumno = getIntent().getIntExtra("idAlumno", -1);
        if (idAlumno == -1) {
            Toast.makeText(this, "ID de alumno no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        horarioDAO = new HorarioDAO(this);

        // Selector de fecha para el campo de fecha
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendario.set(Calendar.YEAR, year);
                calendario.set(Calendar.MONTH, month);
                calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etFecha.setText(formatoFecha.format(calendario.getTime()));
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Al pulsar agregar horario
        btnAgregarHorario.setOnClickListener(v -> {
            String fecha = etFecha.getText().toString().trim();
            String inicio = etHoraInicio.getText().toString().trim();
            String fin = etHoraFin.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (fecha.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea un nuevo objeto horario
            Horario nuevo = new Horario();
            nuevo.setIdAlumno(idAlumno);
            nuevo.setFecha(fecha);
            nuevo.setHoraInicio(inicio);
            nuevo.setHoraFin(fin);
            nuevo.setDescripcion(descripcion);

            boolean exito = horarioDAO.insertarHorario(nuevo);
            if (exito) {
                Toast.makeText(this, "Horario guardado", Toast.LENGTH_SHORT).show();
                etFecha.setText("");
                etHoraInicio.setText("");
                etHoraFin.setText("");
                etDescripcion.setText("");
                cargarHorarios();  // Recarga la lista de horarios
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });

        cargarHorarios();
    }

    // Metodo que carga los horarios del alumno y los muestra
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

            TextView tvDescripcion = new TextView(this);
            tvDescripcion.setText("📝 Descripción: " + h.getDescripcion());
            tvDescripcion.setTextSize(16);

            // Botón Exportar PDF
            Button btnExportar = new Button(this);
            btnExportar.setText("Exportar PDF");
            btnExportar.setTextColor(Color.WHITE);
            GradientDrawable drawableExportar = new GradientDrawable();
            drawableExportar.setCornerRadius(30);
            drawableExportar.setColor(0xFF388E3C);
            btnExportar.setBackground(drawableExportar);
            LinearLayout.LayoutParams exportParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            exportParams.setMargins(10, 0, 10, 0);
            btnExportar.setLayoutParams(exportParams);
            btnExportar.setOnClickListener(v -> exportarHorarioPDF(h));

            // BOTÓN DE FIRMAR
            Button btnFirmar = new Button(this);
            btnFirmar.setText("Firmar");
            btnFirmar.setTextColor(Color.WHITE);

            GradientDrawable drawableFirmar = new GradientDrawable();
            drawableFirmar.setCornerRadius(30);
            drawableFirmar.setColor(0xFF1976D2);  // Azulito
            btnFirmar.setBackground(drawableFirmar);

            LinearLayout.LayoutParams firmarParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            firmarParams.setMargins(10, 0, 10, 0);
            btnFirmar.setLayoutParams(firmarParams);

// Acción de firmar
            btnFirmar.setOnClickListener(v -> {
                Intent intent = new Intent(HorariosActivity.this, FirmaActivity.class);
                intent.putExtra("idHorario", h.getId());
                startActivity(intent);
            });



            // Botón Eliminar horario
            Button btnEliminar = new Button(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextColor(Color.WHITE);
            GradientDrawable drawableEliminar = new GradientDrawable();
            drawableEliminar.setCornerRadius(30);
            drawableEliminar.setColor(0xFFD32F2F);
            btnEliminar.setBackground(drawableEliminar);
            LinearLayout.LayoutParams eliminarParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            eliminarParams.setMargins(10, 0, 10, 0);
            btnEliminar.setLayoutParams(eliminarParams);
            btnEliminar.setOnClickListener(v -> {
                horarioDAO.eliminarHorario(h.getId());
                cargarHorarios();
                Toast.makeText(this, "Horario eliminado", Toast.LENGTH_SHORT).show();
            });

            // Contenedor de botones
            LinearLayout botonesLayout = new LinearLayout(this);
            botonesLayout.setOrientation(LinearLayout.HORIZONTAL);
            botonesLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            botonesLayout.setPadding(0, 10, 0, 0);
            botonesLayout.addView(btnExportar);
            botonesLayout.addView(btnEliminar);
            botonesLayout.addView(btnFirmar);


            // Añadimos todo al card
            card.addView(tvFecha);
            card.addView(tvHora);
            card.addView(tvDescripcion);
            card.addView(botonesLayout);
            contenedorHorarios.addView(card);
        }
    }

    // Formatea fechas de BD a dd/MM/yyyy
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

    // Exporta el horario seleccionado a un PDF
    private void exportarHorarioPDF(Horario horario) {
        PdfDocument documento = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page pagina = documento.startPage(pageInfo);

        Canvas canvas = pagina.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(12);
        paint.setColor(Color.BLACK);

        int x = 10, y = 25;

        String nombreAlumno = alumnoDAO.obtenerNombrePorId(idAlumno);

        canvas.drawText("Detalle de Clase:", x, y, paint);
        y += 25;
        canvas.drawText("Alumno: " + nombreAlumno, x, y, paint);
        y += 20;
        canvas.drawText("Fecha: " + formatearFecha(horario.getFecha()), x, y, paint);
        y += 20;
        canvas.drawText("Hora: " + horario.getHoraInicio() + " - " + horario.getHoraFin(), x, y, paint);
        y += 20;
        canvas.drawText("Descripción: " + horario.getDescripcion(), x, y, paint);

        y += 40;

        // Intentar cargar la firma si existe
        try {
            File fileFirma = new File(getFilesDir(), "firma_" + horario.getId() + ".png");
            if (fileFirma.exists()) {
                Bitmap firma = android.graphics.BitmapFactory.decodeFile(fileFirma.getAbsolutePath());
                Bitmap firmaEscalada = Bitmap.createScaledBitmap(firma, 100, 50, true);
                canvas.drawText("Firma:", x, y, paint);
                y += 20;
                canvas.drawBitmap(firmaEscalada, x, y, null);
            } else {
                canvas.drawText("No se ha registrado firma.", x, y, paint);
            }
        } catch (Exception e) {
            e.printStackTrace();
            canvas.drawText("Error al cargar la firma", x, y, paint);
        }

        documento.finishPage(pagina);

        try {
            File file = new File(getExternalFilesDir(null), "Clase_" + horario.getId() + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            documento.writeTo(fos);
            documento.close();
            fos.close();

            Toast.makeText(this, "PDF guardado: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al exportar PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
