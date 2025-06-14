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
    private DrivexDatabase dbHelper;

    public HorarioDAO(Context context) {
        dbHelper = new DrivexDatabase(context);
    }

    // Insertar horario con fecha
    public boolean insertarHorario(Horario horario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_alumno", horario.getIdAlumno());
        values.put("fecha", horario.getFecha());  // ← usamos fecha
        values.put("hora_inicio", horario.getHoraInicio());
        values.put("hora_fin", horario.getHoraFin());
        values.put("descripcion", horario.getDescripcion());


        long id = db.insert("horarios", null, values);
        db.close();
        return id != -1;
    }

    // Obtener horarios por alumno
    public List<Horario> obtenerHorariosPorAlumno(int idAlumno) {
        List<Horario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

                lista.add(h);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // Agenda general por usuario


    public List<Horario> obtenerAgendaPorFecha(String fecha, int idUsuario) {
        List<Horario> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

                // ⚠️ Añadimos nombre y apellidos usando campos temporales:
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"));

                // Truquito rápido: guardamos el nombre completo dentro de la descripción temporalmente
                h.setDescripcion("Alumno: " + nombre + " " + apellidos + "\nDescripción: " + (h.getDescripcion() == null ? "-" : h.getDescripcion()));

                lista.add(h);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }


    public boolean eliminarHorario(int idHorario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("horarios", "id_horario = ?", new String[]{String.valueOf(idHorario)});
        db.close();
        return filasAfectadas > 0;
    }



    // MÉTODOS NUEVOS PARA ESTADÍSTICAS EN HorarioDAO

    // Obtener total de clases (todas)
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

    // Obtener total de clases futuras (próximas)
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

    // Obtener la próxima clase
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
            proxima.setDescripcion(cursor.getString(4) + " | " + cursor.getString(5) + " " + cursor.getString(6) + " (" + cursor.getString(7) + ")");
        }
        cursor.close();
        db.close();
        return proxima;
    }






}




