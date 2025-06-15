package com.carlosperez.drivex.model;

// Modelo de datos para representar un horario o clase de un alumno
public class Horario {
    private int id;  // ID único del horario (clave primaria en la base de datos)
    private int idAlumno;  // ID del alumno al que pertenece esta clase (clave foránea)
    private String fecha;  // Fecha de la clase en formato YYYY-MM-DD
    private String horaInicio;  // Hora de inicio de la clase
    private String horaFin;  // Hora de fin de la clase
    private String descripcion;  // Descripción opcional de la clase

    private String nombreAlumno;
    private String dniAlumno;

    // Getter y Setter de descripción
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getDniAlumno() {
        return dniAlumno;
    }

    public void setDniAlumno(String dniAlumno) {
        this.dniAlumno = dniAlumno;
    }
    // Constructor vacío (necesario para operaciones de lectura desde la base de datos)
    public Horario() {
    }

    // Constructor con parámetros (para crear nuevos horarios)
    public Horario(int idAlumno, String fecha, String horaInicio, String horaFin) {
        this.idAlumno = idAlumno;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    // Getters y Setters del resto de atributos

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getIdAlumno() {
        return idAlumno;
    }
    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }
    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }
    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }
}
