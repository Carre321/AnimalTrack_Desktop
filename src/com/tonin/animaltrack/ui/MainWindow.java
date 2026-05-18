package com.tonin.animaltrack.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.tonin.animaltrack.model.VeterinarioGranja;
import com.tonin.animaltrack.model.dto.GranjaDTO;
import com.tonin.animaltrack.model.dto.UsuarioLoginDTO;
import com.tonin.animaltrack.service.GranjaService;
import com.tonin.animaltrack.service.VeterinarioGranjaService;
import com.tonin.animaltrack.service.impl.GranjaServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioGranjaServiceImpl;
import com.tonin.animaltrack.views.FarmFilterAware;
import com.tonin.animaltrack.views.FilterableComboBoxSupport;
import com.tonin.animaltrack.views.AbstractView;
import com.tonin.animaltrack.views.DashboardGanaderoView;
import com.tonin.animaltrack.views.controler.OpenAdminController;
import com.tonin.animaltrack.views.controler.OpenAnimalSearchController;
import com.tonin.animaltrack.views.controler.OpenEventoSearchController;
import com.tonin.animaltrack.views.controler.OpenVeterinarioSearchController;


/*
 * Ventana principal del sistema, desde la cual se accede a las diferentes funcionalidades. Contiene un menú lateral para navegar entre vistas, un combo para filtrar por granja y un botón para mostrar información del usuario logueado.
 */

public class MainWindow {

	private static Logger logger = LogManager.getLogger(MainWindow.class.getName());

    private static MainWindow instance = null;

    private JFrame frame;
    private JPanel contentPanel;
    private JButton animalButton;
    private JButton eventoButton;
    private JButton veterinarioButton;
    private JButton adminButton;
    private JButton usuarioButton;
    private JComboBox<ComboItem<GranjaDTO>> granjaComboBox;
    private UsuarioLoginDTO currentUser;
    private Long selectedGranjaId;
    private final GranjaService granjaService;
    private final VeterinarioGranjaService veterinarioGranjaService;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	UIManager.setLookAndFeel(new FlatLightLaf());
                    new LoginWindow().showWindow();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    private MainWindow() {
        this.granjaService = new GranjaServiceImpl();
        this.veterinarioGranjaService = new VeterinarioGranjaServiceImpl();
        initialize();
        postinitialize();
    }

