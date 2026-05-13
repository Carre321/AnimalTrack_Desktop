package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import com.tonin.animaltrack.model.Municipio;
import com.tonin.animaltrack.model.Provincia;
import com.tonin.animaltrack.model.Veterinario;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.MunicipioService;
import com.tonin.animaltrack.service.ProvinciaService;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.MunicipioServiceImpl;
import com.tonin.animaltrack.service.impl.ProvinciaServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.views.controler.CancelController;
import com.tonin.animaltrack.views.controler.Controller;
import com.tonin.animaltrack.views.controler.VeterinarioCreateController;

public class VeterinarioCreateView extends AbstractView {

	private static Logger logger = LogManager.getLogger(VeterinarioCreateView.class.getName());

    private JTextField idTF;
    private JTextField codigoTF;
    private JTextField dniTF;
    private JTextField nombreTF;
    private JTextField apellidosTF;
    private JTextField telefonoTF;
    private JTextField emailTF;
    private JComboBox<ComboItem<Provincia>> provinciaCombo;
    private JComboBox<ComboItem<Municipio>> municipioCombo;

    private ProvinciaService provinciaService;
    private MunicipioService municipioService;
    private VeterinarioService veterinarioService;
    private JButton agreeButton;
    private JButton reloadButton;

    public VeterinarioCreateView() {
        initialize();
        initServices();
        loadInitialData();
        setAgreeController(new VeterinarioCreateController(this));
    }

