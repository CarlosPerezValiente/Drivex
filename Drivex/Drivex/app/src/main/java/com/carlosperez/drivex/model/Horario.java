package com.carlosperez.drivex.model;

public class Horario {
    private int id;
    private int idAlumno;
    private String fecha;         // YYYY-MM-DD
    private String horaInicio;
    private String horaFin;

    public Horario() {
    }

    public Horario(int idAlumno, String fecha, String horaInicio, String horaFin) {
        this.idAlumno = idAlumno;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdAlumno() { return idAlumno; }
    public void setIdAlumno(int idAlumno) { this.idAlumno = idAlumno; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
}
