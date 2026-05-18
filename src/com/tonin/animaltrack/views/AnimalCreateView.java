package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.ComboBoxModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.tonin.animaltrack.model.Animal;
import com.tonin.animaltrack.model.Raza;
import com.tonin.animaltrack.model.Sexo;
import com.tonin.animaltrack.dao.criteria.AnimalCriteria;
import com.tonin.animaltrack.dao.criteria.EventoCriteria;
import com.tonin.animaltrack.dao.Results;
import com.tonin.animaltrack.model.dto.AnimalDTO;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.model.dto.GranjaDTO;
import com.tonin.animaltrack.service.AnimalService;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.GranjaService;
import com.tonin.animaltrack.service.RazaService;
import com.tonin.animaltrack.service.SexoService;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.service.impl.GranjaServiceImpl;
import com.tonin.animaltrack.service.impl.RazaServiceImpl;
import com.tonin.animaltrack.service.impl.SexoServiceImpl;
import com.tonin.animaltrack.views.controler.AnimalCreateController;
import com.tonin.animaltrack.views.controler.CancelController;
import com.tonin.animaltrack.views.controler.Controller;

public class AnimalCreateView extends AbstractView {

	private static Logger logger = LogManager.getLogger(AnimalCreateView.class.getName());

    private static final long serialVersionUID = 1L;

    private JTextField nombreTF;
    private JTextField crotalTF;
    private JTextField fechaNacimientoTF;
    private JLabel fechaBajaLabel;
    private JTextField fechaBajaTF;
    private JTextField madreExternaTF;
    private JTextField idTF;
    private JComboBox<ComboItem<GranjaDTO>> granjaCombo;
    private JComboBox<ComboItem<Sexo>> sexoCombo;
    private JComboBox<ComboItem<Raza>> razaCombo;
    private JComboBox<ComboItem<AnimalDTO>> madreInternaCombo;
    private JComboBox<ComboItem<AnimalDTO>> padreInternoCombo;
    private AnimalService animalService;
    private EventoService eventoService;
    private GranjaService granjaService;
    private SexoService sexoService;
    private RazaService razaService;
    private JButton agreeButton;
    private JButton reloadButton;
    private JTable ultimosEventosTable;
    private JLabel fotoLabel;
    private JButton seleccionarFotoButton;
    private JButton quitarFotoButton;
    private byte[] fotoBytes;
    private Long currentEventPartoId;

    public AnimalCreateView() {
        initialize();
        initServices();
        loadInitialData();
        setAgreeController(new AnimalCreateController(this));
    }

