package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Usuarios {
    private List <Usuario> usuarios;

    public Usuarios() {
        // Creo la lista de usuarios
        this.usuarios = new ArrayList<>();
    }

    public void alta(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Valido que no existe un usuario con el mismo DNI
        if (usuarios.contains(usuario)) {
            throw new IllegalArgumentException("ERROR: Ya existe un usuario con ese DNI.");
        }
        // Agrego el usuario a la lista
        usuarios.add(new Usuario(usuario));
    }

    public boolean baja(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Elimino el usuario usando el método remove de ArrayList
        return usuarios.remove(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Busco el índice del usuario
        int index = usuarios.indexOf(usuario);
        // Si existe, lo devuelvo
        if (index != -1) {
            return usuarios.get(index);
        }
        return null;
    }

    public List <Usuario> todos() {
        // Creo una copia de la lista de usuarios para no modificar la original
        List <Usuario> copiaUsuarios = new ArrayList<>();
        // Recorro la lista de usuarios y añado el usuario a la copia
        for (Usuario usuario : usuarios) {
            // Valido que el usuario existe
            if (usuario != null) {
                // Añado el usuario a la copia
                copiaUsuarios.add(new Usuario(usuario));
            }
        }
        Collections.sort(copiaUsuarios);
        // Devuelvo la copia de la lista de usuarios
        return copiaUsuarios;
    }
}