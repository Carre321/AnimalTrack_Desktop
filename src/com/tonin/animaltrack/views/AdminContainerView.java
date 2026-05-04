package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTable;

import com.tonin.animaltrack.model.AnimalSemilla;
import com.tonin.animaltrack.model.Dosis;
import com.tonin.animaltrack.model.Ganadero;
import com.tonin.animaltrack.model.Granja;
import com.tonin.animaltrack.model.Notificacion;
import com.tonin.animaltrack.model.Semilla;
import com.tonin.animaltrack.model.Tratamiento;
import com.tonin.animaltrack.model.Veterinario;
import com.tonin.animaltrack.model.VeterinarioGranja;
import com.tonin.animaltrack.model.dto.GanaderoDTO;
import com.tonin.animaltrack.model.dto.GranjaDTO;
import com.tonin.animaltrack.model.dto.NotificacionDTO;
import com.tonin.animaltrack.model.dto.UsuarioLoginDTO;
import com.tonin.animaltrack.service.AnimalSemillaService;
import com.tonin.animaltrack.service.DosisService;
import com.tonin.animaltrack.service.GanaderoService;
import com.tonin.animaltrack.service.GranjaService;
import com.tonin.animaltrack.service.MunicipioService;
import com.tonin.animaltrack.service.NotificacionService;
import com.tonin.animaltrack.service.ProvinciaService;
import com.tonin.animaltrack.service.RazaService;
import com.tonin.animaltrack.service.SemillaService;
import com.tonin.animaltrack.service.SexoService;
import com.tonin.animaltrack.service.TipoEventoService;
import com.tonin.animaltrack.service.TipoNotificacionService;
import com.tonin.animaltrack.service.TratamientoService;
import com.tonin.animaltrack.service.VeterinarioGranjaService;
import com.tonin.animaltrack.service.impl.AnimalSemillaServiceImpl;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.service.impl.DosisServiceImpl;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.service.impl.GanaderoServiceImpl;
import com.tonin.animaltrack.service.impl.GranjaServiceImpl;
import com.tonin.animaltrack.service.impl.MunicipioServiceImpl;
import com.tonin.animaltrack.service.impl.NotificacionServiceImpl;
import com.tonin.animaltrack.service.impl.ProvinciaServiceImpl;
import com.tonin.animaltrack.service.impl.RazaServiceImpl;
import com.tonin.animaltrack.service.impl.SemillaServiceImpl;
import com.tonin.animaltrack.service.impl.SexoServiceImpl;
import com.tonin.animaltrack.service.impl.TipoEventoServiceImpl;
import com.tonin.animaltrack.service.impl.TipoNotificacionServiceImpl;
import com.tonin.animaltrack.service.impl.TratamientoServiceImpl;
import com.tonin.animaltrack.service.impl.UsuarioLoginServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioGranjaServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;

public class AdminContainerView extends View {

    private static final long serialVersionUID = 1L;

