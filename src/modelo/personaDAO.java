package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {

	private static final String DIRECTORIO = "c:/gestionContactos";
	private static final String ARCHIVO_NOMBRE = "datosContactos.csv";
	private static final String ENCABEZADO = "NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO";

	private File archivo;
	private persona persona;

	public personaDAO() {
		prepararArchivo();
	}

	public personaDAO(persona persona) {
		this.persona = persona;
		prepararArchivo();
	}

	// Crear directorio y archivo si no existen
	private void prepararArchivo() {
		File directorio = new File(DIRECTORIO);
		if (!directorio.exists()) {
			directorio.mkdirs();
		}

		archivo = new File(directorio, ARCHIVO_NOMBRE);
 
		if (!archivo.exists()) {
			try {
				archivo.createNewFile();
				escribirLinea(ENCABEZADO, false);
			} catch (IOException e) {
				System.err.println("Error al crear archivo: " + e.getMessage());
			}
		}
	}

	// Escribir línea al archivo
	private void escribirLinea(String texto, boolean append) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, append))) {
			bw.write(texto);
			bw.newLine();
		} catch (IOException e) {
			System.err.println("Error al escribir línea: " + e.getMessage());
		}
	}

	// Escribe la persona actual en el archivo
	public boolean escribirArchivo() {
		if (persona != null) {
			escribirLinea(persona.datosContacto(), true);
			return true;
		}
		return false;
	}

	// Leer el archivo y obtener la lista de personas
	public List<persona> leerArchivo() {
		List<persona> personas = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
			String linea;
			boolean esEncabezado = true;

			while ((linea = br.readLine()) != null) {
				if (esEncabezado) {
					esEncabezado = false;
					continue;
				}
				String[] datos = linea.split(";");
				if (datos.length == 5) {
					persona p = new persona(
						datos[0],
						datos[1],
						datos[2],
						datos[3],
						Boolean.parseBoolean(datos[4])
					);
					personas.add(p);
				}
			}
		} catch (IOException e) {
			System.err.println("Error al leer el archivo: " + e.getMessage());
		}

		return personas;
	}

	// Actualiza todo el archivo con una nueva lista de personas
	public void actualizarContactos(List<persona> personas) {
		try {
			if (archivo.exists()) {
				archivo.delete();
			}
			archivo.createNewFile();
			escribirLinea(ENCABEZADO, false);

			for (persona p : personas) {
				escribirLinea(p.datosContacto(), true);
			}
		} catch (IOException e) {
			System.err.println("Error al actualizar contactos: " + e.getMessage());
		}
	}
}