    private void initialize() {
        setName("Nuevo Animal");
        setLayout(new BorderLayout(0, 0));

        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(new EmptyBorder(14, 10, 10, 10));
        add(contentPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(18, 0));
        contentPanel.add(topPanel, BorderLayout.NORTH);

        JPanel photoPanel = createPhotoPanel();
        topPanel.add(photoPanel, BorderLayout.WEST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);

        int row = 0;

        nombreTF = new JTextField(20);
        crotalTF = new JTextField(20);
        fechaNacimientoTF = new JTextField(20);
        fechaBajaTF = new JTextField(20);
        madreExternaTF = new JTextField(20);
        idTF = new JTextField(20);
        idTF.setEditable(false);
        idTF.setVisible(false);
        granjaCombo = new JComboBox<ComboItem<GranjaDTO>>();
        sexoCombo = new JComboBox<ComboItem<Sexo>>();
        razaCombo = new JComboBox<ComboItem<Raza>>();
        madreInternaCombo = new JComboBox<ComboItem<AnimalDTO>>();
        padreInternoCombo = new JComboBox<ComboItem<AnimalDTO>>();
        FilterableComboBoxSupport.decorate(granjaCombo);
        FilterableComboBoxSupport.decorate(sexoCombo);
        FilterableComboBoxSupport.decorate(razaCombo);
        FilterableComboBoxSupport.decorate(madreInternaCombo);
        FilterableComboBoxSupport.decorate(padreInternoCombo);

        addField(formPanel, row++, "Nombre:", nombreTF);
        addField(formPanel, row++, "Crotal:", crotalTF);
        addField(formPanel, row++, "Fecha nac. (yyyy-MM-dd):", fechaNacimientoTF);
        fechaBajaLabel = new JLabel("Fecha baja (yyyy-MM-dd):");
        addField(formPanel, row++, fechaBajaLabel, fechaBajaTF);
        addField(formPanel, row++, "Granja:", granjaCombo);
        addField(formPanel, row++, "Sexo:", sexoCombo);
        addField(formPanel, row++, "Raza:", razaCombo);
        addField(formPanel, row++, "Madre interna:", madreInternaCombo);
        addField(formPanel, row++, "Madre externa:", madreExternaTF);
        addField(formPanel, row++, "Padre interno:", padreInternoCombo);

        JPanel eventosPanel = new JPanel(new BorderLayout(0, 6));
        eventosPanel.setBorder(BorderFactory.createTitledBorder("Ultimos 5 eventos"));
        ultimosEventosTable = new JTable();
        ultimosEventosTable.setFillsViewportHeight(true);
        ultimosEventosTable.setRowHeight(24);
        ultimosEventosTable.setModel(createEventosTableModel(Collections.<EventoDTO>emptyList()));
        eventosPanel.add(new JScrollPane(ultimosEventosTable), BorderLayout.CENTER);
        contentPanel.add(eventosPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        agreeButton = new JButton("Guardar");
        agreeButton.setIcon(new ImageIcon(AnimalCreateView.class.getResource("/animaltrack/icons/32/save.png")));
        buttonPanel.add(agreeButton);

        reloadButton = new JButton("Recargar");
        reloadButton.setIcon(new ImageIcon(AnimalCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.addActionListener(e -> reloadBySelectedFarm());
        buttonPanel.add(reloadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        showFechaBaja(false);
        granjaCombo.addActionListener(e -> reloadBySelectedFarm());
    }

    private JPanel createPhotoPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 6));
        JPanel photoPanel = new JPanel(new GridBagLayout());
        photoPanel.setPreferredSize(new Dimension(460, 260));
        photoPanel.setMinimumSize(new Dimension(320, 220));
        photoPanel.setBorder(BorderFactory.createLineBorder(new Color(190, 190, 190)));
        photoPanel.setBackground(new Color(245, 245, 245));

        fotoLabel = new JLabel("Foto del animal");
        fotoLabel.setForeground(new Color(100, 100, 100));
        fotoLabel.setFont(fotoLabel.getFont().deriveFont(Font.BOLD, 18f));
        photoPanel.add(fotoLabel);

        JPanel photoButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        seleccionarFotoButton = new JButton("Subir foto");
        seleccionarFotoButton.addActionListener(e -> seleccionarFoto());
        quitarFotoButton = new JButton("Quitar foto");
        quitarFotoButton.addActionListener(e -> {
            fotoBytes = null;
            updateFotoPreview();
        });
        photoButtonsPanel.add(seleccionarFotoButton);
        photoButtonsPanel.add(quitarFotoButton);

        container.add(photoPanel, BorderLayout.CENTER);
        container.add(photoButtonsPanel, BorderLayout.SOUTH);
        return container;
    }

    private void initServices() {
        animalService = new AnimalServiceImpl();
        eventoService = new EventoServiceImpl();
        granjaService = new GranjaServiceImpl();
        sexoService = new SexoServiceImpl();
        razaService = new RazaServiceImpl();
    }

    private void loadInitialData() {
        try {
            setModel(granjaCombo, granjaService.findAll(), true);
            setModel(sexoCombo, sexoService.findAll(), false);
            setModel(razaCombo, razaService.findAll(), true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los datos iniciales.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        reloadBySelectedFarm();
    }

    private void reloadBySelectedFarm() {
        Long selectedMadreId = getSelectedValueId(madreInternaCombo);
        Long selectedPadreId = getSelectedValueId(padreInternoCombo);
        ComboItem<GranjaDTO> selected = getSelectedItem(granjaCombo);
        if (selected == null || selected.getValue() == null) {
            setModel(madreInternaCombo, null, true);
            setModel(padreInternoCombo, null, true);
            return;
        }
        Long granjaId = selected.getValue().getId();
        setModel(madreInternaCombo, findAnimalesBySexo(granjaId, "Hembra"), true);
        setModel(padreInternoCombo, findAnimalesBySexo(granjaId, "Macho"), true);
        selectComboItem(madreInternaCombo, selectedMadreId);
        selectComboItem(padreInternoCombo, selectedPadreId);
    }

    private List<AnimalDTO> findAnimalesBySexo(Long granjaId, String sexoNombre) {
        Long sexoId = findSexoId(sexoNombre);
        if (granjaId == null || sexoId == null) {
            return Collections.emptyList();
        }

        AnimalCriteria criteria = new AnimalCriteria();
        criteria.setGranjaId(granjaId);
        criteria.setSexoId(sexoId);

        try {
            List<AnimalDTO> animales = animalService.findByCriteria(criteria);
            return animales == null ? Collections.<AnimalDTO>emptyList() : animales;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    private Long findSexoId(String sexoNombre) {
        ComboBoxModel<ComboItem<Sexo>> model = sexoCombo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ComboItem<Sexo> item = model.getElementAt(i);
            if (item != null && item.getValue() != null && item.getValue().getNombre() != null
                    && item.getValue().getNombre().equalsIgnoreCase(sexoNombre)) {
                return item.getValue().getId();
            }
        }
        return null;
    }

    public boolean createAnimal() {
        try {
            Animal animal = buildAnimal();
            AnimalDTO created = animalService.create(animal);
            if (created == null || created.getId() == null) {
                JOptionPane.showMessageDialog(this,
                        "Faltan datos obligatorios. Revisa los datos introducidos.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            JOptionPane.showMessageDialog(this, "Animal guardado con ID " + created.getId(), "Correcto",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            reloadBySelectedFarm();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateAnimal() {
        try {
            Animal animal = buildAnimal();
            if (animal.getId() == null) {
                JOptionPane.showMessageDialog(this, "No hay ningún animal cargado para actualizar.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            animalService.update(animal);
            AnimalDTO updated = animalService.findById(animal.getId());
            if (updated != null) {
                setModel(updated);
            }
            JOptionPane.showMessageDialog(this, "Animal actualizado correctamente.", "Correcto",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void setAgreeController(Controller controller) {
        agreeButton.setAction(controller);
    }

    public void setEditable(boolean editable) {
        if (editable && parseLong(idTF.getText()) != null) {
            showFechaBaja(true);
        } else if (!editable) {
            showFechaBaja(trimToNull(fechaBajaTF.getText()) != null);
        }
        nombreTF.setEditable(editable);
        crotalTF.setEditable(editable);
        fechaNacimientoTF.setEditable(editable);
        fechaBajaTF.setEditable(editable && fechaBajaTF.isVisible());
        madreExternaTF.setEditable(editable);
        granjaCombo.setEnabled(editable);
        sexoCombo.setEnabled(editable);
        razaCombo.setEnabled(editable);
        madreInternaCombo.setEnabled(editable);
        padreInternoCombo.setEnabled(editable);
        seleccionarFotoButton.setEnabled(editable);
        quitarFotoButton.setEnabled(editable && fotoBytes != null);
        configureSecondaryButton(editable);
    }

    public void setModel(AnimalDTO animal) {
        currentEventPartoId = animal == null ? null : animal.getEventPartoId();

        if (animal == null) {
            clearForm();
            return;
        }

        setName("Detalle Animal");
        showFechaBaja(animal.getFechaBaja() != null);
        idTF.setText(animal.getId() == null ? "" : String.valueOf(animal.getId()));
        nombreTF.setText(defaultString(animal.getNombre()));
        crotalTF.setText(defaultString(animal.getCrotal()));
        fechaNacimientoTF.setText(animal.getFechaNacimiento() == null ? "" : animal.getFechaNacimiento().toString());
        fechaBajaTF.setText(animal.getFechaBaja() == null ? "" : animal.getFechaBaja().toString());
        madreExternaTF.setText(defaultString(animal.getMadreExternaCrotal()));
        fotoBytes = animal.getFoto();
        updateFotoPreview();

        selectComboItem(granjaCombo, animal.getGranjaId());
        selectComboItem(sexoCombo, animal.getSexoId());
        selectComboItem(razaCombo, animal.getRazaId());
        reloadBySelectedFarm();
        selectComboItem(madreInternaCombo, animal.getMadreInternaId());
        selectComboItem(padreInternoCombo, animal.getPadreInternoId());
        loadUltimosEventos(animal.getId());
    }

    private Animal buildAnimal() {
        ComboItem<GranjaDTO> granjaItem = getSelectedItem(granjaCombo);
        ComboItem<Sexo> sexoItem = getSelectedItem(sexoCombo);
        ComboItem<Raza> razaItem = getSelectedItem(razaCombo);
        ComboItem<AnimalDTO> madreItem = getSelectedItem(madreInternaCombo);
        ComboItem<AnimalDTO> padreItem = getSelectedItem(padreInternoCombo);

        String madreExterna = trimToNull(madreExternaTF.getText());
        if (madreItem != null && madreItem.getValue() != null && madreExterna != null) {
            throw new IllegalArgumentException("Solo puedes indicar madre interna o madre externa.");
        }

        Animal animal = new Animal();
        animal.setId(parseLong(idTF.getText()));
        animal.setNombre(trimToNull(nombreTF.getText()));
        animal.setCrotal(trimToNull(crotalTF.getText()));
        animal.setFechaNacimiento(parseDate(fechaNacimientoTF.getText()));
        animal.setFechaBaja(animal.getId() == null ? null : parseDate(fechaBajaTF.getText()));
        animal.setGranjaId(granjaItem == null || granjaItem.getValue() == null ? null : granjaItem.getValue().getId());
        animal.setSexoId(sexoItem == null || sexoItem.getValue() == null ? null : sexoItem.getValue().getId());
        animal.setRazaId(razaItem == null || razaItem.getValue() == null ? null : razaItem.getValue().getId());
        animal.setMadreInternaId(madreItem == null || madreItem.getValue() == null ? null : madreItem.getValue().getId());
        animal.setMadreExternaCrotal(madreExterna);
        animal.setPadreInternoId(padreItem == null || padreItem.getValue() == null ? null : padreItem.getValue().getId());
        animal.setEventPartoId(currentEventPartoId);
        animal.setFoto(fotoBytes);
        return animal;
    }

    private void clearForm() {
        currentEventPartoId = null;
        fotoBytes = null;
        setName("Nuevo Animal");
        idTF.setText("");
        nombreTF.setText("");
        crotalTF.setText("");
        fechaNacimientoTF.setText("");
        fechaBajaTF.setText("");
        madreExternaTF.setText("");
        clearComboSelection(granjaCombo);
        clearComboSelection(razaCombo);
        clearComboSelection(sexoCombo);
        clearComboSelection(madreInternaCombo);
        clearComboSelection(padreInternoCombo);
        updateFotoPreview();
        ultimosEventosTable.setModel(createEventosTableModel(Collections.<EventoDTO>emptyList()));
        configureSecondaryButton(true);
        showFechaBaja(false);
    }

    private void seleccionarFoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Imagenes", "jpg", "jpeg", "png", "gif"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = chooser.getSelectedFile();
        try {
            fotoBytes = Files.readAllBytes(selectedFile.toPath());
            updateFotoPreview();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen seleccionada.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFotoPreview() {
        if (fotoBytes == null || fotoBytes.length == 0) {
            fotoLabel.setIcon(null);
            fotoLabel.setText("Foto del animal");
            fotoLabel.setHorizontalAlignment(JLabel.CENTER);
            quitarFotoButton.setEnabled(false);
            return;
        }

        ImageIcon imageIcon = new ImageIcon(fotoBytes);
        Image scaledImage = imageIcon.getImage().getScaledInstance(440, 250, Image.SCALE_SMOOTH);
        fotoLabel.setText("");
        fotoLabel.setIcon(new ImageIcon(scaledImage));
        fotoLabel.setHorizontalAlignment(JLabel.CENTER);
        quitarFotoButton.setEnabled(seleccionarFotoButton.isEnabled());
    }

    private void loadUltimosEventos(Long animalId) {
        if (animalId == null) {
            ultimosEventosTable.setModel(createEventosTableModel(Collections.<EventoDTO>emptyList()));
            return;
        }

        try {
            EventoCriteria criteria = new EventoCriteria();
            criteria.setAnimalId(animalId);
            Results<EventoDTO> results = eventoService.findByCriteria(criteria, 1, 5);
            List<EventoDTO> eventos = results == null ? Collections.<EventoDTO>emptyList() : results.getPageResults();
            ultimosEventosTable.setModel(createEventosTableModel(eventos));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ultimosEventosTable.setModel(createEventosTableModel(Collections.<EventoDTO>emptyList()));
        }
    }

    private DefaultTableModel createEventosTableModel(List<EventoDTO> eventos) {
        DefaultTableModel tableModel = new DefaultTableModel(new Object[] {
                "Fecha", "Tipo", "Veterinario", "Resultado", "Observaciones"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (eventos != null) {
            for (EventoDTO evento : eventos) {
                tableModel.addRow(new Object[] {
                        evento.getFechaHora() == null ? "" : evento.getFechaHora().toString(),
                        defaultString(evento.getTipoEventoNombre()),
                        defaultString(evento.getVeterinarioNombreCompleto()),
                        defaultString(evento.getResultado()),
                        defaultString(evento.getObservaciones())
                });
            }
        }
        return tableModel;
    }

    private void configureSecondaryButton(boolean editable) {
        for (java.awt.event.ActionListener listener : reloadButton.getActionListeners()) {
            reloadButton.removeActionListener(listener);
        }

        if (editable && parseLong(idTF.getText()) != null) {
            reloadButton.setText("Cancelar");
            reloadButton.setIcon(new ImageIcon(AnimalCreateView.class.getResource("/animaltrack/icons/32/cancel.png")));
            reloadButton.setEnabled(true);
            reloadButton.setAction(new CancelController(this));
            return;
        }

        reloadButton.setText("Recargar");
        reloadButton.setIcon(new ImageIcon(AnimalCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.setEnabled(editable);
        reloadButton.addActionListener(e -> reloadBySelectedFarm());
    }

    private void addField(JPanel panel, int row, java.awt.Component labelComponent, java.awt.Component fieldComponent) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        labelConstraints.insets = new Insets(6, 6, 6, 6);
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(labelComponent, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(6, 6, 6, 6);
        fieldConstraints.anchor = GridBagConstraints.WEST;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fieldComponent, fieldConstraints);
    }

    private void addField(JPanel panel, int row, String label, java.awt.Component component) {
        addField(panel, row, new JLabel(label), component);
    }

    private <T> void setModel(JComboBox<ComboItem<T>> combo, List<T> values, boolean includeEmpty) {
        DefaultComboBoxModel<ComboItem<T>> model = new DefaultComboBoxModel<ComboItem<T>>();
        if (values != null) {
            for (T value : values) {
                model.addElement(new ComboItem<T>(value, buildLabel(value)));
            }
        }
        combo.setModel(model);
        clearComboSelection(combo);
    }

    private <T> void clearComboSelection(JComboBox<ComboItem<T>> combo) {
        combo.setSelectedIndex(-1);
        combo.getEditor().setItem("");
    }

    private String buildLabel(Object value) {
        if (value instanceof GranjaDTO) {
            GranjaDTO granja = (GranjaDTO) value;
            return granja.getNombre();
        }
        if (value instanceof Sexo) {
            return ((Sexo) value).getNombre();
        }
        if (value instanceof Raza) {
            return ((Raza) value).getNombre();
        }
        if (value instanceof AnimalDTO) {
            AnimalDTO animal = (AnimalDTO) value;
            String nombre = animal.getNombre() == null ? "" : animal.getNombre();
            return animal.getCrotal() + " - " + nombre;
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = FilterableComboBoxSupport.getSelectedItem(combo);
        if (selectedItem instanceof ComboItem) {
            return (ComboItem<T>) selectedItem;
        }
        return null;
    }

    private void showFechaBaja(boolean visible) {
        fechaBajaLabel.setVisible(visible);
        fechaBajaTF.setVisible(visible);
        fechaBajaTF.setEditable(visible);
        if (!visible) {
            fechaBajaTF.setText("");
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private Long parseLong(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : Long.valueOf(trimmed);
    }

    private Date parseDate(String value) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        try {
            return Date.valueOf(trimmed);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La fecha de nacimiento debe tener formato yyyy-MM-dd.");
        }
    }

    private <T> void selectComboItem(JComboBox<ComboItem<T>> combo, Long id) {
        ComboBoxModel<ComboItem<T>> model = combo.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            ComboItem<T> item = model.getElementAt(i);
            Long itemId = getId(item == null ? null : item.getValue());
            if (id == null) {
                if (itemId == null) {
                    combo.setSelectedIndex(i);
                    return;
                }
            } else if (id.equals(itemId)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private Long getId(Object value) {
        if (value instanceof GranjaDTO) {
            return ((GranjaDTO) value).getId();
        }
        if (value instanceof Sexo) {
            return ((Sexo) value).getId();
        }
        if (value instanceof Raza) {
            return ((Raza) value).getId();
        }
        if (value instanceof AnimalDTO) {
            return ((AnimalDTO) value).getId();
        }
        return null;
    }

    private <T> Long getSelectedValueId(JComboBox<ComboItem<T>> combo) {
        ComboItem<T> item = getSelectedItem(combo);
        return getId(item == null ? null : item.getValue());
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
