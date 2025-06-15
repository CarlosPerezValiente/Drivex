package com.carlosperez.drivex.model;

// Modelo de datos para representar a un alumno
public class Alumno {
    private int id;  // ID único del alumno (clave primaria en la base de datos)
    private String nombre;  // Nombre del alumno
    private String apellidos;  // Apellidos del alumno
    private String dni;  // DNI del alumno
    private int idUsuario; // ID del usuario al que pertenece este alumno (profesor o autoescuela)

    // Constructor vacío (necesario para algunas operaciones como la lectura desde la base de datos)
    public Alumno() {
    }

    // Constructor completo (útil para crear objetos nuevos al insertar alumnos)
    public Alumno(String nombre, String apellidos, String dni, int idUsuario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters para acceder y modificar los atributos

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
