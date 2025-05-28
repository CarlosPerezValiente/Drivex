package com.carlosperez.drivex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nombreUsuario = getIntent().getStringExtra("nombreUsuario");
        if (nombreUsuario != null) {
            Toast.makeText(this, "Hola, " + nombreUsuario, Toast.LENGTH_LONG).show();
        }
    }

}