    private void initialize() {
        setName("Nuevo Veterinario");
        setLayout(new BorderLayout(0, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        add(formPanel, BorderLayout.CENTER);

        int row = 0;

        idTF = new JTextField(20);
        idTF.setEditable(false);
        idTF.setVisible(false);
        codigoTF = new JTextField(20);
        dniTF = new JTextField(20);
        nombreTF = new JTextField(20);
        apellidosTF = new JTextField(20);
        telefonoTF = new JTextField(20);
        emailTF = new JTextField(20);
        provinciaCombo = new JComboBox<ComboItem<Provincia>>();
        municipioCombo = new JComboBox<ComboItem<Municipio>>();
        FilterableComboBoxSupport.decorate(provinciaCombo);
        FilterableComboBoxSupport.decorate(municipioCombo);

        addField(formPanel, row++, "Codigo:", codigoTF);
        addField(formPanel, row++, "DNI:", dniTF);
        addField(formPanel, row++, "Nombre:", nombreTF);
        addField(formPanel, row++, "Apellidos:", apellidosTF);
        addField(formPanel, row++, "Telefono:", telefonoTF);
        addField(formPanel, row++, "Email:", emailTF);
        addField(formPanel, row++, "Provincia:", provinciaCombo);
        addField(formPanel, row++, "Municipio:", municipioCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        agreeButton = new JButton("Guardar");
        agreeButton.setIcon(new ImageIcon(VeterinarioCreateView.class.getResource("/animaltrack/icons/32/save.png")));
        buttonPanel.add(agreeButton);

        reloadButton = new JButton("Recargar");
        reloadButton.setIcon(new ImageIcon(VeterinarioCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.addActionListener(e -> resetForm());
        buttonPanel.add(reloadButton);

        add(buttonPanel, BorderLayout.SOUTH);

        provinciaCombo.addActionListener(e -> reloadMunicipios());
    }

    private void initServices() {
        provinciaService = new ProvinciaServiceImpl();
        municipioService = new MunicipioServiceImpl();
        veterinarioService = new VeterinarioServiceImpl();
    }

    private void loadInitialData() {
        try {
            setModel(provinciaCombo, provinciaService.findAll(), true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setModel(provinciaCombo, null, true);
        }
        reloadMunicipios();
    }

    private void reloadMunicipios() {
        ComboItem<Provincia> provinciaItem = getSelectedItem(provinciaCombo);
        if (provinciaItem == null || provinciaItem.getValue() == null) {
            setModel(municipioCombo, null, true);
            return;
        }
        try {
            setModel(municipioCombo, municipioService.findByProvinciaId(provinciaItem.getValue().getId()), true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setModel(municipioCombo, null, true);
        }
    }

    public boolean createVeterinario() {
        try {
            Veterinario veterinario = buildVeterinario();
            VeterinarioDTO created = veterinarioService.create(veterinario);
            if (created == null || created.getId() == null) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el veterinario. Revisa los datos introducidos.",
                        "Validacion", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            JOptionPane.showMessageDialog(this, "Veterinario guardado con ID " + created.getId(), "Correcto",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateVeterinario() {
        try {
            Veterinario veterinario = buildVeterinario();
            if (veterinario.getId() == null) {
                JOptionPane.showMessageDialog(this, "No hay ningun veterinario cargado para actualizar.", "Validacion",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            veterinarioService.update(veterinario);
            VeterinarioDTO updated = veterinarioService.findById(veterinario.getId());
            if (updated != null) {
                setModel(updated);
            }
            JOptionPane.showMessageDialog(this, "Veterinario actualizado correctamente.", "Correcto",
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
        codigoTF.setEditable(editable);
        dniTF.setEditable(editable);
        nombreTF.setEditable(editable);
        apellidosTF.setEditable(editable);
        telefonoTF.setEditable(editable);
        emailTF.setEditable(editable);
        provinciaCombo.setEnabled(editable);
        municipioCombo.setEnabled(editable);
        configureSecondaryButton(editable);
    }

    public void setModel(VeterinarioDTO veterinario) {
        if (veterinario == null) {
            clearForm();
            return;
        }

        setName("Detalle Veterinario");
        idTF.setText(veterinario.getId() == null ? "" : String.valueOf(veterinario.getId()));
        codigoTF.setText(defaultString(veterinario.getCodigo()));
        dniTF.setText(defaultString(veterinario.getDni()));
        nombreTF.setText(defaultString(veterinario.getNombre()));
        apellidosTF.setText(defaultString(veterinario.getApellidos()));
        telefonoTF.setText(defaultString(veterinario.getTelefono()));
        emailTF.setText(defaultString(veterinario.getEmail()));
        selectComboItem(provinciaCombo, veterinario.getProvinciaId());
        reloadMunicipios();
        selectComboItem(municipioCombo, veterinario.getMunicipioId());
    }

    private Veterinario buildVeterinario() {
        ComboItem<Municipio> municipioItem = getSelectedItem(municipioCombo);

        Veterinario veterinario = new Veterinario();
        veterinario.setId(parseLong(idTF.getText()));
        veterinario.setCodigo(trimToNull(codigoTF.getText()));
        veterinario.setDni(trimToNull(dniTF.getText()));
        veterinario.setNombre(trimToNull(nombreTF.getText()));
        veterinario.setApellidos(trimToNull(apellidosTF.getText()));
        veterinario.setTelefono(trimToNull(telefonoTF.getText()));
        veterinario.setEmail(trimToNull(emailTF.getText()));
        veterinario.setMunicipioId(municipioItem == null || municipioItem.getValue() == null ? null : municipioItem.getValue().getId());
        return veterinario;
    }

    private void clearForm() {
        setName("Nuevo Veterinario");
        idTF.setText("");
        codigoTF.setText("");
        dniTF.setText("");
        nombreTF.setText("");
        apellidosTF.setText("");
        telefonoTF.setText("");
        emailTF.setText("");
        clearComboSelection(provinciaCombo);
        reloadMunicipios();
        configureSecondaryButton(true);
    }

    private void resetForm() {
        clearForm();
        loadInitialData();
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
        if (value instanceof Provincia) {
            return ((Provincia) value).getNombre();
        }
        if (value instanceof Municipio) {
            return ((Municipio) value).getNombre();
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
        if (value instanceof Provincia) {
            return ((Provincia) value).getId();
        }
        if (value instanceof Municipio) {
            return ((Municipio) value).getId();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = combo.getSelectedItem();
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

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private Long parseLong(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : Long.valueOf(trimmed);
    }

    private void configureSecondaryButton(boolean editable) {
        for (java.awt.event.ActionListener listener : reloadButton.getActionListeners()) {
            reloadButton.removeActionListener(listener);
        }

        if (editable && parseLong(idTF.getText()) != null) {
            reloadButton.setText("Cancelar");
            reloadButton.setIcon(new ImageIcon(VeterinarioCreateView.class.getResource("/animaltrack/icons/32/cancel.png")));
            reloadButton.setEnabled(true);
            reloadButton.setAction(new CancelController(this));
            return;
        }

        reloadButton.setText("Recargar");
        reloadButton.setIcon(new ImageIcon(VeterinarioCreateView.class.getResource("/animaltrack/icons/32/refresh.png")));
        reloadButton.setEnabled(editable);
        reloadButton.addActionListener(e -> resetForm());
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
