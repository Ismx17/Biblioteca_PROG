package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Usuarios {
    private List <Usuario> usuarios;

    public Usuarios() {
        usuarios = new ArrayList<>();
    }

    public void alta(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        for (Usuario u : usuarios) {
            if (u.getDni().equals(usuario.getDni())) {
                throw new IllegalArgumentException("ERROR: Ya existe un usuario con ese DNI.");
            }
        }
        usuarios.add(new Usuario(usuario));
    }

    public boolean baja(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getDni().equals(usuario.getDni())) {
                usuarios.remove(i);
                return true;
            }
        }
        return false;
    }

    public Usuario buscar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        for (Usuario u : usuarios) {
            if (u.getDni().equals(usuario.getDni())) {
                return u;
            }
        }
        return null;
    }

    public List <Usuario> todos() {
        List <Usuario> copiaUsuarios = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            if (usuario != null) {
                copiaUsuarios.add(new Usuario(usuario));
            }
        }
        copiaUsuarios.sort(Comparator.comparing(Usuario::getNombre));
        return copiaUsuarios;
    }
}