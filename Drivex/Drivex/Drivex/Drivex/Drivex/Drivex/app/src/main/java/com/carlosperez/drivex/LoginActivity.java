package com.carlosperez.drivex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.UsuarioDAO;
import com.carlosperez.drivex.model.Usuario;

// Pantalla de inicio de sesión (login)
public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etContrasenaLogin;
    private Button btnLogin, btnIrRegistro;
    private UsuarioDAO usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Inicio de sesión de Drivex");

        // Enlazar los elementos de la interfaz
        etEmailLogin = findViewById(R.id.etEmailLogin);
        etContrasenaLogin = findViewById(R.id.etContrasenaLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        usuarioDao = new UsuarioDAO(this);

        // Comprobar si ya hay sesión guardada en SharedPreferences (login automático)
        SharedPreferences prefs = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
        int idUsuarioGuardado = prefs.getInt("idUsuario", -1);
        if (idUsuarioGuardado != -1) {
            // Si ya hay sesión, saltamos directamente al dashboard
            String nombreUsuarioGuardado = prefs.getString("nombreUsuario", "");
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra("nombreUsuario", nombreUsuarioGuardado);
            intent.putExtra("idUsuario", idUsuarioGuardado);
            startActivity(intent);
            finish();
            return;
        }

        // Cuando se pulsa el botón de login
        btnLogin.setOnClickListener(v -> {
            String email = etEmailLogin.getText().toString().trim();
            String contrasena = etContrasenaLogin.getText().toString().trim();

            // Validación de campos vacíos
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // Comprobamos el usuario en la base de datos
            Usuario usuario = usuarioDao.obtenerUsuario(email, contrasena);

            if (usuario != null) {
                Toast.makeText(this, "¡Bienvenido " + usuario.getNombre() + "!", Toast.LENGTH_SHORT).show();

                // Guardar los datos de sesión en SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("idUsuario", usuario.getId());
                editor.putString("nombreUsuario", usuario.getNombre());
                editor.apply();

                // Redirigimos al Dashboard enviando también el ID y nombre
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("nombreUsuario", usuario.getNombre());
                intent.putExtra("idUsuario", usuario.getId());
                startActivity(intent);
                finish();
            } else {
                // Si el login falla, muestra error
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // Cuando se pulsa el botón de ir a registro de usuario nuevo
        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }
}
