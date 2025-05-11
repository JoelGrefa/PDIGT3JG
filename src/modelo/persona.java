package modelo;

public class persona {
	
	private String nombre, telefono, email, categoria;
	private boolean favorito;

	// Constructor por defecto
	public persona() {
		this.nombre = "";
		this.telefono = "";
		this.email = "";
		this.categoria = "otro";
		this.favorito = false;
	}

	// Constructor con parámetros
	public persona(String nombre, String telefono, String email, String categoria, boolean favorito) {
		this.nombre = nombre != null ? nombre : "";
		this.telefono = telefono != null ? telefono : "";
		this.email = (email != null && email.contains("@")) ? email : "";
		this.categoria = (categoria != null && !categoria.isEmpty()) ? categoria.toLowerCase() : "otro";
		this.favorito = favorito;
	}

	// Getters y Setters
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre != null ? nombre : "";
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono != null ? telefono : "";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = (email != null && email.contains("@")) ? email : "";
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = (categoria != null && !categoria.isEmpty()) ? categoria.toLowerCase() : "otro";
	}

	public boolean isFavorito() {
		return favorito;
	}

	public void setFavorito(boolean favorito) {
		this.favorito = favorito;
	}

	// Formato para guardar en archivo
	public String datosContacto() {
		return String.format("%s;%s;%s;%s;%s", nombre, telefono, email, categoria, favorito);
	}

	// Formato para mostrar en lista
	public String formatoLista() {
		return String.format("%-30s%-20s%-30s%-15s", nombre, telefono, email, categoria);
	}

	// toString para depuración rápida
	@Override
	public String toString() {
		return String.format("Nombre: %s | Teléfono: %s | Email: %s | Categoría: %s | Favorito: %s",
				nombre, telefono, email, categoria, favorito ? "Sí" : "No");
	}
}

