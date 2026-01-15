package org.iesalandalus.programacion.biblioteca.modelo.negocio;

import org.iesalandalus.programacion.biblioteca.modelo.dominio.Usuario;

public class Usuarios {
    private Usuario[] usuarios;

    public Usuarios(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("ERROR: La capacidad debe ser mayor que cero.");
        }
        this.usuarios = new Usuario[capacidad];
    }

    public void alta(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        int indice = -1;
        for (int i = 0; i < usuarios.length; i++) {
            if (usuarios[i] != null) {
                if (usuarios[i].getDni().equals(usuario.getDni())) {
                    throw new IllegalArgumentException("ERROR: Ya existe un usuario con ese ID.");
                }
            } else if (indice == -1) {
                indice = i;
            }
        }
        if (indice == -1) {
            throw new IllegalArgumentException("ERROR: No hay espacio para más usuarios.");
        }
        usuarios[indice] = new Usuario(usuario);
    }

    public boolean baja(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        for (int i = 0; i < usuarios.length; i++) {
            if (usuarios[i] != null && usuarios[i].getDni().equals(usuario.getDni())) {
                usuarios[i] = null;
                return true;
            }
        }
        return false;
    }

    public Usuario buscar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("ERROR: El usuario no puede ser nulo.");
        }
        for (int i = 0; i < usuarios.length; i++) {
            if (usuarios[i] != null && usuarios[i].getDni().equals(usuario.getDni())) {
                return new Usuario(usuarios[i]);
            }
        }
        return null;
    }

    public Usuario[] todos() {
        int contador = 0;
        for (Usuario usuario : usuarios) {
            if (usuario != null) {
                contador++;
            }
        }
        Usuario[] copiaUsuarios = new Usuario[contador];
        int j = 0;
        for (Usuario usuario : usuarios) {
            if (usuario != null) {
                copiaUsuarios[j++] = new Usuario(usuario);
            }
        }
        return copiaUsuarios;
    }
}