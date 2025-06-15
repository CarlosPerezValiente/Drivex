package com.carlosperez.drivex.model;

// Modelo de datos para representar a un usuario (probablemente profesor o administrador)
public class Usuario {
    private int id;  // ID único del usuario (clave primaria en la base de datos)
    private String nombre;  // Nombre del usuario
    private String email;  // Email del usuario (sirve también para el login)
    private String contrasena;  // Contraseña del usuario

    // Constructor vacío (necesario para la lectura desde la base de datos)
    public Usuario() {
    }

    // Constructor con parámetros (para crear nuevos usuarios)
    public Usuario(String nombre, String email, String contrasena) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
    }

    // Getters y Setters de los atributos

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
