package com.carlosperez.drivex.model;

public class Alumno {
    private int id;
    private String nombre;
    private String apellidos;
    private String dni;
    private int idUsuario; // 🔹 Nuevo campo

    public Alumno() {
    }

    public Alumno(String nombre, String apellidos, String dni, int idUsuario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
