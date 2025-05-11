package modelo;

import java.util.List;

public class GestorContactos {

    private personaDAO dao;

    public GestorContactos() {
        dao = new personaDAO();
    }

    // Agregar un nuevo contacto
    public boolean agregarContacto(persona p) {
        dao = new personaDAO(p); // Asignar un nuevo contacto al DAO
        return dao.escribirArchivo();
    }

    // Obtener todos los contactos
    public List<persona> obtenerContactos() {
        return dao.leerArchivo();
    }

    // Buscar contactos por nombre
    public List<persona> buscarPorNombre(String nombre) {
        List<persona> contactos = obtenerContactos();
        if (contactos != null) {
            contactos.removeIf(p -> !p.getNombre().contains(nombre));
        }
        return contactos;
    }

    // Actualizar los contactos en el archivo
    public void actualizarContactos(List<persona> personas) {
        dao.actualizarContactos(personas);
    }
}

