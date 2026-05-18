package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.toedter.calendar.JDateChooser;
import com.tonin.animaltrack.model.Dosis;
import com.tonin.animaltrack.model.Evento;
import com.tonin.animaltrack.model.Semilla;
import com.tonin.animaltrack.model.TipoEvento;
import com.tonin.animaltrack.model.Tratamiento;
import com.tonin.animaltrack.dao.criteria.AnimalCriteria;
import com.tonin.animaltrack.dao.Results;
import com.tonin.animaltrack.model.dto.AnimalDTO;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.AnimalService;
import com.tonin.animaltrack.service.DosisService;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.SemillaService;
import com.tonin.animaltrack.service.TipoEventoService;
import com.tonin.animaltrack.service.TratamientoService;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.service.impl.DosisServiceImpl;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.service.impl.SemillaServiceImpl;
import com.tonin.animaltrack.service.impl.TipoEventoServiceImpl;
import com.tonin.animaltrack.service.impl.TratamientoServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.controler.CancelController;
import com.tonin.animaltrack.views.controler.Controller;
import com.tonin.animaltrack.views.controler.EventoCreateController;

public class EventoCreateView extends AbstractView {

	private static Logger logger = LogManager.getLogger(EventoCreateView.class.getName());

    private JTextField idTF;
    private JComboBox<ComboItem<AnimalDTO>> animalCombo;
    private JComboBox<ComboItem<TipoEvento>> tipoEventoCombo;
    private JComboBox<ComboItem<VeterinarioDTO>> veterinarioCombo;
    private JComboBox<ComboItem<Semilla>> semillaCombo;
    private JComboBox<ComboItem<Dosis>> dosisCombo;
    private JComboBox<ComboItem<Tratamiento>> tratamientoCombo;
    private JDateChooser fechaChooser;
    private JTextField horaTF;
    private JTextField precioTF;
    private JTextField resultadoTF;
    private JTextField observacionesTF;

    private AnimalService animalService;
    private TipoEventoService tipoEventoService;
    private VeterinarioService veterinarioService;
    private SemillaService semillaService;
    private DosisService dosisService;
    private TratamientoService tratamientoService;
    private EventoService eventoService;
    private JButton agreeButton;
    private JButton reloadButton;
    private JButton deleteButton;

    public EventoCreateView() {
        initialize();
        initServices();
        loadInitialData();
        setAgreeController(new EventoCreateController(this));
    }

