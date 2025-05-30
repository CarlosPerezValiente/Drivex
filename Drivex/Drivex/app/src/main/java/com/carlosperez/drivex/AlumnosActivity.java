package com.carlosperez.drivex;

import android.content.Intent;
import android.os.Bundle;
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
        int idUsuario = getIntent().getIntExtra("idUsuario", -1);
        List<Alumno> alumnos = alumnoDAO.obtenerPorUsuario(idUsuario);


        for (Alumno alumno : alumnos) {
            LinearLayout contenedor = new LinearLayout(this);
            contenedor.setOrientation(LinearLayout.VERTICAL);
            contenedor.setPadding(0, 8, 0, 8);

            TextView tv = new TextView(this);
            tv.setText(alumno.getNombre() + " " + alumno.getApellidos() + " (" + alumno.getDni() + ")");
            tv.setTextSize(16);

            Button btnVerHorario = new Button(this);
            btnVerHorario.setText("Ver horario");
            btnVerHorario.setOnClickListener(v -> {
                Intent intent = new Intent(AlumnosActivity.this, HorariosActivity.class);
                intent.putExtra("idAlumno", alumno.getId());
                startActivity(intent);
            });

            contenedor.addView(tv);
            contenedor.addView(btnVerHorario);

            listaAlumnosLayout.addView(contenedor);
        }
    }

}
