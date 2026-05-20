package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTable;

import com.tonin.animaltrack.dao.criteria.SemillaCriteria;
import com.tonin.animaltrack.model.Raza;
import com.tonin.animaltrack.model.Semilla;
import com.tonin.animaltrack.service.RazaService;
import com.tonin.animaltrack.service.SemillaService;
import com.tonin.animaltrack.service.impl.RazaServiceImpl;
import com.tonin.animaltrack.service.impl.SemillaServiceImpl;

public class SemillaView extends AbstractView {

    private static final long serialVersionUID = 1L;
    private static final String NO_MATCHES_MESSAGE = "No hay coincidencias con el filtro de busqueda.";
    private static Logger logger = LogManager.getLogger(SemillaView.class.getName());

    private final SemillaService semillaService;
    private final RazaService razaService;
    private List<Semilla> model;

    private JXSearchField codigoSearchTF;
    private JXSearchField nombreSearchTF;
    private JComboBox<ComboItem<Raza>> razaSearchCB;
    private JXTable table;
    private JLabel totalResultadosLabel;

    private JPanel editPanel;
    private JTextField idTF;
    private JTextField codigoTF;
    private JTextField nombreTF;
    private JTextField descripcionTF;
    private JComboBox<ComboItem<Raza>> razaEditCB;
    private JButton guardarButton;
    private JButton borrarButton;
    private JButton cancelarButton;

    public SemillaView() {
        this.semillaService = new SemillaServiceImpl();
        this.razaService = new RazaServiceImpl();
        this.model = Collections.emptyList();

        initialize();
        loadRazaCombos();
        postInitialize();
        buscar();
    }