    public AdminContainerView() {
        setName("Administracion");
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        add(tabs, BorderLayout.CENTER);

        tabs.addTab("Ganaderos", new CrudPanel<Ganadero>(
                "Ganaderos", new GanaderoServiceImpl(), Ganadero.class,
                fields("id", "dni", "nombre", "apellidos", "telefono", "email", "municipioId"),
                labels("ID", "DNI", "Nombre", "Apellidos", "Telefono", "Email", "Municipio")));
        tabs.addTab("Veterinarios", new CrudPanel<Veterinario>(
                "Veterinarios", new VeterinarioServiceImpl(), Veterinario.class,
                fields("id", "codigo", "dni", "nombre", "apellidos", "telefono", "email", "municipioId"),
                labels("ID", "Codigo", "DNI", "Nombre", "Apellidos", "Telefono", "Email", "Municipio")));
        tabs.addTab("Granjas", new CrudPanel<Granja>(
                "Granjas", new GranjaServiceImpl(), Granja.class,
                fields("id", "nombre", "direccion", "municipioId", "ganaderoId"),
                labels("ID", "Nombre", "Direccion", "Municipio", "Ganadero")));
        tabs.addTab("Semillas", new CrudPanel<Semilla>(
                "Semillas", new SemillaServiceImpl(), Semilla.class,
                fields("id", "codigo", "descripcion"),
                labels("ID", "Codigo", "Descripcion")));
        tabs.addTab("Tratamientos", new CrudPanel<Tratamiento>(
                "Tratamientos", new TratamientoServiceImpl(), Tratamiento.class,
                fields("id", "nombre"),
                labels("ID", "Nombre")));
        tabs.addTab("Dosis", new CrudPanel<Dosis>(
                "Dosis", new DosisServiceImpl(), Dosis.class,
                fields("id", "plazoSiguiente", "numOrdenDosis", "tratamientoId"),
                labels("ID", "Plazo siguiente", "Orden dosis", "Tratamiento")));
        tabs.addTab("Notificaciones", new CrudPanel<Notificacion>(
                "Notificaciones", new NotificacionServiceImpl(), Notificacion.class,
                fields("id", "eventoId", "tipo", "fechaEmision", "descripcion", "tipoNotificacionId"),
                labels("ID", "Evento", "Tipo", "Fecha emision", "Descripcion", "Tipo notif.")));
        tabs.addTab("Usuarios", new CrudPanel<UsuarioLoginDTO>(
                "Usuarios", new UsuarioLoginServiceImpl(), UsuarioLoginDTO.class,
                fields("id", "email", "passwordHash", "rol", "ganaderoId", "veterinarioId", "activo"),
                labels("ID", "Email", "Password/Hash", "Rol", "Ganadero", "Veterinario", "Activo")));
        tabs.addTab("Relaciones", new RelationsPanel());
        tabs.addTab("Maestras", new MastersPanel());
    }

    private static List<String> fields(String... values) {
        return Arrays.asList(values);
    }

    private static List<String> labels(String... values) {
        return Arrays.asList(values);
    }

    private static class CrudPanel<T> extends JPanel {

        private static final long serialVersionUID = 1L;

        private final Object service;
        private final Class<T> entityClass;
        private final List<String> fields;
        private final List<String> labels;
        private final List<JComponent> inputs;
        private final JXTable table;
        private final JXSearchField filterTF;
        private List<Object> rows;

