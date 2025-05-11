package controlador;

import java.util.ResourceBundle;
import java.util.Locale;
import vista.ventana;

public class IdiomaControlador {

    // Cambia el campo `bundle` a público para que sea accesible
    public ResourceBundle bundle; 
    private ventana vista;

    // Constructor de la clase que recibe la ventana
    public IdiomaControlador(ventana vista) {
        this.vista = vista;
        loadLanguage("es"); // Cargar el idioma por defecto (español)
    }

    // Método para cargar el idioma según la selección
    public void loadLanguage(String language) {
        // Cargar el ResourceBundle correspondiente según el idioma
    	if (language.equals("es")) {
    	    bundle = ResourceBundle.getBundle("idioma/messages", Locale.forLanguageTag("es-ES"));
    	} else if (language.equals("en")) {
    	    bundle = ResourceBundle.getBundle("idioma/messages", Locale.forLanguageTag("en-US"));
    	} else if (language.equals("pt")) {
    	    bundle = ResourceBundle.getBundle("idioma/messages", Locale.forLanguageTag("pt-BR"));
    	}

        setLanguage(); // Actualiza la interfaz con el idioma seleccionado
    }

    // Método para actualizar los textos de la interfaz
    public void setLanguage() {
        // Actualizamos todos los textos de la ventana con el idioma cargado
    	vista.btn_add.setText(bundle.getString("btn_add"));
        vista.btn_modificar.setText(bundle.getString("btn_modificar"));
        vista.btn_eliminar.setText(bundle.getString("btn_eliminar"));
        vista.btn_exportar.setText(bundle.getString("btn_exportar"));
        vista.lbl_totalContactos.setText(bundle.getString("lbl_totalContactos"));
        vista.lbl_favoritos.setText(bundle.getString("lbl_favoritos"));
        vista.lbl_familia.setText(bundle.getString("lbl_familia"));
        vista.lbl_amigos.setText(bundle.getString("lbl_amigos"));
        vista.lbl_trabajo.setText(bundle.getString("lbl_trabajo"));
        vista.lblBuscar.setText(bundle.getString("lblBuscar"));
        vista.chb_favorito.setText(bundle.getString("chb_favorito"));
        vista.lblIdioma.setText(bundle.getString("lblIdioma"));
        vista.lblModo.setText(bundle.getString("lblModo"));
        vista.rbtnClaro.setText(bundle.getString("rbtnClaro"));
        vista.rbtnOscuro.setText(bundle.getString("rbtnOscuro"));
    }
    

    // Método para manejar el cambio de idioma cuando se selecciona un idioma del JComboBox
    public void cambiarIdioma(String selectedLanguage) {
        if (selectedLanguage.equals("Español")) {
            loadLanguage("es");
        } else if (selectedLanguage.equals("Inglés")) {
            loadLanguage("en");
        } else if (selectedLanguage.equals("Portugués")) {
            loadLanguage("pt");
        }
    }
}


