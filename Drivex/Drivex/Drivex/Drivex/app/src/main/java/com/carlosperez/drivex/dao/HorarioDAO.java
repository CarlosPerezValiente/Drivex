package com.carlosperez.drivex.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.carlosperez.drivex.database.DrivexDatabase;
import com.carlosperez.drivex.model.Horario;

import java.util.ArrayList;
import java.util.List;

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
    public List<String> obtenerAgendaCompleta(int idUsuario) {
        List<String> agenda = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT h.fecha, h.hora_inicio, h.hora_fin, a.nombre, a.apellidos " +
                "FROM horarios h " +
                "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                "WHERE a.id_usuario = ? " +
                "ORDER BY h.fecha ASC, h.hora_inicio ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario)});

        if (cursor.moveToFirst()) {
            do {
                String fecha = cursor.getString(0);
                String inicio = cursor.getString(1);
                String fin = cursor.getString(2);
                String nombre = cursor.getString(3);
                String apellidos = cursor.getString(4);
                agenda.add("📅 " + fecha + " | " + inicio + " - " + fin + " → " + nombre + " " + apellidos);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return agenda;
    }

    public List<String> obtenerAgendaPorFecha(String fecha, int idUsuario) {
        List<String> agenda = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT h.hora_inicio, h.hora_fin, a.nombre, a.apellidos " +
                "FROM horarios h " +
                "JOIN alumnos a ON h.id_alumno = a.id_alumno " +
                "WHERE a.id_usuario = ? AND h.fecha = ? " +
                "ORDER BY h.hora_inicio ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idUsuario), fecha});

        if (cursor.moveToFirst()) {
            do {
                String inicio = cursor.getString(0);
                String fin = cursor.getString(1);
                String nombre = cursor.getString(2);
                String apellidos = cursor.getString(3);
                agenda.add("⏰ " + inicio + " - " + fin + " → " + nombre + " " + apellidos);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return agenda;

    }

    public boolean eliminarHorario(int idHorario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filasAfectadas = db.delete("horarios", "id_horario = ?", new String[]{String.valueOf(idHorario)});
        db.close();
        return filasAfectadas > 0;
    }



}