    private void initialize() {
        setName("Semillas");
        setLayout(new BorderLayout(0, 0));

        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        add(contentPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.add(topPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        topPanel.add(searchPanel, BorderLayout.NORTH);

        searchPanel.add(new JLabel("Codigo:"));
        codigoSearchTF = new JXSearchField("Codigo");
        codigoSearchTF.setColumns(14);
        searchPanel.add(codigoSearchTF);

        searchPanel.add(new JLabel("Nombre:"));
        nombreSearchTF = new JXSearchField("Nombre");
        nombreSearchTF.setColumns(18);
        searchPanel.add(nombreSearchTF);

        searchPanel.add(new JLabel("Raza:"));
        razaSearchCB = new JComboBox<ComboItem<Raza>>();
        searchPanel.add(razaSearchCB);

        JButton buscarButton = new JButton("Buscar", icon("/animaltrack/icons/32/search.png"));
        buscarButton.addActionListener(e -> buscar());
        searchPanel.add(buscarButton);

        editPanel = new JPanel(new GridBagLayout());
        topPanel.add(editPanel, BorderLayout.CENTER);

        idTF = new JTextField(20);
        idTF.setEditable(false);
        idTF.setVisible(false);
        codigoTF = new JTextField(20);
        nombreTF = new JTextField(24);
        descripcionTF = new JTextField(36);
        razaEditCB = new JComboBox<ComboItem<Raza>>();

        addField(editPanel, 0, "Codigo:", codigoTF);
        addField(editPanel, 1, "Nombre:", nombreTF);
        addField(editPanel, 2, "Raza:", razaEditCB);
        addField(editPanel, 3, "Descripcion:", descripcionTF);

        table = new JXTable();
        table.setColumnControlVisible(true);
        table.setSortable(true);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton crearButton = new JButton("Crear Nueva", icon("/animaltrack/icons/32/add-new.png"));
        crearButton.addActionListener(e -> startCreate());
        buttonPanel.add(crearButton);

        guardarButton = new JButton("Guardar", icon("/animaltrack/icons/32/save.png"));
        guardarButton.addActionListener(e -> save());
        buttonPanel.add(guardarButton);

        borrarButton = new JButton("Borrar", icon("/animaltrack/icons/32/delete.png"));
        borrarButton.addActionListener(e -> deleteSelected());
        buttonPanel.add(borrarButton);

        cancelarButton = new JButton("Cancelar", icon("/animaltrack/icons/32/cancel.png"));
        cancelarButton.addActionListener(e -> showSearchMode());
        buttonPanel.add(cancelarButton);

        JButton recargarButton = new JButton("Recargar", icon("/animaltrack/icons/32/refresh.png"));
        recargarButton.addActionListener(e -> buscar());
        buttonPanel.add(recargarButton);

        totalResultadosLabel = new JLabel("Total: 0 resultados");
        buttonPanel.add(totalResultadosLabel);

        showSearchMode();
    }

    private void postInitialize() {
        FilterableComboBoxSupport.decorate(razaSearchCB);
        FilterableComboBoxSupport.decorate(razaEditCB);

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar();
            }
        };
        codigoSearchTF.getDocument().addDocumentListener(listener);
        nombreSearchTF.getDocument().addDocumentListener(listener);
        razaSearchCB.addItemListener(e -> buscar());

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedIntoForm();
                }
            }
        });
    }

    private void loadRazaCombos() {
        DefaultComboBoxModel<ComboItem<Raza>> searchModel = new DefaultComboBoxModel<ComboItem<Raza>>();
        DefaultComboBoxModel<ComboItem<Raza>> editModel = new DefaultComboBoxModel<ComboItem<Raza>>();
        try {
            List<Raza> razas = razaService.findAll();
            if (razas != null) {
                for (Raza raza : razas) {
                    searchModel.addElement(new ComboItem<Raza>(raza, raza.getNombre()));
                    editModel.addElement(new ComboItem<Raza>(raza, raza.getNombre()));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
        razaSearchCB.setModel(searchModel);
        razaEditCB.setModel(editModel);
        clearComboSelection(razaSearchCB);
        clearComboSelection(razaEditCB);
    }

    private void buscar() {
        try {
            SemillaCriteria criteria = new SemillaCriteria();
            criteria.setCodigoLike(StringUtils.trimToNull(codigoSearchTF.getText()));
            criteria.setNombreLike(StringUtils.trimToNull(nombreSearchTF.getText()));
            ComboItem<Raza> razaItem = getSelectedItem(razaSearchCB);
            criteria.setRazaId(razaItem == null || razaItem.getValue() == null ? null : razaItem.getValue().getId());
            model = semillaService.findByCriteria(criteria);
            updateView();
            showSearchMode();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateView() {
        List<Semilla> rows = model == null ? Collections.<Semilla>emptyList() : model;
        DefaultTableModel tableModel = new DefaultTableModel(
                new Object[] { "ID", "Codigo", "Nombre", "Raza", "Descripcion" }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (rows.isEmpty()) {
            tableModel.addRow(new Object[] { null, NO_MATCHES_MESSAGE, null, null, null });
        } else {
            for (Semilla semilla : rows) {
                tableModel.addRow(new Object[] {
                        semilla.getId(),
                        semilla.getCodigo(),
                        semilla.getNombre(),
                        semilla.getRazaNombre(),
                        semilla.getDescripcion()
                });
            }
        }
        table.setModel(tableModel);
        table.getColumnExt("ID").setVisible(false);
        totalResultadosLabel.setText("Total: " + rows.size() + " resultados");
    }

    private void startCreate() {
        clearForm();
        codigoTF.setText(defaultString(StringUtils.trimToNull(codigoSearchTF.getText())));
        nombreTF.setText(defaultString(StringUtils.trimToNull(nombreSearchTF.getText())));
        ComboItem<Raza> razaItem = getSelectedItem(razaSearchCB);
        if (razaItem != null && razaItem.getValue() != null) {
            selectComboItem(razaEditCB, razaItem.getValue().getId());
        }
        showEditMode(false);
    }

    private void save() {
        try {
            Semilla semilla = new Semilla();
            semilla.setId(parseLong(idTF.getText()));
            semilla.setCodigo(StringUtils.trimToNull(codigoTF.getText()));
            semilla.setNombre(StringUtils.trimToNull(nombreTF.getText()));
            semilla.setDescripcion(StringUtils.trimToNull(descripcionTF.getText()));
            ComboItem<Raza> razaItem = getSelectedItem(razaEditCB);
            semilla.setRazaId(razaItem == null || razaItem.getValue() == null ? null : razaItem.getValue().getId());

            if (semilla.getId() == null) {
                semillaService.create(semilla);
            } else {
                semillaService.update(semilla);
            }
            JOptionPane.showMessageDialog(this, "Semilla guardada correctamente.", "Semillas",
                    JOptionPane.INFORMATION_MESSAGE);
            buscar();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteSelected() {
        Long id = parseLong(idTF.getText());
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Carga primero una semilla con doble click.", "Semillas",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String codigo = StringUtils.trimToNull(codigoTF.getText());
        String typed = JOptionPane.showInputDialog(this, "Para borrar escribe \"" + codigo + "\":", "Confirmar borrado",
                JOptionPane.WARNING_MESSAGE);
        if (!codigo.equals(typed)) {
            return;
        }
        try {
            semillaService.delete(id);
            buscar();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadSelectedIntoForm() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= (model == null ? 0 : model.size())) {
            return;
        }
        Semilla semilla = model.get(modelRow);
        idTF.setText(String.valueOf(semilla.getId()));
        codigoTF.setText(defaultString(semilla.getCodigo()));
        nombreTF.setText(defaultString(semilla.getNombre()));
        descripcionTF.setText(defaultString(semilla.getDescripcion()));
        selectComboItem(razaEditCB, semilla.getRazaId());
        showEditMode(true);
    }

    private void showSearchMode() {
        clearForm();
        editPanel.setVisible(false);
        guardarButton.setVisible(false);
        borrarButton.setVisible(false);
        cancelarButton.setVisible(false);
        revalidate();
        repaint();
    }

    private void showEditMode(boolean existingRecord) {
        editPanel.setVisible(true);
        guardarButton.setVisible(true);
        borrarButton.setVisible(existingRecord);
        cancelarButton.setVisible(true);
        revalidate();
        repaint();
    }

    private void clearForm() {
        idTF.setText("");
        codigoTF.setText("");
        nombreTF.setText("");
        descripcionTF.setText("");
        clearComboSelection(razaEditCB);
    }

    private void addField(JPanel panel, int row, String label, java.awt.Component component) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        labelConstraints.insets = new Insets(6, 6, 6, 6);
        labelConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(6, 6, 6, 6);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, fieldConstraints);
    }

    private ImageIcon icon(String path) {
        return new ImageIcon(SemillaView.class.getResource(path));
    }

    private <T> void clearComboSelection(JComboBox<ComboItem<T>> combo) {
        combo.setSelectedIndex(-1);
        if (combo.isEditable()) {
            combo.getEditor().setItem("");
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = FilterableComboBoxSupport.getSelectedItem(combo);
        if (!(selectedItem instanceof ComboItem)) {
            return null;
        }
        return (ComboItem<T>) selectedItem;
    }

    private <T> void selectComboItem(JComboBox<ComboItem<T>> combo, Long id) {
        for (int i = 0; i < combo.getModel().getSize(); i++) {
            ComboItem<T> item = combo.getModel().getElementAt(i);
            if (item != null && item.getValue() instanceof Raza && id != null
                    && id.equals(((Raza) item.getValue()).getId())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
        clearComboSelection(combo);
    }

    private Long parseLong(String value) {
        String trimmed = StringUtils.trimToNull(value);
        return trimmed == null || "null".equalsIgnoreCase(trimmed) ? null : Long.valueOf(trimmed);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private void showError(Exception ex) {
        logger.error(rootMessage(ex), ex);
        JOptionPane.showMessageDialog(this, rootMessage(ex), "Semillas", JOptionPane.ERROR_MESSAGE);
    }

    private String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.toString() : current.getMessage();
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
