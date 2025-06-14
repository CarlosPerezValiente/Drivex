package com.carlosperez.drivex;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.UsuarioDAO;
import com.carlosperez.drivex.model.Usuario;

public class RegistroUsuarioActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etContrasena;
    private Button btnGuardarUsuario;
    private UsuarioDAO usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        setTitle("Registro de usuario");

        // Inicializamos vistas
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        btnGuardarUsuario = findViewById(R.id.btnGuardarUsuario);

        // Inicializamos el DAO (ajusta según tu implementación)
        usuarioDao = new UsuarioDAO(this);

        // Listener botón guardar
        btnGuardarUsuario.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String contrasena = etContrasena.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "El email debe tener el formato de un gmail", Toast.LENGTH_SHORT).show();
                return;
            }


            Usuario nuevoUsuario = new Usuario(nombre, email, contrasena);

            boolean exito = usuarioDao.insertarUsuario(nuevoUsuario);

            if (exito) {
                Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show();
                // Puedes limpiar campos o cerrar la actividad si quieres
                etNombre.setText("");
                etEmail.setText("");
                etContrasena.setText("");
            } else {
                Toast.makeText(this, "Error al guardar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