    private void initialize() {
        setName("Nuevo Evento");
        setLayout(new BorderLayout(0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        add(formPanel, BorderLayout.CENTER);

        int row = 0;

        idTF = new JTextField(12);
        idTF.setEditable(false);
        idTF.setVisible(false);
        animalCombo = new JComboBox<ComboItem<AnimalDTO>>();
        tipoEventoCombo = new JComboBox<ComboItem<TipoEvento>>();
        veterinarioCombo = new JComboBox<ComboItem<VeterinarioDTO>>();
        semillaCombo = new JComboBox<ComboItem<Semilla>>();
        dosisCombo = new JComboBox<ComboItem<Dosis>>();
        tratamientoCombo = new JComboBox<ComboItem<Tratamiento>>();
        FilterableComboBoxSupport.decorate(animalCombo);
        FilterableComboBoxSupport.decorate(tipoEventoCombo);
        FilterableComboBoxSupport.decorate(veterinarioCombo);
        FilterableComboBoxSupport.decorate(semillaCombo);
        FilterableComboBoxSupport.decorate(dosisCombo);
        FilterableComboBoxSupport.decorate(tratamientoCombo);
        fechaChooser = new JDateChooser();
        horaTF = new JTextField(12);
        precioTF = new JTextField(12);
        resultadoTF = new JTextField(12);
        observacionesTF = new JTextField(24);

        addField(formPanel, row++, "Animal:", animalCombo);
        addField(formPanel, row++, "Tipo evento:", tipoEventoCombo);
        addField(formPanel, row++, "Veterinario:", veterinarioCombo);
        addField(formPanel, row++, "Fecha:", fechaChooser);
        addField(formPanel, row++, "Hora (HH:mm):", horaTF);
        addField(formPanel, row++, "Precio:", precioTF);
        addField(formPanel, row++, "Resultado:", resultadoTF);
        addField(formPanel, row++, "Observaciones:", observacionesTF);
        addField(formPanel, row++, "Semilla:", semillaCombo);
        addField(formPanel, row++, "Dosis:", dosisCombo);
        addField(formPanel, row++, "Tratamiento:", tratamientoCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        agreeButton = new JButton("Guardar");
        agreeButton.setIcon(new ImageIcon(EventoCreateView.class.getResource("/animaltrack/icons/32/save.png")));
        buttonPanel.add(agreeButton);

        reloadButton = new JButton("Recargar");
        reloadButton.setIcon(new ImageIcon(EventoCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.addActionListener(e -> loadInitialData());
        buttonPanel.add(reloadButton);

        deleteButton = new JButton("Borrar");
        deleteButton.setIcon(new ImageIcon(EventoCreateView.class.getResource("/animaltrack/icons/32/delete.png")));
        deleteButton.addActionListener(e -> deleteEvento());
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void initServices() {
        animalService = new AnimalServiceImpl();
        tipoEventoService = new TipoEventoServiceImpl();
        veterinarioService = new VeterinarioServiceImpl();
        semillaService = new SemillaServiceImpl();
        dosisService = new DosisServiceImpl();
        tratamientoService = new TratamientoServiceImpl();
        eventoService = new EventoServiceImpl();
    }

    private void loadInitialData() {
        try {
            AnimalCriteria criteria = new AnimalCriteria();
            criteria.setGranjaId(MainWindow.getInstance().getSelectedGranjaId());
            Results<AnimalDTO> results = animalService.findByCriteria(criteria, 1, Integer.MAX_VALUE);
            setModel(animalCombo, results == null ? null : results.getPageResults(), false);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            setModel(animalCombo, null, false);
        }
        try {
            setModel(tipoEventoCombo, tipoEventoService.findAll(), false);
            setModel(veterinarioCombo, veterinarioService.findAll(), true);
            setModel(semillaCombo, semillaService.findAll(), true);
            setModel(dosisCombo, dosisService.findAll(), true);
            setModel(tratamientoCombo, tratamientoService.findAll(), true);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            setModel(tipoEventoCombo, null, false);
            setModel(veterinarioCombo, null, true);
            setModel(semillaCombo, null, true);
            setModel(dosisCombo, null, true);
            setModel(tratamientoCombo, null, true);
        }
    }

    public boolean createEvento() {
        if (!MainWindow.getInstance().getPermissions().canCreateEvento()) {
            JOptionPane.showMessageDialog(this, "No tienes permisos para crear eventos.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Evento evento = buildEvento();
            EventoDTO created = eventoService.create(evento);
            if (created == null || created.getId() == null) {
                JOptionPane.showMessageDialog(this, "Faltan datos obligatorios. Revisa los datos introducidos.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            JOptionPane.showMessageDialog(this, "Evento guardado con ID " + created.getId(), "Correcto",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateEvento() {
        if (!MainWindow.getInstance().getPermissions().canEditEvento()) {
            JOptionPane.showMessageDialog(this, "No tienes permisos para editar eventos.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Evento evento = buildEvento();
            if (evento.getId() == null) {
                JOptionPane.showMessageDialog(this, "No hay ningún evento cargado para actualizar.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            eventoService.update(evento);
            EventoDTO updated = eventoService.findById(evento.getId());
            if (updated != null) {
                setModel(updated);
            }
            JOptionPane.showMessageDialog(this, "Evento actualizado correctamente.", "Correcto",
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
        animalCombo.setEnabled(editable);
        tipoEventoCombo.setEnabled(editable);
        veterinarioCombo.setEnabled(editable);
        semillaCombo.setEnabled(editable);
        dosisCombo.setEnabled(editable);
        tratamientoCombo.setEnabled(editable);
        fechaChooser.setEnabled(editable);
        horaTF.setEditable(editable);
        precioTF.setEditable(editable);
        resultadoTF.setEditable(editable);
        observacionesTF.setEditable(editable);
        deleteButton.setEnabled(!editable && MainWindow.getInstance().getPermissions().canDeleteEvento()
                && parseLong(idTF.getText()) != null);
        configureSecondaryButton(editable);
    }

    public void setModel(EventoDTO evento) {
        if (evento == null) {
            clearForm();
            return;
        }

        setName("Detalle Evento");
        idTF.setText(evento.getId() == null ? "" : String.valueOf(evento.getId()));
        selectComboItem(animalCombo, evento.getAnimalId());
        selectComboItem(tipoEventoCombo, evento.getTipoEventoId());
        selectComboItem(veterinarioCombo, evento.getVeterinarioId());
        selectComboItem(semillaCombo, evento.getSemillaId());
        selectComboItem(dosisCombo, evento.getDosisId());
        selectComboItem(tratamientoCombo, evento.getTratamientoId());
        if (evento.getFechaHora() == null) {
            fechaChooser.setDate(null);
            horaTF.setText("");
        } else {
            fechaChooser.setDate(Date.from(evento.getFechaHora().atZone(ZoneId.systemDefault()).toInstant()));
            horaTF.setText(String.format("%02d:%02d", evento.getFechaHora().getHour(), evento.getFechaHora().getMinute()));
        }
        precioTF.setText(evento.getPrecioEvento() == null ? "" : String.valueOf(evento.getPrecioEvento()));
        resultadoTF.setText(evento.getResultado() == null ? "" : evento.getResultado());
        observacionesTF.setText(evento.getObservaciones() == null ? "" : evento.getObservaciones());
        deleteButton.setEnabled(MainWindow.getInstance().getPermissions().canDeleteEvento() && evento.getId() != null);
    }

    public boolean deleteEvento() {
        if (!MainWindow.getInstance().getPermissions().canDeleteEvento()) {
            JOptionPane.showMessageDialog(this, "No tienes permisos para borrar eventos.", "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        Long id = parseLong(idTF.getText());
        if (id == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún evento cargado para borrar.", "Validación",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String expected = "ELIMINAR";
        ComboItem<AnimalDTO> animalItem = getSelectedItem(animalCombo);
        ComboItem<TipoEvento> tipoItem = getSelectedItem(tipoEventoCombo);
        if (animalItem != null && animalItem.getValue() != null && tipoItem != null && tipoItem.getValue() != null) {
            expected = animalItem.getValue().getCrotal() + " " + tipoItem.getValue().getNombre();
        }
        String typed = JOptionPane.showInputDialog(this, "Para borrar escribe \"" + expected + "\":",
                "Confirmar borrado", JOptionPane.WARNING_MESSAGE);
        if (!expected.equals(typed)) {
            return false;
        }
        try {
            eventoService.delete(id);
            JOptionPane.showMessageDialog(this, "Evento borrado correctamente.", "Correcto",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private Evento buildEvento() {
        ComboItem<AnimalDTO> animalItem = getSelectedItem(animalCombo);
        ComboItem<TipoEvento> tipoEventoItem = getSelectedItem(tipoEventoCombo);
        ComboItem<VeterinarioDTO> veterinarioItem = getSelectedItem(veterinarioCombo);
        ComboItem<Semilla> semillaItem = getSelectedItem(semillaCombo);
        ComboItem<Dosis> dosisItem = getSelectedItem(dosisCombo);
        ComboItem<Tratamiento> tratamientoItem = getSelectedItem(tratamientoCombo);

        Evento evento = new Evento();
        evento.setId(parseLong(idTF.getText()));
        evento.setAnimalId(animalItem == null || animalItem.getValue() == null ? null : animalItem.getValue().getId());
        evento.setTipoEventoId(tipoEventoItem == null || tipoEventoItem.getValue() == null ? null : tipoEventoItem.getValue().getId());
        evento.setVeterinarioId(veterinarioItem == null || veterinarioItem.getValue() == null ? null : veterinarioItem.getValue().getId());
        evento.setSemillaId(semillaItem == null || semillaItem.getValue() == null ? null : semillaItem.getValue().getId());
        evento.setDosisId(dosisItem == null || dosisItem.getValue() == null ? null : dosisItem.getValue().getId());
        evento.setTratamientoId(tratamientoItem == null || tratamientoItem.getValue() == null ? null : tratamientoItem.getValue().getId());
        evento.setFechaHora(buildFechaHora());
        evento.setPrecioEvento(parseBigDecimal(precioTF.getText()));
        evento.setResultado(trimToNull(resultadoTF.getText()));
        evento.setObservaciones(trimToNull(observacionesTF.getText()));
        return evento;
    }

    private LocalDateTime buildFechaHora() {
        if (fechaChooser.getDate() == null) {
            return null;
        }
        String hora = trimToNull(horaTF.getText());
        if (hora == null) {
            return LocalDateTime.ofInstant(fechaChooser.getDate().toInstant(), ZoneId.systemDefault())
                    .withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        String[] parts = hora.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("La hora debe tener formato HH:mm");
        }

        int hh = Integer.parseInt(parts[0]);
        int mm = Integer.parseInt(parts[1]);
        LocalDate date = fechaChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return LocalDateTime.of(date, LocalTime.of(hh, mm));
    }

    private BigDecimal parseBigDecimal(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : new BigDecimal(trimmed.replace(',', '.'));
    }

    private Long parseLong(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : Long.valueOf(trimmed);
    }

    private void clearForm() {
        setName("Nuevo Evento");
        idTF.setText("");
        clearComboSelection(animalCombo);
        clearComboSelection(tipoEventoCombo);
        clearComboSelection(veterinarioCombo);
        clearComboSelection(semillaCombo);
        clearComboSelection(dosisCombo);
        clearComboSelection(tratamientoCombo);
        fechaChooser.setDate(null);
        horaTF.setText("");
        precioTF.setText("");
        resultadoTF.setText("");
        observacionesTF.setText("");
        deleteButton.setEnabled(false);
        configureSecondaryButton(true);
    }

    private void configureSecondaryButton(boolean editable) {
        for (java.awt.event.ActionListener listener : reloadButton.getActionListeners()) {
            reloadButton.removeActionListener(listener);
        }

        if (editable && parseLong(idTF.getText()) != null) {
            reloadButton.setText("Cancelar");
            reloadButton.setIcon(new ImageIcon(EventoCreateView.class.getResource("/animaltrack/icons/32/cancel.png")));
            reloadButton.setEnabled(true);
            reloadButton.setAction(new CancelController(this));
            return;
        }

        reloadButton.setText("Recargar");
        reloadButton.setIcon(new ImageIcon(EventoCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.setEnabled(editable);
        reloadButton.addActionListener(e -> loadInitialData());
    }

    private void addField(JPanel panel, int row, Component labelComponent, Component fieldComponent) {
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

    private void addField(JPanel panel, int row, String label, Component component) {
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
        if (value instanceof AnimalDTO) {
            AnimalDTO animal = (AnimalDTO) value;
            String nombre = animal.getNombre() == null ? "" : animal.getNombre();
            return animal.getCrotal() + " - " + nombre;
        }
        if (value instanceof TipoEvento) {
            return ((TipoEvento) value).getNombre();
        }
        if (value instanceof VeterinarioDTO) {
            VeterinarioDTO veterinario = (VeterinarioDTO) value;
            return veterinario.getNombreCompleto();
        }
        if (value instanceof Semilla) {
            Semilla semilla = (Semilla) value;
            String descripcion = semilla.getDescripcion() == null ? "" : " - " + semilla.getDescripcion();
            return semilla.getCodigo() + descripcion;
        }
        if (value instanceof Dosis) {
            Dosis dosis = (Dosis) value;
            Tratamiento tratamiento = null;
            try {
                tratamiento = dosis.getTratamientoId() == null ? null
                        : tratamientoService.findById(dosis.getTratamientoId());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            String nombreTratamiento = tratamiento == null || tratamiento.getNombre() == null
                    ? ""
                    : " - " + tratamiento.getNombre();
            return "Dosis " + dosis.getNumOrdenDosis() + nombreTratamiento;
        }
        if (value instanceof Tratamiento) {
            return ((Tratamiento) value).getNombre();
        }
        return "";
    }

    private <T> void selectComboItem(JComboBox<ComboItem<T>> combo, Long id) {
        for (int i = 0; i < combo.getModel().getSize(); i++) {
            ComboItem<T> item = combo.getModel().getElementAt(i);
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
        if (value instanceof AnimalDTO) {
            return ((AnimalDTO) value).getId();
        }
        if (value instanceof TipoEvento) {
            return ((TipoEvento) value).getId();
        }
        if (value instanceof VeterinarioDTO) {
            return ((VeterinarioDTO) value).getId();
        }
        if (value instanceof Semilla) {
            return ((Semilla) value).getId();
        }
        if (value instanceof Dosis) {
            return ((Dosis) value).getId();
        }
        if (value instanceof Tratamiento) {
            return ((Tratamiento) value).getId();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = FilterableComboBoxSupport.getSelectedItem(combo);
        if (!(selectedItem instanceof ComboItem)) {
            return null;
        }
        return (ComboItem<T>) selectedItem;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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
