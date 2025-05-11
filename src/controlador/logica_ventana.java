package controlador;

import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import vista.ventana;
import modelo.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {
    private ventana vista;
    private String nombres, email, telefono, categoria = "";
    private List<persona> contactos;
    private boolean favorito = false;

    // ExecutorService para manejar los hilos en segundo plano
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3); // Crear un pool de hilos con 3 hilos disponibles

    // Crear un objeto lock para la sincronización del acceso al contacto
    private final Object lock = new Object();

    public logica_ventana(ventana vista) {
        this.vista = vista;
        cargarContactos();

        // Asignar listeners
        vista.btn_add.addActionListener(this);
        vista.btn_eliminar.addActionListener(this);
        vista.btn_modificar.addActionListener(this);
        vista.cmb_categoria.addItemListener(this);
        vista.chb_favorito.addItemListener(this);
        vista.btn_exportar.addActionListener(e -> exportarCSV());

        // Filtro en tiempo real para la búsqueda de contactos
        vista.txt_buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscarContactos(); }
            public void removeUpdate(DocumentEvent e) { buscarContactos(); }
            public void changedUpdate(DocumentEvent e) { buscarContactos(); }
        });

        // Ctrl + S para agregar contacto
        vista.contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("control S"), "agregar");
        vista.contentPane.getActionMap().put("agregar", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                vista.btn_add.doClick();
            }
        });

        // Menú contextual para eliminar
        agregarMenuContextual();
    }

    private void cargarContactos() {
        // Cargar los contactos del archivo
        personaDAO dao = new personaDAO(new persona());
        contactos = dao.leerArchivo();
        actualizarTabla(contactos);
        actualizarEstadisticas(); // Aseguramos que las estadísticas se actualicen después de cargar los contactos
    }

    private void actualizarTabla(List<persona> lista) {
        DefaultTableModel modelo = vista.modeloTabla;
        modelo.setRowCount(0); // Limpiar la tabla antes de agregar los nuevos datos
        for (persona p : lista) {
            modelo.addRow(new Object[]{
                    p.getNombre(), p.getTelefono(), p.getEmail(), p.getCategoria(),
                    p.isFavorito() ? "Sí" : "No"
            });
        }
    }

    private void actualizarEstadisticas() {
        // Actualizamos las estadísticas en el hilo de eventos de Swing para evitar problemas de concurrencia
        SwingUtilities.invokeLater(() -> {
            // Recalcular y actualizar las estadísticas
            vista.lbl_totalContactos.setText("Total de contactos: " + contactos.size());
            vista.lbl_favoritos.setText("Favoritos: " + contactos.stream().filter(persona::isFavorito).count());
            vista.lbl_familia.setText("Familia: " + contactos.stream().filter(p -> p.getCategoria().equals("Familia")).count());
            vista.lbl_amigos.setText("Amigos: " + contactos.stream().filter(p -> p.getCategoria().equals("Amigos")).count());
            vista.lbl_trabajo.setText("Trabajo: " + contactos.stream().filter(p -> p.getCategoria().equals("Trabajo")).count());
        });
    }

    private void limpiarCampos() {
        // Limpiar los campos de la interfaz gráfica
        vista.txt_nombres.setText("");
        vista.txt_telefono.setText("");
        vista.txt_email.setText("");
        vista.cmb_categoria.setSelectedIndex(0);
        vista.chb_favorito.setSelected(false);
        categoria = "";
        favorito = false;
        cargarContactos();
    }

    private void inicializarCampos() {
        // Inicializar las variables con los datos ingresados en los campos
        nombres = vista.txt_nombres.getText();
        telefono = vista.txt_telefono.getText();
        email = vista.txt_email.getText();
    }

    private void agregarMenuContextual() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        menu.add(itemEliminar);
        itemEliminar.addActionListener(e -> eliminarSeleccionado());

        vista.tbl_contactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { mostrarMenuSiNecesario(e); }
            @Override
            public void mouseReleased(MouseEvent e) { mostrarMenuSiNecesario(e); }

            private void mostrarMenuSiNecesario(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int fila = vista.tbl_contactos.rowAtPoint(e.getPoint());
                    if (fila != -1) {
                        vista.tbl_contactos.setRowSelectionInterval(fila, fila);
                        menu.show(vista.tbl_contactos, e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void buscarContactos() {
        String textoBusqueda = vista.txt_buscar.getText().toLowerCase(); // Convertir a minúsculas para comparación no sensible a mayúsculas

        // Usamos un hilo del pool para realizar la búsqueda en segundo plano
        executorService.submit(() -> {
            // Filtrar los contactos que coincidan con cualquier campo (nombre, teléfono, email, categoría, favorito)
            List<persona> resultados = contactos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(textoBusqueda) || 
                             p.getTelefono().toLowerCase().contains(textoBusqueda) || 
                             p.getEmail().toLowerCase().contains(textoBusqueda) || 
                             p.getCategoria().toLowerCase().contains(textoBusqueda) || 
                             (p.isFavorito() && textoBusqueda.contains("sí")) || // Para filtrar por "favorito"
                             (!p.isFavorito() && textoBusqueda.contains("no"))) // Para filtrar por "no favorito"
                .collect(Collectors.toList());

            // Actualizamos la UI en el hilo principal para mostrar los resultados
            SwingUtilities.invokeLater(() -> actualizarTabla(resultados));
        });
    }

    private void exportarCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar contactos como CSV");
        chooser.setSelectedFile(new java.io.File(System.getProperty("user.home") + "/Downloads/contactos.csv"));

        // Verificamos si el usuario ha aprobado la exportación
        if (chooser.showSaveDialog(vista) == JFileChooser.APPROVE_OPTION) {
            // Ejecutamos el proceso en segundo plano
            executorService.submit(() -> {
                // Sincronizamos el acceso al archivo para evitar problemas de concurrencia
                synchronized (this) {
                    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chooser.getSelectedFile()), "UTF-8"))) {
                        writer.write("NOMBRE;TELEFONO;EMAIL;CATEGORIA;FAVORITO\n");
                        for (int i = 0; i < contactos.size(); i++) {
                            persona p = contactos.get(i);

                            // Aseguramos que el teléfono se exporte en su formato correcto
                            String telefono = p.getTelefono();
                            if (telefono != null && !telefono.isEmpty()) {
                                telefono = telefono.replaceFirst("^0+", ""); // Remover ceros iniciales solo si los tiene
                            }

                            // Escribir los datos del contacto en el archivo CSV
                            writer.write(String.join(";", p.getNombre(), telefono, p.getEmail(), p.getCategoria(), String.valueOf(p.isFavorito())) + "\n");

                            // Mostrar en consola el progreso del hilo para cada contacto
                            System.out.println("Hilo de exportación - Contacto " + (i + 1) + " exportado.");
                        }

                        // Notificación de éxito
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(vista, "Exportación completada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        });
                    } catch (IOException ex) {
                        // Manejo de errores de escritura
                        System.err.println("Error al exportar el archivo: " + ex.getMessage());
                    }
                }
            });
        }
    }



    private void eliminarSeleccionado() {
        int fila = vista.tbl_contactos.getSelectedRow();
        if (fila != -1) {
            contactos.remove(fila);
            new personaDAO().actualizarContactos(contactos);
            limpiarCampos();
            JOptionPane.showMessageDialog(vista, "Contacto eliminado correctamente.");
            // Actualizar las estadísticas en el hilo de eventos de Swing
            SwingUtilities.invokeLater(() -> actualizarEstadisticas());
        }
    }

    // Método para validar y registrar el contacto en segundo plano
    private void validarYRegistrarContacto() {
        executorService.submit(() -> {
            GestorContactos gestor = new GestorContactos();
            List<persona> contactosExistentes = gestor.obtenerContactos(); // Obtener la lista de contactos

            // Verificar si ya existe un contacto con el mismo teléfono o email
            boolean existe = contactosExistentes.stream()
                .anyMatch(p -> p.getEmail().equals(email) || p.getTelefono().equals(telefono));

            // Actualizamos la UI en el hilo principal para mostrar la notificación
            SwingUtilities.invokeLater(() -> {
                if (existe) {
                    JOptionPane.showMessageDialog(vista, "El contacto ya está registrado.");
                } else {
                    registrarNuevoContacto();
                }
            });
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        inicializarCampos();

        if (e.getSource() == vista.btn_add) {
            if (validarCampos()) {
                if (validarCategoria()) {
                    // Llamamos a validar y registrar el contacto en segundo plano
                    validarYRegistrarContacto();
                } else {
                    JOptionPane.showMessageDialog(vista, "Elija una categoría válida.");
                }
            } else {
                JOptionPane.showMessageDialog(vista, "Todos los campos deben estar llenos.");
            }
        }

        if (e.getSource() == vista.btn_modificar) {
            modificarContacto();
        }

        if (e.getSource() == vista.btn_eliminar) {
            eliminarSeleccionado();
        }
    }

    private boolean validarCampos() {
        return !nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty();
    }

    private boolean validarCategoria() {
        return !categoria.equals("Elija una Categoria") && !categoria.isEmpty();
    }

    private void registrarNuevoContacto() {
        vista.barraProgreso.setValue(0);
        Timer timer = new Timer(10, null);
        timer.addActionListener(new ActionListener() {
            int progreso = 0;
            public void actionPerformed(ActionEvent e) {
                progreso++;
                vista.barraProgreso.setValue(progreso);
                if (progreso >= 100) {
                    timer.stop();
                    persona nuevo = new persona(nombres, telefono, email, categoria, favorito);
                    new personaDAO(nuevo).escribirArchivo();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(vista, "Contacto registrado.");
                    vista.barraProgreso.setValue(0);
                    // Actualizar las estadísticas en el hilo de eventos de Swing
                    SwingUtilities.invokeLater(() -> actualizarEstadisticas());
                }
            }
        });
        timer.start();
    }

    private void modificarContacto() {
        int fila = vista.tbl_contactos.getSelectedRow();
        if (fila != -1 && validarCampos() && validarCategoria()) {
            persona actual = contactos.get(fila);
            boolean hayCambios = !actual.getNombre().equals(nombres) ||
                                 !actual.getTelefono().equals(telefono) ||
                                 !actual.getEmail().equals(email) ||
                                 !actual.getCategoria().equals(categoria) ||
                                 actual.isFavorito() != favorito;

            if (!hayCambios) {
                JOptionPane.showMessageDialog(vista, "Debes hacer algún cambio.");
                return;
            }

            persona actualizado = new persona(nombres, telefono, email, categoria, favorito);
            contactos.set(fila, actualizado);
            new personaDAO().actualizarContactos(contactos);
            limpiarCampos();
            JOptionPane.showMessageDialog(vista, "Contacto modificado.");
            // Actualizar las estadísticas en el hilo de eventos de Swing
            SwingUtilities.invokeLater(() -> actualizarEstadisticas());
        } else {
            JOptionPane.showMessageDialog(vista, "Selecciona un contacto y completa todos los campos.");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == vista.cmb_categoria) {
            categoria = vista.cmb_categoria.getSelectedItem().toString();
        } else if (e.getSource() == vista.chb_favorito) {
            favorito = vista.chb_favorito.isSelected();
        }
    }

    @Override public void valueChanged(ListSelectionEvent e) { /* No usado */ }
}
