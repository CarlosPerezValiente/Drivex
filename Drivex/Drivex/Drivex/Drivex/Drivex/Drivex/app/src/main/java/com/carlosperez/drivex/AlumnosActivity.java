package com.carlosperez.drivex;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

    // Declaración de variables de interfaz
    private LinearLayout listaAlumnosLayout;
    private EditText etNombreAlumno, etApellidosAlumno, etDniAlumno;
    private Button btnAgregarAlumno;
    private AlumnoDAO alumnoDAO;
    private int idUsuario;  // ID del usuario logueado (profesor o administrador)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos);
        setTitle("Gestión de Alumnos");

        // Recupera el ID del usuario que viene desde el login
        idUsuario = getIntent().getIntExtra("idUsuario", -1);

        // Si no se recibe un ID válido, muestra error y cierra la actividad
        if (idUsuario == -1) {
            Toast.makeText(this, "Error: ID de usuario no recibido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Enlaza los elementos de la interfaz con el código
        listaAlumnosLayout = findViewById(R.id.listaAlumnosLayout);
        etNombreAlumno = findViewById(R.id.etNombreAlumno);
        etApellidosAlumno = findViewById(R.id.etApellidosAlumno);
        etDniAlumno = findViewById(R.id.etDniAlumno);
        btnAgregarAlumno = findViewById(R.id.btnAgregarAlumno);

        // Inicializa el DAO de alumnos
        alumnoDAO = new AlumnoDAO(this);

        // Carga la lista inicial de alumnos
        cargarListaAlumnos();

        // Lógica para añadir un nuevo alumno al pulsar el botón
        btnAgregarAlumno.setOnClickListener(v -> {
            String nombre = etNombreAlumno.getText().toString().trim();
            String apellidos = etApellidosAlumno.getText().toString().trim();
            String dni = etDniAlumno.getText().toString().trim();

            // Validación simple del formato de DNI español
            if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
                Toast.makeText(this, "Introduce un DNI válido (8 números + letra)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validación de campos vacíos
            if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crea el objeto alumno con los datos introducidos
            Alumno nuevo = new Alumno(nombre, apellidos, dni, idUsuario);
            boolean exito = alumnoDAO.insertarAlumno(nuevo);

            // Muestra mensajes según si se insertó correctamente
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

    // Metodo que carga y muestra la lista de alumnos
    private void cargarListaAlumnos() {
        listaAlumnosLayout.removeAllViews(); // Limpia la vista

        // Consulta a la base de datos los alumnos de este usuario
        List<Alumno> alumnos = alumnoDAO.obtenerPorUsuario(idUsuario);

        // Si no hay alumnos, muestra un mensaje informativo
        if (alumnos.isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("No hay alumnos registrados.");
            vacio.setPadding(0, 32, 0, 32);
            listaAlumnosLayout.addView(vacio);
            return;
        }

        // Si hay alumnos, genera las "tarjetas" de cada alumno
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

            // Nombre completo del alumno
            TextView tvNombre = new TextView(this);
            tvNombre.setText(alumno.getNombre() + " " + alumno.getApellidos());
            tvNombre.setTextSize(18);
            tvNombre.setTextColor(Color.parseColor("#333333"));

            // DNI del alumno
            TextView tvDni = new TextView(this);
            tvDni.setText("DNI: " + alumno.getDni());
            tvDni.setTextSize(16);
            tvDni.setTextColor(Color.parseColor("#666666"));

            // Layout horizontal para los dos botones (ver horario y eliminar)
            LinearLayout botonesLayout = new LinearLayout(this);
            botonesLayout.setOrientation(LinearLayout.HORIZONTAL);
            botonesLayout.setGravity(Gravity.CENTER);
            botonesLayout.setPadding(0, 16, 0, 0);

            // ---------- Botón Ver Horario ----------
            Button btnVerHorario = new Button(this);
            btnVerHorario.setText("Ver horario");
            btnVerHorario.setTextSize(14);
            btnVerHorario.setTextColor(Color.WHITE);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(30);
            drawable.setColor(0xFF1976D2); // Color azul
            btnVerHorario.setBackground(drawable);

            LinearLayout.LayoutParams btnParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams1.setMargins(16, 0, 16, 0);
            btnVerHorario.setLayoutParams(btnParams1);

            // Acción al pulsar el botón de ver horario
            btnVerHorario.setOnClickListener(v -> {
                Intent intent = new Intent(AlumnosActivity.this, HorariosActivity.class);
                intent.putExtra("idAlumno", alumno.getId());
                startActivity(intent);
            });

            // ---------- Botón Eliminar ----------
            Button btnEliminar = new Button(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextSize(14);
            btnEliminar.setTextColor(Color.WHITE);

            GradientDrawable drawable2 = new GradientDrawable();
            drawable2.setCornerRadius(30);
            drawable2.setColor(0xFFD32F2F); // Color rojo
            btnEliminar.setBackground(drawable2);

            LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnParams2.setMargins(16, 0, 16, 0);
            btnEliminar.setLayoutParams(btnParams2);

            // Acción al pulsar eliminar: borra el alumno de la base de datos
            btnEliminar.setOnClickListener(v -> {
                alumnoDAO.eliminarAlumno(alumno.getId());
                cargarListaAlumnos();  // Refresca la lista
            });

            // Añadimos los botones al layout de botones
            botonesLayout.addView(btnVerHorario);
            botonesLayout.addView(btnEliminar);

            // Añadimos el contenido a la tarjeta
            card.addView(tvNombre);
            card.addView(tvDni);
            card.addView(botonesLayout);

            // Finalmente, añadimos la tarjeta a la lista principal
            listaAlumnosLayout.addView(card);
        }
    }
}
