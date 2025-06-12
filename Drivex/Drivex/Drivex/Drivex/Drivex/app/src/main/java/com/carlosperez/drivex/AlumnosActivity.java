package com.carlosperez.drivex;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.AlumnoDAO;
import com.carlosperez.drivex.model.Alumno;

import java.util.List;

public class AlumnosActivity extends AppCompatActivity {

    private LinearLayout listaAlumnosLayout;
    private EditText etNombreAlumno, etApellidosAlumno, etDniAlumno;
    private Button btnAgregarAlumno;
    private AlumnoDAO alumnoDAO;
    private int idUsuario;  // 🔹 Guardar ID del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos);

        // 🔹 Recuperar ID del usuario desde el intent
        idUsuario = getIntent().getIntExtra("idUsuario", -1);  // 👈 Aquí sin 'int'

        if (idUsuario == -1) {
            Toast.makeText(this, "Error: ID de usuario no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Enlazar vistas
        listaAlumnosLayout = findViewById(R.id.listaAlumnosLayout);
        etNombreAlumno = findViewById(R.id.etNombreAlumno);
        etApellidosAlumno = findViewById(R.id.etApellidosAlumno);
        etDniAlumno = findViewById(R.id.etDniAlumno);
        btnAgregarAlumno = findViewById(R.id.btnAgregarAlumno);

        alumnoDAO = new AlumnoDAO(this);

        cargarListaAlumnos();

        btnAgregarAlumno.setOnClickListener(v -> {
            String nombre = etNombreAlumno.getText().toString().trim();
            String apellidos = etApellidosAlumno.getText().toString().trim();
            String dni = etDniAlumno.getText().toString().trim();

            // Validación de formato DNI español simple
            if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
                Toast.makeText(this, "Introduce un DNI válido (8 números + letra)", Toast.LENGTH_SHORT).show();
                return;
            }


            if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Alumno nuevo = new Alumno(nombre, apellidos, dni, idUsuario);  // 🔹 Asignar usuario
            boolean exito = alumnoDAO.insertarAlumno(nuevo);

            if (exito) {
                Toast.makeText(this, "Alumno añadido correctamente", Toast.LENGTH_SHORT).show();
                etNombreAlumno.setText("");
                etApellidosAlumno.setText("");
                etDniAlumno.setText("");
                cargarListaAlumnos();
            } else {
                Toast.makeText(this, "Error al añadir alumno", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cargarListaAlumnos() {
        listaAlumnosLayout.removeAllViews();
        List<Alumno> alumnos = alumnoDAO.obtenerPorUsuario(idUsuario);

        if (alumnos.isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("No hay alumnos registrados.");
            vacio.setPadding(0, 32, 0, 32);
            listaAlumnosLayout.addView(vacio);
            return;
        }

        for (Alumno alumno : alumnos) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(32, 32, 32, 32);
            card.setBackgroundColor(Color.parseColor("#EFEFEF"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 24, 0, 24);
            card.setLayoutParams(params);

            TextView tvNombre = new TextView(this);
            tvNombre.setText(alumno.getNombre() + " " + alumno.getApellidos());
            tvNombre.setTextSize(18);
            tvNombre.setTextColor(Color.parseColor("#333333"));

            TextView tvDni = new TextView(this);
            tvDni.setText("DNI: " + alumno.getDni());
            tvDni.setTextSize(16);
            tvDni.setTextColor(Color.parseColor("#666666"));

            // Contenedor horizontal de botones centrado
            LinearLayout botonesLayout = new LinearLayout(this);
            botonesLayout.setOrientation(LinearLayout.HORIZONTAL);
            botonesLayout.setGravity(Gravity.CENTER);
            botonesLayout.setPadding(0, 16, 0, 0);

            // Botón Ver Horario
            Button btnVerHorario = new Button(this);
            btnVerHorario.setText("Ver horario");
            btnVerHorario.setTextSize(14);
            btnVerHorario.setTextColor(Color.WHITE);
            btnVerHorario.setBackgroundColor(Color.parseColor("#1976D2"));

            LinearLayout.LayoutParams btnParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams1.setMargins(16, 0, 16, 0);
            btnVerHorario.setLayoutParams(btnParams1);

            btnVerHorario.setOnClickListener(v -> {
                Intent intent = new Intent(AlumnosActivity.this, HorariosActivity.class);
                intent.putExtra("idAlumno", alumno.getId());
                startActivity(intent);
            });

            // Botón Eliminar
            Button btnEliminar = new Button(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextSize(14);
            btnEliminar.setTextColor(Color.WHITE);
            btnEliminar.setBackgroundColor(Color.parseColor("#D32F2F"));

            LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams2.setMargins(16, 0, 16, 0);
            btnEliminar.setLayoutParams(btnParams2);

            btnEliminar.setOnClickListener(v -> {
                alumnoDAO.eliminarAlumno(alumno.getId());
                cargarListaAlumnos();
            });

            botonesLayout.addView(btnVerHorario);
            botonesLayout.addView(btnEliminar);

            card.addView(tvNombre);
            card.addView(tvDni);
            card.addView(botonesLayout);

            listaAlumnosLayout.addView(card);
        }
    }



}
