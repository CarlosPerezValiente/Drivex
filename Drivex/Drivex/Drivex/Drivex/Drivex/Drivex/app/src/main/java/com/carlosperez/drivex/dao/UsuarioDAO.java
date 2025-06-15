package com.carlosperez.drivex.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carlosperez.drivex.database.DrivexDatabase; // Helper de la base de datos
import com.carlosperez.drivex.model.Usuario;

public class UsuarioDAO {

    // Objeto que gestiona la conexión con la base de datos
    private DrivexDatabase dbHelper;

    // Constructor: inicializa el helper pasándole el contexto de la app
    public UsuarioDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    // Inserta un nuevo usuario en la tabla 'usuarios'
    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();  // Modo escritura
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("email", usuario.getEmail());
        valores.put("contrasena", usuario.getContrasena());

        long id = db.insert("usuarios", null, valores);
        db.close();
        return id != -1;  // Devuelve true si la inserción ha sido correcta
    }

    // Obtiene un usuario según email y contraseña (validación de login)
    public Usuario obtenerUsuario(String email, String contrasena) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();  // Modo lectura
        String[] columnas = {"id_usuario", "nombre", "email", "contrasena"};  // Campos que queremos obtener
        String selection = "email = ? AND contrasena = ?";  // Condición de búsqueda
        String[] selectionArgs = {email, contrasena};  // Argumentos de la condición

        // Ejecuta la consulta en la tabla 'usuarios'
        Cursor cursor = db.query("usuarios", columnas, selection, selectionArgs, null, null, null);
        Usuario usuario = null;
        if (cursor != null && cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")));
            usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            usuario.setContrasena(cursor.getString(cursor.getColumnIndexOrThrow("contrasena")));
            cursor.close();
        }
        db.close();
        return usuario;  // Devuelve el objeto usuario si existe, si no devuelve null
    }
}
