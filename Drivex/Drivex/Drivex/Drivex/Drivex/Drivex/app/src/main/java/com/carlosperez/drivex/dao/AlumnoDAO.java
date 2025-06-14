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
    private DrivexDatabase dbHelper;

    public AlumnoDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    // Insertar un alumno asociado a un usuario
    public boolean insertarAlumno(Alumno alumno) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", alumno.getNombre());
        values.put("apellidos", alumno.getApellidos());
        values.put("dni", alumno.getDni());
        values.put("id_usuario", alumno.getIdUsuario());  // Asociar al usuario
        long id = db.insert("alumnos", null, values);
        db.close();
        return id != -1;
    }

    // AlumnoDAO.java

    public boolean eliminarAlumno(int idAlumno) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("alumnos", "id_alumno = ?", new String[]{String.valueOf(idAlumno)});
        db.close();
        return filasAfectadas > 0;
    }

    // Obtener alumnos de un usuario específico
    public List<Alumno> obtenerPorUsuario(int idUsuario) {
        List<Alumno> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM alumnos WHERE id_usuario = ?", new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                Alumno alumno = new Alumno();
                alumno.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_alumno")));
                alumno.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                alumno.setApellidos(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")));
                alumno.setDni(cursor.getString(cursor.getColumnIndexOrThrow("dni")));
                alumno.setIdUsuario(idUsuario);
                lista.add(alumno);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }


    public List<String> obtenerTodosPorUsuario(int idUsuario) {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, apellidos, dni FROM alumnos WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                String alumno = cursor.getString(0) + " " + cursor.getString(1) + " (DNI: " + cursor.getString(2) + ")";
                lista.add(alumno);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }




}
