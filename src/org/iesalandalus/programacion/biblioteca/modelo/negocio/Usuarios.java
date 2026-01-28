package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Usuarios {
    private List <Usuario> usuarios;

    public Usuarios() {
        // Creo la lista de usuarios
        usuarios = new ArrayList<>();
    }

    public void alta(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Valido que no existe un usuario con el mismo DNI
        for (Usuario u : usuarios) {
            if (u.getDni().equals(usuario.getDni())) {
                throw new IllegalArgumentException("ERROR: Ya existe un usuario con ese DNI.");
            }
        }
        // Agrego el usuario a la lista
        usuarios.add(new Usuario(usuario));
    }

    public boolean baja(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Recorro la lista completa de usuarios y valido que el usuario existe en la lista
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getDni().equals(usuario.getDni())) {
                // Elimino el usuario de la lista y devuelvo true
                usuarios.remove(i);
                return true;
            }
        }
        // Devuelvo false si no se ha eliminado al usuario de la lista correctamente
        return false;
    }

    public Usuario buscar(Usuario usuario) {
        // Valido que el usuario existe
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        // Recorro la lista de usuarios y devuelvo al usuario si existe en la lista
        for (Usuario u : usuarios) {
            if (u.getDni().equals(usuario.getDni())) {
                return u;
            }
        }
        // Devuelvo null si no existe el usuario en la lista
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
        // Ordeno la lista por nombre 
        copiaUsuarios.sort(Comparator.comparing(Usuario::getNombre));
        // Devuelvo la copia de la lista de usuarios
        return copiaUsuarios;
    }
}