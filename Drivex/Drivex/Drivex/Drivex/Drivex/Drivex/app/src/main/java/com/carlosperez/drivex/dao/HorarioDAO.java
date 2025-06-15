package com.carlosperez.drivex.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carlosperez.drivex.database.DrivexDatabase;
import com.carlosperez.drivex.model.Horario;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorarioDAO {
    // Helper para acceder a la base de datos
    private DrivexDatabase dbHelper;

    // Constructor: inicializa el helper de la BD
    public HorarioDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    // Inserta un nuevo horario en la tabla 'horarios'
    public boolean insertarHorario(Horario horario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // Rellenamos los valores que vamos a insertar
        values.put("id_alumno", horario.getIdAlumno());
        values.put("fecha", horario.getFecha());  // fecha de la clase
        values.put("hora_inicio", horario.getHoraInicio());
        values.put("hora_fin", horario.getHoraFin());
        values.put("descripcion", horario.getDescripcion());

        long id = db.insert("horarios", null, values);
        db.close();
        return id != -1;  // Devuelve true si la inserción fue exitosa
    }

    // Devuelve la lista de horarios de un alumno concreto
    public List<Horario> obtenerHorariosPorAlumno(int idAlumno) {
        List<Horario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta todos los horarios de ese alumno, ordenados por fecha y hora de inicio
        Cursor cursor = db.rawQuery("SELECT * FROM horarios WHERE id_alumno = ? ORDER BY fecha, hora_inicio", new String[]{String.valueOf(idAlumno)});

        if (cursor.moveToFirst()) {
            do {
                Horario h = new Horario();
                h.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_horario")));
                h.setIdAlumno(idAlumno);
                h.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                h.setHoraInicio(cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")));
                h.setHoraFin(cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")));
                h.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));

                lista.add(h);  // Añadimos el horario a la lista
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Devuelve la agenda (horarios) de un usuario en una fecha concreta
    public List<Horario> obtenerAgendaPorFecha(String fecha, int idUsuario) {
        List<Horario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta los horarios de los alumnos de un usuario en una fecha concreta
        String query = "SELECT h.id_horario, h.id_alumno, h.fecha, h.hora_inicio, h.hora_fin, h.descripcion, a.nombre, a.apellidos " +
                "FROM horarios h " +
                "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                "WHERE a.id_usuario = ? AND h.fecha = ? " +
                "ORDER BY h.hora_inicio ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario), fecha});

        if (cursor.moveToFirst()) {
            do {
                Horario h = new Horario();
                h.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id_horario")));
                h.setIdAlumno(cursor.getInt(cursor.getColumnIndexOrThrow("id_alumno")));
                h.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
                h.setHoraInicio(cursor.getString(cursor.getColumnIndexOrThrow("hora_inicio")));
                h.setHoraFin(cursor.getString(cursor.getColumnIndexOrThrow("hora_fin")));
                h.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));

                // Recuperamos el nombre y apellidos del alumno
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));

                // Modificamos la descripción para incluir el nombre del alumno (truco temporal)
                h.setDescripcion("Alumno: " + nombre + " " + apellidos + "\nDescripción: " + (h.getDescripcion() == null ? "-" : h.getDescripcion()));

                lista.add(h);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Elimina un horario por su ID
    public boolean eliminarHorario(int idHorario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("horarios", "id_horario = ?", new String[]{String.valueOf(idHorario)});
        db.close();
        return filasAfectadas > 0;  // Devuelve true si eliminó algún registro
    }

    // ---------- MÉTODOS DE ESTADÍSTICAS -----------

    // Devuelve el número total de clases de un usuario (todas)
    public int obtenerTotalClases(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM horarios h " +
                        "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                        "WHERE a.id_usuario = ?", new String[]{String.valueOf(idUsuario)});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Devuelve el número total de clases futuras (a partir de hoy)
    public int obtenerTotalClasesFuturas(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM horarios h " +
                        "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                        "WHERE a.id_usuario = ? AND date(h.fecha) >= date('now')",
                new String[]{String.valueOf(idUsuario)});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Devuelve la próxima clase programada de un usuario
    public Horario obtenerProximaClase(int idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT h.id_horario, h.fecha, h.hora_inicio, h.hora_fin, h.descripcion, " +
                        "a.nombre, a.apellidos, a.dni " +
                        "FROM horarios h " +
                        "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                        "WHERE a.id_usuario = ? AND date(h.fecha) >= date('now') " +
                        "ORDER BY h.fecha ASC, h.hora_inicio ASC LIMIT 1",
                new String[]{String.valueOf(idUsuario)});

        Horario proxima = null;
        if (cursor.moveToFirst()) {
            proxima = new Horario();
            proxima.setId(cursor.getInt(0));
            proxima.setFecha(cursor.getString(1));
            proxima.setHoraInicio(cursor.getString(2));
            proxima.setHoraFin(cursor.getString(3));
            // Formatea la descripción añadiendo el alumno y su DNI
            proxima.setDescripcion(cursor.getString(4) + " | " + cursor.getString(5) + " " + cursor.getString(6) + " (" + cursor.getString(7) + ")");
        }
        cursor.close();
        db.close();
        return proxima;
    }
}