        CrudPanel(String title, Object service, Class<T> entityClass, List<String> fields, List<String> labels) {
            this.service = service;
            this.entityClass = entityClass;
            this.fields = fields;
            this.labels = labels;
            this.inputs = new ArrayList<JComponent>();
            this.rows = Collections.emptyList();

            setLayout(new BorderLayout(0, 0));

            JPanel formPanel = new JPanel(new GridBagLayout());
            add(formPanel, BorderLayout.NORTH);

            int visibleRow = 0;
            for (int i = 0; i < fields.size(); i++) {
                JComponent input = createEditor(fields.get(i));
                inputs.add(input);
                if (!"id".equals(fields.get(i))) {
                    addField(formPanel, visibleRow++, labels.get(i) + ":", input);
                }
            }

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            add(buttonPanel, BorderLayout.SOUTH);

            JButton nuevoButton = new JButton("Nuevo", icon("/nuvola/32x32/1727_add_add.png"));
            nuevoButton.addActionListener(e -> clearForm());
            buttonPanel.add(nuevoButton);

            JButton guardarButton = new JButton("Guardar", icon("/nuvola/32x32/1847_save_guardar_disk.png"));
            guardarButton.addActionListener(e -> save());
            buttonPanel.add(guardarButton);

            JButton borrarButton = new JButton("Borrar", icon("/nuvola/32x32/1250_delete_delete.png"));
            borrarButton.addActionListener(e -> deleteSelected());
            buttonPanel.add(borrarButton);

            JButton recargarButton = new JButton("Recargar", icon("/nuvola/32x32/1839_all_all_refresh_reload_sync_tabs_refresh_reload_sync_tabs.png"));
            recargarButton.addActionListener(e -> reload());
            buttonPanel.add(recargarButton);

            filterTF = new JXSearchField("Filtrar " + title);
            filterTF.setColumns(22);
            filterTF.addActionListener(e -> updateTable());
            filterTF.addCaretListener(e -> updateTable());
            buttonPanel.add(filterTF);

            table = new JXTable();
            table.setColumnControlVisible(true);
            table.setSortable(true);
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        loadSelectedIntoForm();
                    }
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);
            reload();
        }

        private void reload() {
            try {
                Object result = service.getClass().getMethod("findAll").invoke(service);
                rows = result instanceof List ? new ArrayList<Object>((List<?>) result) : Collections.emptyList();
                updateTable();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void updateTable() {
            String filter = normalize(filterTF.getText());
            DefaultTableModel model = new DefaultTableModel(labels.toArray(), 0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Object row : rows) {
                Object[] values = new Object[fields.size()];
                StringBuilder searchable = new StringBuilder();
                for (int i = 0; i < fields.size(); i++) {
                    Object value = get(row, fields.get(i));
                    value = displayValue(fields.get(i), value);
                    values[i] = value;
                    searchable.append(value).append(' ');
                }
                if (filter == null || normalize(searchable.toString()).contains(filter)) {
                    model.addRow(values);
                }
            }
            table.setModel(model);
            hideColumn("ID");
        }

        private void hideColumn(String identifier) {
            try {
                table.getColumnExt(identifier).setVisible(false);
            } catch (IllegalArgumentException ex) {
                // The column can be absent while the table model is being replaced.
            }
        }

        private void save() {
            try {
                T entity = entityClass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < fields.size(); i++) {
                    set(entity, fields.get(i), readEditorValue(inputs.get(i)));
                }

                Long id = parseLong(readEditorValue(inputs.get(0)));
                if (id == null) {
                    service.getClass().getMethod("create", entityClass).invoke(service, entity);
                } else {
                    service.getClass().getMethod("update", entityClass).invoke(service, entity);
                }
                clearForm();
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void deleteSelected() {
            Long id = parseLong(readEditorValue(inputs.get(0)));
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Carga primero un registro con doble click.", "Borrar",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int option = JOptionPane.showConfirmDialog(this, "Borrar registro " + id + "?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                service.getClass().getMethod("delete", Long.class).invoke(service, id);
                clearForm();
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void loadSelectedIntoForm() {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            Long id = parseLong(String.valueOf(table.getModel().getValueAt(table.convertRowIndexToModel(row), 0)));
            if (id == null) {
                return;
            }
            try {
                Object selected = service.getClass().getMethod("findById", Long.class).invoke(service, id);
                for (int i = 0; i < fields.size(); i++) {
                    Object value = get(selected, fields.get(i));
                    writeEditorValue(inputs.get(i), value);
                }
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void clearForm() {
            for (JComponent input : inputs) {
                writeEditorValue(input, null);
            }
        }

        private Object get(Object target, String field) {
            if (target == null) {
                return null;
            }
            try {
                Method method = target.getClass().getMethod("get" + capitalize(field));
                return method.invoke(target);
            } catch (Exception ex) {
                return null;
            }
        }

        private void set(Object target, String field, String value) throws Exception {
            Method setter = findSetter(target.getClass(), field);
            if (setter == null) {
                return;
            }
            setter.invoke(target, convert(value, setter.getParameterTypes()[0]));
        }

        private Method findSetter(Class<?> type, String field) {
            String name = "set" + capitalize(field);
            for (Method method : type.getMethods()) {
                if (method.getName().equals(name) && method.getParameterTypes().length == 1) {
                    return method;
                }
            }
            return null;
        }

        private JComponent createEditor(String field) {
            if ("id".equals(field)) {
                return new JTextField(16);
            }
            if ("rol".equals(field)) {
                JComboBox<LookupItem> combo = new JComboBox<LookupItem>();
                combo.setModel(new DefaultComboBoxModel<LookupItem>(new LookupItem[] {
                        new LookupItem(null, null, ""),
                        new LookupItem(null, "ADMINISTRADOR", "Administrador"),
                        new LookupItem(null, "GANADERO", "Ganadero"),
                        new LookupItem(null, "VETERINARIO", "Veterinario")
                }));
                FilterableComboBoxSupport.decorate(combo);
                return combo;
            }
            if ("activo".equals(field)) {
                JComboBox<LookupItem> combo = new JComboBox<LookupItem>();
                combo.setModel(new DefaultComboBoxModel<LookupItem>(new LookupItem[] {
                        new LookupItem(null, null, ""),
                        new LookupItem(null, "true", "Si"),
                        new LookupItem(null, "false", "No")
                }));
                return combo;
            }
            Object lookupService = lookupServiceFor(field);
            if (lookupService != null) {
                JComboBox<LookupItem> combo = new JComboBox<LookupItem>();
                combo.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(lookupService, true)));
                FilterableComboBoxSupport.decorate(combo);
                return combo;
            }
            return new JTextField(16);
        }

        private String readEditorValue(JComponent input) {
            if (input instanceof JTextField) {
                return ((JTextField) input).getText();
            }
            if (input instanceof JComboBox) {
                Object selected = ((JComboBox<?>) input).getSelectedItem();
                return selected instanceof LookupItem ? ((LookupItem) selected).getValue() : null;
            }
            return null;
        }

        private void writeEditorValue(JComponent input, Object value) {
            String text = value == null ? null : String.valueOf(value);
            if (input instanceof JTextField) {
                ((JTextField) input).setText(text == null ? "" : text);
            } else if (input instanceof JComboBox) {
                selectLookupItem((JComboBox<?>) input, text);
            }
        }

        private Object displayValue(String field, Object value) {
            if (value == null) {
                return null;
            }
            JComponent input = inputs.get(fields.indexOf(field));
            if (input instanceof JComboBox) {
                LookupItem item = findLookupItem((JComboBox<?>) input, String.valueOf(value));
                return item == null ? value : item.toString();
            }
            return value;
        }
    }

    private static class RelationsPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        RelationsPanel() {
            setLayout(new BorderLayout(0, 0));
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Veterinario-Granja", new RelationEditorPanel("Veterinario", "Granja",
                    new VeterinarioGranjaServiceImpl(), new VeterinarioServiceImpl(), new GranjaServiceImpl(), true));
            tabs.addTab("Animal-Semilla", new RelationEditorPanel("Animal", "Semilla",
                    new AnimalSemillaServiceImpl(), new AnimalServiceImpl(), new SemillaServiceImpl(), false));
            add(tabs, BorderLayout.CENTER);
        }
    }

    private static class RelationEditorPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final JComboBox<LookupItem> firstCB;
        private final JComboBox<LookupItem> secondCB;
        private final Object service;
        private final boolean veterinarioGranja;
        private final JXTable table;

        RelationEditorPanel(String firstLabel, String secondLabel, Object service, Object firstLookupService,
                Object secondLookupService, boolean veterinarioGranja) {
            this.service = service;
            this.veterinarioGranja = veterinarioGranja;
            setLayout(new BorderLayout(0, 0));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            add(top, BorderLayout.NORTH);
            firstCB = new JComboBox<LookupItem>();
            firstCB.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(firstLookupService, true)));
            FilterableComboBoxSupport.decorate(firstCB);
            secondCB = new JComboBox<LookupItem>();
            secondCB.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(secondLookupService, true)));
            FilterableComboBoxSupport.decorate(secondCB);
            top.add(new JLabel(firstLabel + ":"));
            top.add(firstCB);
            top.add(new JLabel(secondLabel + ":"));
            top.add(secondCB);

            JButton guardarButton = new JButton("Guardar", icon("/nuvola/32x32/1847_save_guardar_disk.png"));
            guardarButton.addActionListener(e -> save());
            top.add(guardarButton);
            JButton borrarButton = new JButton("Borrar", icon("/nuvola/32x32/1250_delete_delete.png"));
            borrarButton.addActionListener(e -> delete());
            top.add(borrarButton);
            JButton buscarButton = new JButton("Buscar por primero", icon("/nuvola/32x32/1746_find_find.png"));
            buscarButton.addActionListener(e -> reload());
            top.add(buscarButton);

            table = new JXTable();
            table.setColumnControlVisible(true);
            table.setSortable(true);
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                        int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                        selectLookupItem(firstCB, String.valueOf(table.getModel().getValueAt(modelRow, 0)));
                        selectLookupItem(secondCB, String.valueOf(table.getModel().getValueAt(modelRow, 2)));
                    }
                }
            });
            add(new JScrollPane(table), BorderLayout.CENTER);
            updateTable(Collections.emptyList());
        }

        private void save() {
            try {
                Long first = selectedId(firstCB);
                Long second = selectedId(secondCB);
                if (veterinarioGranja) {
                    VeterinarioGranja value = new VeterinarioGranja();
                    value.setVeterinarioId(first);
                    value.setGranjaId(second);
                    ((VeterinarioGranjaService) service).create(value);
                } else {
                    AnimalSemilla value = new AnimalSemilla();
                    value.setAnimalId(first);
                    value.setSemillaId(second);
                    ((AnimalSemillaService) service).create(value);
                }
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void delete() {
            try {
                Long first = selectedId(firstCB);
                Long second = selectedId(secondCB);
                if (veterinarioGranja) {
                    ((VeterinarioGranjaService) service).delete(first, second);
                } else {
                    ((AnimalSemillaService) service).delete(first, second);
                }
                selectLookupItem(firstCB, null);
                selectLookupItem(secondCB, null);
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void reload() {
            try {
                Long first = selectedId(firstCB);
                List<?> rows = veterinarioGranja
                        ? ((VeterinarioGranjaService) service).findByVeterinarioId(first)
                        : ((AnimalSemillaService) service).findByAnimalId(first);
                updateTable(rows == null ? Collections.emptyList() : rows);
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void updateTable(List<?> rows) {
            DefaultTableModel model = new DefaultTableModel(new Object[] { "Primero ID", "Primero", "Segundo ID", "Segundo" }, 0);
            for (Object row : rows) {
                if (row instanceof VeterinarioGranja) {
                    VeterinarioGranja value = (VeterinarioGranja) row;
                    model.addRow(new Object[] { value.getVeterinarioId(), displayComboValue(firstCB, value.getVeterinarioId()),
                            value.getGranjaId(), displayComboValue(secondCB, value.getGranjaId()) });
                } else if (row instanceof AnimalSemilla) {
                    AnimalSemilla value = (AnimalSemilla) row;
                    model.addRow(new Object[] { value.getAnimalId(), displayComboValue(firstCB, value.getAnimalId()),
                            value.getSemillaId(), displayComboValue(secondCB, value.getSemillaId()) });
                }
            }
            table.setModel(model);
            table.getColumnExt("Primero ID").setVisible(false);
            table.getColumnExt("Segundo ID").setVisible(false);
        }
    }

    private static class MastersPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        MastersPanel() {
            setLayout(new BorderLayout(0, 0));
            JComboBox<ComboItem> selector = new JComboBox<ComboItem>();
            selector.setModel(new DefaultComboBoxModel<ComboItem>(new ComboItem[] {
                    new ComboItem("Razas", new RazaServiceImpl()),
                    new ComboItem("Sexos", new SexoServiceImpl()),
                    new ComboItem("Tipos evento", new TipoEventoServiceImpl()),
                    new ComboItem("Tipos notificacion", new TipoNotificacionServiceImpl()),
                    new ComboItem("Provincias", new ProvinciaServiceImpl()),
                    new ComboItem("Municipios", new MunicipioServiceImpl())
            }));
            FilterableComboBoxSupport.decorate(selector);
            add(selector, BorderLayout.NORTH);

            JXTable table = new JXTable();
            table.setColumnControlVisible(true);
            table.setSortable(true);
            add(new JScrollPane(table), BorderLayout.CENTER);

            Runnable reload = () -> {
                ComboItem item = (ComboItem) selector.getSelectedItem();
                if (item == null) {
                    return;
                }
                try {
                    List<?> rows = (List<?>) item.service.getClass().getMethod("findAll").invoke(item.service);
                    DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Nombre", "Extra" }, 0);
                    for (Object row : rows) {
                        model.addRow(new Object[] { invoke(row, "getId"), invoke(row, "getNombre"), invoke(row, "getProvinciaId") });
                    }
                    table.setModel(model);
                } catch (Exception ex) {
                    showError(ex);
                }
            };
            selector.addActionListener(e -> reload.run());
            reload.run();
        }

        private static class ComboItem {
            private final String label;
            private final Object service;

            ComboItem(String label, Object service) {
                this.label = label;
                this.service = service;
            }

            @Override
            public String toString() {
                return label;
            }
        }
    }

    private static void addField(JPanel panel, int row, String label, Component component) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        labelConstraints.insets = new Insets(4, 6, 4, 6);
        labelConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.insets = new Insets(4, 6, 4, 6);
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, fieldConstraints);
    }

    private static ImageIcon icon(String path) {
        return new ImageIcon(AdminContainerView.class.getResource(path));
    }

    private static Object lookupServiceFor(String field) {
        if ("municipioId".equals(field)) {
            return new MunicipioServiceImpl();
        }
        if ("ganaderoId".equals(field)) {
            return new GanaderoServiceImpl();
        }
        if ("veterinarioId".equals(field)) {
            return new VeterinarioServiceImpl();
        }
        if ("tratamientoId".equals(field)) {
            return new TratamientoServiceImpl();
        }
        if ("tipoNotificacionId".equals(field)) {
            return new TipoNotificacionServiceImpl();
        }
        if ("eventoId".equals(field)) {
            return new EventoServiceImpl();
        }
        if ("animalId".equals(field)) {
            return new AnimalServiceImpl();
        }
        if ("semillaId".equals(field)) {
            return new SemillaServiceImpl();
        }
        return null;
    }

    private static LookupItem[] lookupItems(Object service, boolean emptyFirst) {
        List<LookupItem> items = new ArrayList<LookupItem>();
        if (emptyFirst) {
            items.add(new LookupItem(null, null, ""));
        }
        try {
            List<?> rows = (List<?>) service.getClass().getMethod("findAll").invoke(service);
            for (Object row : rows) {
                Object id = invoke(row, "getId");
                if (id != null) {
                    String value = String.valueOf(id);
                    items.add(new LookupItem((Long) convert(value, Long.class), value, describe(row)));
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return items.toArray(new LookupItem[items.size()]);
    }

    private static LookupItem findLookupItem(JComboBox<?> combo, String value) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof LookupItem) {
                LookupItem lookup = (LookupItem) item;
                if (value == null ? lookup.getValue() == null : value.equals(lookup.getValue())) {
                    return lookup;
                }
            }
        }
        return null;
    }

    private static void selectLookupItem(JComboBox<?> combo, String value) {
        LookupItem item = findLookupItem(combo, value);
        combo.setSelectedItem(item == null && combo.getItemCount() > 0 ? combo.getItemAt(0) : item);
    }

    private static Long selectedId(JComboBox<LookupItem> combo) {
        Object selected = combo.getSelectedItem();
        return selected instanceof LookupItem ? ((LookupItem) selected).getId() : null;
    }

    private static String displayComboValue(JComboBox<LookupItem> combo, Long id) {
        LookupItem item = findLookupItem(combo, id == null ? null : String.valueOf(id));
        return item == null ? "" : item.toString();
    }

    private static String describe(Object row) {
        String nombreVisible = stringValue(invoke(row, "getNombreVisible"));
        if (nombreVisible != null) {
            return nombreVisible;
        }
        String nombreCompleto = stringValue(invoke(row, "getNombreCompleto"));
        if (nombreCompleto != null) {
            return nombreCompleto;
        }
        String crotal = stringValue(invoke(row, "getCrotal"));
        String tipoEvento = stringValue(invoke(row, "getTipoEventoNombre"));
        if (crotal != null && tipoEvento != null) {
            return crotal + " - " + tipoEvento + " - " + invoke(row, "getFechaHora");
        }
        String codigo = stringValue(invoke(row, "getCodigo"));
        String descripcion = stringValue(invoke(row, "getDescripcion"));
        if (codigo != null && descripcion != null) {
            return codigo + " - " + descripcion;
        }
        if (codigo != null) {
            return codigo;
        }
        String nombre = stringValue(invoke(row, "getNombre"));
        if (nombre != null) {
            return nombre;
        }
        Object id = invoke(row, "getId");
        return id == null ? "" : String.valueOf(id);
    }

    private static String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    private static class LookupItem {
        private final Long id;
        private final String value;
        private final String label;

        LookupItem(Long id, String value, String label) {
            this.id = id;
            this.value = value;
            this.label = label;
        }

        Long getId() {
            return id;
        }

        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static Object invoke(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (Exception ex) {
            return null;
        }
    }

    private static Object convert(String value, Class<?> type) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            return Long.valueOf(trimmed);
        }
        if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            return Integer.valueOf(trimmed);
        }
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            return "1".equals(trimmed) || "true".equalsIgnoreCase(trimmed) || "si".equalsIgnoreCase(trimmed);
        }
        if (LocalDateTime.class.equals(type)) {
            return LocalDateTime.parse(trimmed.replace(' ', 'T'));
        }
        return trimmed;
    }

    private static Long parseLong(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null || "null".equalsIgnoreCase(trimmed) ? null : Long.valueOf(trimmed);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalize(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : trimmed.toLowerCase();
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static void showError(Exception ex) {
        JOptionPane.showMessageDialog(null, rootMessage(ex), "Administracion", JOptionPane.ERROR_MESSAGE);
    }

    private static String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.toString() : current.getMessage();
    }
}
