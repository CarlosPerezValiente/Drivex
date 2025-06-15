package com.carlosperez.drivex.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carlosperez.drivex.database.DrivexDatabase;
import com.carlosperez.drivex.model.Alumno;

import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {
    // Objeto que gestiona la conexión a la base de datos
    private DrivexDatabase dbHelper;

    // Constructor: recibe el contexto y crea el helper de la BD
    public AlumnoDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    // Inserta un nuevo alumno en la tabla 'alumnos'
    public boolean insertarAlumno(Alumno alumno) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Rellenamos los campos a insertar
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        values.put("dni", alumno.getDni());
        values.put("id_usuario", alumno.getIdUsuario());  // Asociación al usuario
        long id = db.insert("alumnos", null, values);
        db.close();
        return id != -1;  // Devuelve true si se insertó correctamente
    }

    // Elimina un alumno por su ID
    public boolean eliminarAlumno(int idAlumno) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("alumnos", "id_alumno = ?", new String[]{String.valueOf(idAlumno)});
        db.close();
        return filasAfectadas > 0; // Devuelve true si eliminó al menos una fila
    }

    // Obtiene la lista de alumnos de un usuario concreto (devuelve objetos Alumno)
    public List<Alumno> obtenerPorUsuario(int idUsuario) {
        List<Alumno> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta los alumnos cuyo id_usuario coincide
        Cursor cursor = db.rawQuery("SELECT * FROM alumnos WHERE id_usuario = ?", new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                Alumno alumno = new Alumno();
                alumno.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_alumno")));
                alumno.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                alumno.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")));
                alumno.setDni(cursor.getString(cursor.getColumnIndexOrThrow("dni")));
                alumno.setIdUsuario(idUsuario);
                lista.add(alumno);  // Añadimos el alumno a la lista
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Obtiene los alumnos de un usuario, pero devuelve una lista de Strings (para mostrar)
    public List<String> obtenerTodosPorUsuario(int idUsuario) {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, apellidos, dni FROM alumnos WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                // Formateamos el string con nombre, apellidos y DNI
                String alumno = cursor.getString(0) + " " + cursor.getString(1) + " (DNI: " + cursor.getString(2) + ")";
                lista.add(alumno);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    // Obtiene el nombre completo (nombre + apellidos) de un alumno por su ID
    public String obtenerNombrePorId(int idAlumno) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombre = "";

        Cursor cursor = db.rawQuery("SELECT nombre, apellidos FROM alumnos WHERE id_alumno = ?", new String[]{String.valueOf(idAlumno)});
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0) + " " + cursor.getString(1);
        }
        cursor.close();
        db.close();
        return nombre;
    }
}
