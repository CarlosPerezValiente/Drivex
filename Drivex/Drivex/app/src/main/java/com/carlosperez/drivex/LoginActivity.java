package com.carlosperez.drivex;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.carlosperez.drivex.dao.UsuarioDAO;
import com.carlosperez.drivex.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmailLogin, etContrasenaLogin;
    private Button btnLogin, btnIrRegistro;
    private UsuarioDAO usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmailLogin = findViewById(R.id.etEmailLogin);
        etContrasenaLogin = findViewById(R.id.etContrasenaLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);

        usuarioDao = new UsuarioDAO(this);

        btnLogin.setOnClickListener(v -> {
            String email = etEmailLogin.getText().toString().trim();
            String contrasena = etContrasenaLogin.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena)) {
                Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = usuarioDao.obtenerUsuario(email, contrasena);

            if (usuario != null) {
                Toast.makeText(this, "¡Bienvenido " + usuario.getNombre() + "!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                intent.putExtra("nombreUsuario", usuario.getNombre());
                intent.putExtra("idUsuario", usuario.getId()); // 🔹 enviamos el ID
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });


        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroUsuarioActivity.class);
            startActivity(intent);
        });
    }
}
