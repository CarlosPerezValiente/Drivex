package com.carlosperez.drivex.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carlosperez.drivex.database.DrivexDatabase; // ← usa la nueva base de datos
import com.carlosperez.drivex.model.Usuario;

public class UsuarioDAO {

    private DrivexDatabase dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    public boolean insertarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", usuario.getNombre());
        valores.put("email", usuario.getEmail());
        valores.put("contrasena", usuario.getContrasena());

        long id = db.insert("usuarios", null, valores);
        db.close();
        return id != -1;
    }

    public Usuario obtenerUsuario(String email, String contrasena) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columnas = {"id_usuario", "nombre", "email", "contrasena"};
        String selection = "email = ? AND contrasena = ?";
        String[] selectionArgs = {email, contrasena};

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
        return usuario;
    }
}
