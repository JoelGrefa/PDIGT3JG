package vista;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ventana extends JFrame {

    private static final long serialVersionUID = 1L;

    public JPanel contentPane;
    public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
    public JCheckBox chb_favorito;
    @SuppressWarnings("rawtypes")
    public JComboBox cmb_categoria;
    public JButton btn_add, btn_modificar, btn_eliminar, btn_exportar;
    public JTable tbl_contactos;
    public DefaultTableModel modeloTabla;
    public JLabel lbl_totalContactos, lbl_favoritos, lbl_familia, lbl_amigos, lbl_trabajo;
    public JProgressBar barraProgreso;

    // Eliminar el JLabel, JComboBox y código relacionado con la selección de fuente
    public JLabel lblIdioma, lblColor, lblBuscar;
    
    
    public JComboBox<String> cmb_idioma;

    public ventana() {
        setTitle("GESTION DE CONTACTOS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 1026, 748);

        initComponents();

        // Controlador
        new controlador.logica_ventana(this);
    }

    private void initComponents() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 1010, 710);
        contentPane.add(tabbedPane);

        // Panel Contactos
        JPanel panelContactos = new JPanel(null);
        tabbedPane.addTab("Contactos", panelContactos);

        addContactoFields(panelContactos);
        addContactoButtons(panelContactos);
        addContactoTable(panelContactos);
        addProgressBar(panelContactos);

        // Panel Estadísticas
        JPanel panelEstadisticas = new JPanel(null);
        tabbedPane.addTab("Estadísticas", panelEstadisticas);

        addEstadisticasLabels(panelEstadisticas);

        // Panel Ajustes
        JPanel panelAjustes = new JPanel(null);
        tabbedPane.addTab("Ajustes", panelAjustes);

        // Selector de Idioma
        JLabel lblIdioma = new JLabel("Seleccionar Idioma:");
        lblIdioma.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblIdioma.setBounds(25, 70, 150, 20);
        panelAjustes.add(lblIdioma);

        cmb_idioma = new JComboBox<>();
        cmb_idioma.setBounds(175, 70, 200, 30);
        panelAjustes.add(cmb_idioma);

        String[] idiomas = {"Español", "Inglés", "Portugués"};
        for (String idioma : idiomas) {
            cmb_idioma.addItem(idioma);
        }

        // Selector de Modo de Visualización (Claro/Oscuro)
        JLabel lblModo = new JLabel("Modo de visualización:");
        lblModo.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblModo.setBounds(25, 120, 200, 20);
        panelAjustes.add(lblModo);

        // Crear los botones de opción (JRadioButton) para modo Claro y Oscuro
        JRadioButton rbtnClaro = new JRadioButton("Claro");
        rbtnClaro.setBounds(175, 120, 100, 30);
        panelAjustes.add(rbtnClaro);

        JRadioButton rbtnOscuro = new JRadioButton("Oscuro");
        rbtnOscuro.setBounds(275, 120, 100, 30);
        panelAjustes.add(rbtnOscuro);

        // Agrupar los botones de opción en un ButtonGroup
        ButtonGroup grupoModo = new ButtonGroup();
        grupoModo.add(rbtnClaro);
        grupoModo.add(rbtnOscuro);

        // Acción para cambiar el modo de visualización
        rbtnClaro.addActionListener(e -> {
            contentPane.setBackground(Color.WHITE);  // Cambiar el color de fondo a blanco para el modo Claro
            panelAjustes.setBackground(Color.WHITE);
        });

        rbtnOscuro.addActionListener(e -> {
            contentPane.setBackground(Color.DARK_GRAY);  // Cambiar el color de fondo a gris oscuro para el modo Oscuro
            panelAjustes.setBackground(Color.DARK_GRAY);
        });

        // Asignar icono al botón Agregar
        try {
            ImageIcon addIcon = new ImageIcon(getClass().getResource("/icons/agregar.png"));
            if (addIcon.getIconWidth() == -1) {
                System.out.println("Icono Agregar no encontrado.");
            }
            btn_add.setIcon(addIcon);
        } catch (Exception e) {
            System.out.println("Error al cargar el icono Agregar: " + e.getMessage());
        }

        // Asignar icono al botón Modificar
        try {
            ImageIcon modIcon = new ImageIcon(getClass().getResource("/icons/modificar.png"));
            if (modIcon.getIconWidth() == -1) {
                System.out.println("Icono Modificar no encontrado.");
            }
            btn_modificar.setIcon(modIcon);
        } catch (Exception e) {
            System.out.println("Error al cargar el icono Modificar: " + e.getMessage());
        }

        // Asignar icono al botón Eliminar
        try {
            ImageIcon delIcon = new ImageIcon(getClass().getResource("/icons/eliminar.png"));
            if (delIcon.getIconWidth() == -1) {
                System.out.println("Icono Eliminar no encontrado.");
            }
            btn_eliminar.setIcon(delIcon);
        } catch (Exception e) {
            System.out.println("Error al cargar el icono Eliminar: " + e.getMessage());
        }

        // Redimensionar los iconos si es necesario
        btn_add.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icons/agregar.png")).getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
        btn_modificar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icons/modificar.png")).getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
        btn_eliminar.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/icons/eliminar.png")).getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH)));
    }
    
    

    private void addContactoFields(JPanel panel) {
        JLabel lblNombres = new JLabel("NOMBRES:");
        lblNombres.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblNombres.setBounds(25, 41, 89, 13);
        panel.add(lblNombres);

        txt_nombres = new JTextField();
        txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_nombres.setBounds(124, 28, 427, 31);
        panel.add(txt_nombres);

        JLabel lblTelefono = new JLabel("TELEFONO:");
        lblTelefono.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblTelefono.setBounds(25, 80, 89, 13);
        panel.add(lblTelefono);

        txt_telefono = new JTextField();
        txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_telefono.setBounds(124, 69, 427, 31);
        panel.add(txt_telefono);

        JLabel lblEmail = new JLabel("EMAIL:");
        lblEmail.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblEmail.setBounds(25, 122, 89, 13);
        panel.add(lblEmail);

        txt_email = new JTextField();
        txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_email.setBounds(124, 110, 427, 31);
        panel.add(txt_email);

        chb_favorito = new JCheckBox("CONTACTO FAVORITO");
        chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
        chb_favorito.setBounds(24, 170, 193, 21);
        panel.add(chb_favorito);

        cmb_categoria = new JComboBox<>();
        cmb_categoria.setBounds(300, 167, 251, 31);
        panel.add(cmb_categoria);
        initCategorias();

        JLabel lblBuscar = new JLabel("BUSCAR CONTACTO:");
        lblBuscar.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblBuscar.setBounds(25, 661, 192, 13);
        panel.add(lblBuscar);

        txt_buscar = new JTextField();
        txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        txt_buscar.setBounds(212, 650, 784, 31);
        panel.add(txt_buscar);
    }

    private void addContactoButtons(JPanel panel) {
        btn_add = new JButton("AGREGAR");
        btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_add.setBounds(601, 70, 125, 65);
        panel.add(btn_add);

        btn_modificar = new JButton("MODIFICAR");
        btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_modificar.setBounds(736, 70, 125, 65);
        panel.add(btn_modificar);

        btn_eliminar = new JButton("ELIMINAR");
        btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_eliminar.setBounds(871, 69, 125, 65);
        panel.add(btn_eliminar);
    }

    private void addContactoTable(JPanel panel) {
        String[] columnas = { "NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA", "FAVORITO" };
        modeloTabla = new DefaultTableModel(null, columnas);
        tbl_contactos = new JTable(modeloTabla);
        tbl_contactos.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tbl_contactos.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tbl_contactos);
        scrollPane.setBounds(25, 242, 971, 398);
        panel.add(scrollPane);
    }

    private void addProgressBar(JPanel panel) {
        barraProgreso = new JProgressBar();
        barraProgreso.setBounds(25, 200, 971, 25);
        barraProgreso.setStringPainted(true);
        panel.add(barraProgreso);
    }

    private void addEstadisticasLabels(JPanel panel) {
        Font labelFont = new Font("Tahoma", Font.PLAIN, 16);

        lbl_totalContactos = new JLabel("Total de contactos:");
        lbl_totalContactos.setFont(labelFont);
        lbl_totalContactos.setBounds(50, 50, 400, 25);
        panel.add(lbl_totalContactos);

        lbl_favoritos = new JLabel("Contactos favoritos:");
        lbl_favoritos.setFont(labelFont);
        lbl_favoritos.setBounds(50, 90, 400, 25);
        panel.add(lbl_favoritos);

        lbl_familia = new JLabel("Contactos en categoría Familia:");
        lbl_familia.setFont(labelFont);
        lbl_familia.setBounds(50, 130, 400, 25);
        panel.add(lbl_familia);

        lbl_amigos = new JLabel("Contactos en categoría Amigos:");
        lbl_amigos.setFont(labelFont);
        lbl_amigos.setBounds(50, 170, 400, 25);
        panel.add(lbl_amigos);

        lbl_trabajo = new JLabel("Contactos en categoría Trabajo:");
        lbl_trabajo.setFont(labelFont);
        lbl_trabajo.setBounds(50, 210, 400, 25);
        panel.add(lbl_trabajo);

        btn_exportar = new JButton("Exportar a CSV");
        btn_exportar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn_exportar.setBounds(50, 260, 200, 40);
        panel.add(btn_exportar);
    }

    @SuppressWarnings("unchecked")
    private void initCategorias() {
        String[] categorias = { "Elija una Categoria", "Familia", "Amigos", "Trabajo" };
        for (String categoria : categorias) {
            cmb_categoria.addItem(categoria);
        }
    }
}