    public static MainWindow getInstance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 700, 491);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel mainPanel = new JPanel();
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));

        JPanel northPanel = new JPanel();
        mainPanel.add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new BorderLayout(0, 0));

        JPanel southPanel = new JPanel();
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        JTextArea txtrAnimaltrack = new JTextArea();
        txtrAnimaltrack.setText("AnimalTrack 2025-2030");
        southPanel.add(txtrAnimaltrack);

        JPanel eastPanel = new JPanel();
        mainPanel.add(eastPanel, BorderLayout.WEST);
        eastPanel.setLayout(new BorderLayout(0, 0));

        JPanel mainMenuPanel = new JPanel();
        mainMenuPanel.setBackground(Color.LIGHT_GRAY);
        eastPanel.add(mainMenuPanel, BorderLayout.CENTER);
        GridBagLayout gbl_mainMenuPanel = new GridBagLayout();
        gbl_mainMenuPanel.columnWidths = new int[] { 0, 0 };
        gbl_mainMenuPanel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_mainMenuPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl_mainMenuPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        mainMenuPanel.setLayout(gbl_mainMenuPanel);

        eventoButton = new JButton("Eventos");
        eventoButton.setIcon(new ImageIcon(MainWindow.class.getResource("/animaltrack/icons/32/calendar.png")));
        GridBagConstraints gbc_eventoButton = new GridBagConstraints();
        gbc_eventoButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_eventoButton.insets = new Insets(0, 0, 5, 0);
        gbc_eventoButton.gridx = 0;
        gbc_eventoButton.gridy = 0;
        mainMenuPanel.add(eventoButton, gbc_eventoButton);

        animalButton = new JButton("Animal");
        animalButton.setIcon(new ImageIcon(MainWindow.class.getResource("/animaltrack/icons/32/animal.png")));
        GridBagConstraints gbc_animalButton = new GridBagConstraints();
        gbc_animalButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_animalButton.insets = new Insets(0, 0, 5, 0);
        gbc_animalButton.gridx = 0;
        gbc_animalButton.gridy = 1;
        mainMenuPanel.add(animalButton, gbc_animalButton);

        veterinarioButton = new JButton("Veterinario");
        veterinarioButton.setIcon(new ImageIcon(MainWindow.class.getResource("/animaltrack/icons/32/veterinarian.png")));
        GridBagConstraints gbc_veterinarioButton = new GridBagConstraints();
        gbc_veterinarioButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_veterinarioButton.gridx = 0;
        gbc_veterinarioButton.gridy = 2;
        mainMenuPanel.add(veterinarioButton, gbc_veterinarioButton);

        adminButton = new JButton("Administración");
        adminButton.setIcon(new ImageIcon(MainWindow.class.getResource("/animaltrack/icons/32/admin.png")));
        GridBagConstraints gbc_adminButton = new GridBagConstraints();
        gbc_adminButton.fill = GridBagConstraints.HORIZONTAL;
        gbc_adminButton.insets = new Insets(5, 0, 0, 0);
        gbc_adminButton.gridx = 0;
        gbc_adminButton.gridy = 3;
        mainMenuPanel.add(adminButton, gbc_adminButton);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel izquierdaPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) izquierdaPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        northPanel.add(izquierdaPanel, BorderLayout.WEST);

        JButton burgerbutton = new JButton("\u2630");
        burgerbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eastPanel.setVisible(!eastPanel.isVisible());
                eastPanel.revalidate();
                eastPanel.repaint();
            }
        });
        burgerbutton.setFocusable(false);
        izquierdaPanel.add(burgerbutton);

        granjaComboBox = new JComboBox<ComboItem<GranjaDTO>>();
        FilterableComboBoxSupport.decorate(granjaComboBox);
        granjaComboBox.setToolTipText("Seleccionar granja");
        granjaComboBox.setEnabled(false);
        granjaComboBox.addActionListener(e -> {
            ComboItem<GranjaDTO> selectedItem = getSelectedGranjaItem();
            selectedGranjaId = selectedItem == null || selectedItem.getValue() == null ? null : selectedItem.getValue().getId();
            refreshCurrentViewForFarmFilter();
        });
        izquierdaPanel.add(granjaComboBox);

        JPanel derechaPanel = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) derechaPanel.getLayout();
        flowLayout_1.setAlignment(FlowLayout.RIGHT);
        northPanel.add(derechaPanel, BorderLayout.EAST);

        usuarioButton = new JButton("");
        usuarioButton.setIcon(new ImageIcon(MainWindow.class.getResource("/animaltrack/icons/16/login.png")));
        usuarioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentUser == null) {
                    JOptionPane.showMessageDialog(frame, "No hay sesión iniciada.", "Usuario",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                showProfile();
            }
        });
        derechaPanel.add(usuarioButton);
    }

    private void postinitialize() {
        OpenAnimalSearchController animalController = new OpenAnimalSearchController();
        animalButton.addActionListener(animalController);
        OpenEventoSearchController eventoController = new OpenEventoSearchController();
        eventoButton.addActionListener(eventoController);
        OpenVeterinarioSearchController veterinarioController = new OpenVeterinarioSearchController();
        veterinarioButton.addActionListener(veterinarioController);
        OpenAdminController adminController = new OpenAdminController();
        adminButton.addActionListener(adminController);
    }

    public void setView(AbstractView view) {
        contentPanel.removeAll();
        contentPanel.add(view, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        refreshCurrentViewForFarmFilter();
    }

    public void remove(AbstractView view) {
        if (view == null) {
            return;
        }
        contentPanel.remove(view);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void setCurrentUser(UsuarioLoginDTO user) {
        currentUser = user;
        if (usuarioButton == null) {
            return;
        }
        if (user == null) {
            usuarioButton.setText("");
            usuarioButton.setToolTipText("Sin sesión");
            selectedGranjaId = null;
            loadFarmCombo(new ArrayList<GranjaDTO>());
            return;
        }
        usuarioButton.setText(user.getNombreVisible());
        usuarioButton.setToolTipText(user.getEmail() + " (" + user.getRol() + ")");
        loadFarmCombo(resolveAvailableFarms(user));
        setView(new DashboardGanaderoView());
    }

    public Long getSelectedGranjaId() {
        return selectedGranjaId;
    }

    public void showWindow() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showProfile() {
        JPanel profilePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addProfileRow(profilePanel, gbc, 0, "Usuario:", currentUser.getNombreVisible());
        addProfileRow(profilePanel, gbc, 1, "Rol:", currentUser.getRol());
        addProfileRow(profilePanel, gbc, 2, "Email:", currentUser.getEmail());

        Object[] options = { "Cerrar sesión", "Cerrar" };
        int option = JOptionPane.showOptionDialog(frame, profilePanel, "Perfil", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon(MainWindow.class.getResource("/animaltrack/logo-64.png")), options, options[1]);
        if (option == JOptionPane.YES_OPTION) {
            logout();
        }
    }

    private void addProfileRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel(value == null ? "" : value), gbc);
    }

    public void logout() {
        int option = JOptionPane.showConfirmDialog(frame, "¿Cerrar la sesión actual?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        setCurrentUser(null);
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();

        frame.dispose();
        instance = null;
        new LoginWindow().showWindow();
    }

    private List<GranjaDTO> resolveAvailableFarms(UsuarioLoginDTO user) {
        if (user == null) {
            return new ArrayList<GranjaDTO>();
        }
        try {
            if (isAdmin(user)) {
                List<GranjaDTO> granjas = granjaService.findAll();
                return granjas == null ? new ArrayList<GranjaDTO>() : granjas;
            }
            if (user.getGanaderoId() != null) {
                List<GranjaDTO> granjas = granjaService.findByGanaderoId(user.getGanaderoId());
                return granjas == null ? new ArrayList<GranjaDTO>() : granjas;
            }
            if (user.getVeterinarioId() != null) {
                List<VeterinarioGranja> relaciones = veterinarioGranjaService.findByVeterinarioId(user.getVeterinarioId());
                Map<Long, GranjaDTO> granjas = new LinkedHashMap<Long, GranjaDTO>();
                if (relaciones != null) {
                    for (VeterinarioGranja relacion : relaciones) {
                        GranjaDTO granja = granjaService.findById(relacion.getGranjaId());
                        if (granja != null && granja.getId() != null) {
                            granjas.put(granja.getId(), granja);
                        }
                    }
                }
                return new ArrayList<GranjaDTO>(granjas.values());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(frame, "No se pudieron cargar las granjas disponibles.", "AnimalTrack",
                    JOptionPane.ERROR_MESSAGE);
        }
        return new ArrayList<GranjaDTO>();
    }

    private boolean isAdmin(UsuarioLoginDTO user) {
        return user != null && user.getRol() != null && "ADMINISTRADOR".equalsIgnoreCase(user.getRol());
    }

    private void loadFarmCombo(List<GranjaDTO> granjas) {
        DefaultComboBoxModel<ComboItem<GranjaDTO>> model = new DefaultComboBoxModel<ComboItem<GranjaDTO>>();
        if (granjas != null) {
            for (GranjaDTO granja : granjas) {
                model.addElement(new ComboItem<GranjaDTO>(granja, granja.getNombre()));
            }
        }
        granjaComboBox.setModel(model);
        granjaComboBox.setSelectedIndex(-1);
        granjaComboBox.getEditor().setItem("");
        granjaComboBox.setEnabled(model.getSize() > 0);
        selectedGranjaId = null;
    }

    private ComboItem<GranjaDTO> getSelectedGranjaItem() {
        Object selected = FilterableComboBoxSupport.getSelectedItem(granjaComboBox);
        @SuppressWarnings("unchecked")
        ComboItem<GranjaDTO> selectedItem = selected instanceof ComboItem
                ? (ComboItem<GranjaDTO>) selected
                : null;
        return selectedItem;
    }

    private void refreshCurrentViewForFarmFilter() {
        if (contentPanel.getComponentCount() == 0) {
            return;
        }
        java.awt.Component component = contentPanel.getComponent(0);
        if (component instanceof FarmFilterAware) {
            ((FarmFilterAware) component).refreshForSelectedFarm();
        }
    }

    private static class ComboItem<T> {

        private final T value;
        private final String label;

        ComboItem(T value, String label) {
            this.value = value;
            this.label = label;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
