package com.carlosperez.drivex.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DrivexDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "drivex.db";
    private static final int DATABASE_VERSION = 3;

    public DrivexDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla usuarios
        db.execSQL("CREATE TABLE usuarios (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "contrasena TEXT NOT NULL)");

        // Crear tabla alumnos
        db.execSQL("CREATE TABLE alumnos (" +
                "id_alumno INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "apellidos TEXT, " +
                "dni TEXT, " +
                "telefono TEXT, " +
                "email TEXT, " +
                "id_usuario INTEGER, " +
                "FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE)");

        // Crear tabla clases
        db.execSQL("CREATE TABLE clases (" +
                "id_clase INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_alumno INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +       // Guardar fechas como TEXT (yyyy-MM-dd)
                "hora TEXT NOT NULL, " +        // Guardar hora como TEXT (HH:mm:ss)
                "descripcion TEXT, " +
                "FOREIGN KEY(id_alumno) REFERENCES alumnos(id_alumno) ON DELETE CASCADE)");

        // Crear tabla firmas
        db.execSQL("CREATE TABLE firmas (" +
                "id_firma INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_clase INTEGER NOT NULL, " +
                "firma BLOB NOT NULL, " +
                "fecha_firma TEXT DEFAULT (datetime('now','localtime')), " +
                "FOREIGN KEY(id_clase) REFERENCES clases(id_clase) ON DELETE CASCADE)");

        // Crear tabla horarios
        db.execSQL("CREATE TABLE horarios (" +
                "id_horario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_alumno INTEGER NOT NULL, " +
                "fecha TEXT NOT NULL, " +          // NUEVO: fecha real (YYYY-MM-DD)
                "hora_inicio TEXT NOT NULL, " +
                "hora_fin TEXT NOT NULL, " +
                "FOREIGN KEY(id_alumno) REFERENCES alumnos(id_alumno) ON DELETE CASCADE)");
    }

        @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Por ahora eliminar y crear de nuevo (puedes hacer migraciones cuando la DB cambie)
        db.execSQL("DROP TABLE IF EXISTS firmas");
        db.execSQL("DROP TABLE IF EXISTS clases");
        db.execSQL("DROP TABLE IF EXISTS horarios");
        db.execSQL("DROP TABLE IF EXISTS alumnos");
        db.execSQL("DROP TABLE IF EXISTS usuarios");

        onCreate(db);
    }
}
