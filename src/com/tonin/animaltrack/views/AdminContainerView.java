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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
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
import com.tonin.animaltrack.service.UsuarioLoginService;
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
import com.tonin.animaltrack.ui.MainWindow;

public class AdminContainerView extends AbstractView {

	private static Logger logger = LogManager.getLogger(AdminContainerView.class.getName());

    private static final long serialVersionUID = 1L;
    private static final String USER_TYPE_GANADERO = "Ganadero";
    private static final String USER_TYPE_VETERINARIO = "Veterinario";
    private static final String NO_MATCHES_MESSAGE = "No hay coincidencias con el filtro de búsqueda.";

    public AdminContainerView() {
        setName("Usuarios");
        setLayout(new BorderLayout(0, 0));
        if (!MainWindow.getInstance().getPermissions().canOpenAdmin()) {
            add(new JLabel("No tienes permisos para abrir Administración."), BorderLayout.CENTER);
            return;
        }

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        add(tabs, BorderLayout.CENTER);

        tabs.addTab("Usuarios", new UsuariosPersonasPanel());
        tabs.addTab("Granjas", new CrudPanel<Granja>(
                "Granjas", new GranjaServiceImpl(), Granja.class,
                fields("id", "rega", "nombre", "direccion", "codigoPostal", "municipioId"),
                labels("ID", "REGA", "Nombre", "Dirección", "CP", "Municipio")));
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
                labels("ID", "Evento", "Tipo", "Fecha emisión", "Descripción", "Tipo notif.")));
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

            JButton nuevoButton = new JButton("Nuevo", icon("/animaltrack/icons/32/add-new.png"));
            nuevoButton.addActionListener(e -> clearForm());
            buttonPanel.add(nuevoButton);

            JButton guardarButton = new JButton("Guardar", icon("/animaltrack/icons/32/save.png"));
            guardarButton.addActionListener(e -> save());
            buttonPanel.add(guardarButton);

            JButton borrarButton = new JButton("Borrar", icon("/animaltrack/icons/32/delete.png"));
            borrarButton.addActionListener(e -> deleteSelected());
            buttonPanel.add(borrarButton);

            JButton recargarButton = new JButton("Recargar", icon("/animaltrack/icons/32/refresh.png"));
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
            if (filter != null && model.getRowCount() == 0) {
                Object[] values = new Object[fields.size()];
                values[fields.size() > 1 ? 1 : 0] = NO_MATCHES_MESSAGE;
                model.addRow(values);
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

            Object selected = findRowById(id);
            String confirmationText = deleteConfirmationText(selected, id);
            if (!confirmDelete(this, confirmationText)) {
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

        private Object findRowById(Long id) {
            for (Object row : rows) {
                Object rowId = get(row, "id");
                if (id.equals(rowId)) {
                    return row;
                }
            }
            return null;
        }

        private String deleteConfirmationText(Object selected, Long id) {
            if (selected != null) {
                String crotal = stringValue(invoke(selected, "getCrotal"));
                if (crotal != null) {
                    return crotal;
                }
                if (isEvento(selected)) {
                    return "ELIMINAR";
                }
                String description = describe(selected);
                if (description != null && !description.trim().isEmpty()) {
                    return description;
                }
            }
            if (entityClass.getSimpleName().toLowerCase().contains("evento")) {
                return "ELIMINAR";
            }
            return String.valueOf(id);
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
                        new LookupItem(null, "ADMINISTRADOR", "Administrador"),
                        new LookupItem(null, "GANADERO", "Ganadero"),
                        new LookupItem(null, "VETERINARIO", "Veterinario")
                }));
                FilterableComboBoxSupport.decorate(combo);
                clearLookupSelection(combo);
                return combo;
            }
            if ("activo".equals(field)) {
                JComboBox<LookupItem> combo = new JComboBox<LookupItem>();
                combo.setModel(new DefaultComboBoxModel<LookupItem>(new LookupItem[] {
                        new LookupItem(null, "true", "Si"),
                        new LookupItem(null, "false", "No")
                }));
                clearLookupSelection(combo);
                return combo;
            }
            Object lookupService = lookupServiceFor(field);
            if (lookupService != null) {
                JComboBox<LookupItem> combo = new JComboBox<LookupItem>();
                combo.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(lookupService, false)));
                FilterableComboBoxSupport.decorate(combo);
                clearLookupSelection(combo);
                return combo;
            }
            return new JTextField(16);
        }

        private String readEditorValue(JComponent input) {
            if (input instanceof JTextField) {
                return ((JTextField) input).getText();
            }
            if (input instanceof JComboBox) {
                Object selected = FilterableComboBoxSupport.getSelectedItem((JComboBox<?>) input);
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

    private static class UsuariosPersonasPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final GanaderoService ganaderoService;
        private final com.tonin.animaltrack.service.VeterinarioService veterinarioService;
        private final MunicipioService municipioService;
        private final UsuarioLoginService usuarioLoginService;

        private final JComboBox<String> tipoUsuarioCombo;
        private final JTextField idTF;
        private final JLabel codigoLabel;
        private final JTextField codigoTF;
        private final JTextField dniTF;
        private final JTextField nombreTF;
        private final JTextField apellidosTF;
        private final JTextField telefonoTF;
        private final JTextField emailTF;
        private final JPasswordField passwordPF;
        private final JTextField direccionTF;
        private final JTextField codigoPostalTF;
        private final JComboBox<LookupItem> provinciaCombo;
        private final JComboBox<LookupItem> municipioCombo;
        private final JXSearchField filterTF;
        private final JXTable table;

        private List<Object> rows;

        UsuariosPersonasPanel() {
            this.ganaderoService = new GanaderoServiceImpl();
            this.veterinarioService = new VeterinarioServiceImpl();
            this.municipioService = new MunicipioServiceImpl();
            this.usuarioLoginService = new UsuarioLoginServiceImpl();
            this.rows = Collections.emptyList();

            setLayout(new BorderLayout(0, 0));

            JPanel formPanel = new JPanel(new GridBagLayout());
            add(formPanel, BorderLayout.NORTH);

            tipoUsuarioCombo = new JComboBox<String>();
            tipoUsuarioCombo.setModel(new DefaultComboBoxModel<String>(
                    new String[] { USER_TYPE_VETERINARIO, USER_TYPE_GANADERO }));
            idTF = new JTextField(20);
            idTF.setEditable(false);
            idTF.setVisible(false);
            codigoLabel = new JLabel("Codigo:");
            codigoTF = new JTextField(20);
            dniTF = new JTextField(20);
            nombreTF = new JTextField(20);
            apellidosTF = new JTextField(20);
            telefonoTF = new JTextField(20);
            emailTF = new JTextField(20);
            passwordPF = new JPasswordField(20);
            direccionTF = new JTextField(20);
            codigoPostalTF = new JTextField(20);
            provinciaCombo = new JComboBox<LookupItem>();
            municipioCombo = new JComboBox<LookupItem>();
            FilterableComboBoxSupport.decorate(provinciaCombo);
            FilterableComboBoxSupport.decorate(municipioCombo);

            int row = 0;
            addField(formPanel, row++, "Tipo:", tipoUsuarioCombo);
            addField(formPanel, row++, codigoLabel, codigoTF);
            addField(formPanel, row++, "DNI:", dniTF);
            addField(formPanel, row++, "Nombre:", nombreTF);
            addField(formPanel, row++, "Apellidos:", apellidosTF);
            addField(formPanel, row++, "Telefono:", telefonoTF);
            addField(formPanel, row++, "Email:", emailTF);
            addField(formPanel, row++, "Contraseña:", passwordPF);
            addField(formPanel, row++, "Direccion:", direccionTF);
            addField(formPanel, row++, "CP:", codigoPostalTF);
            addField(formPanel, row++, "Provincia:", provinciaCombo);
            addField(formPanel, row++, "Municipio:", municipioCombo);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            add(buttonPanel, BorderLayout.SOUTH);

            JButton nuevoButton = new JButton("Nuevo", icon("/animaltrack/icons/32/add-new.png"));
            nuevoButton.addActionListener(e -> clearForm());
            buttonPanel.add(nuevoButton);

            JButton guardarButton = new JButton("Guardar", icon("/animaltrack/icons/32/save.png"));
            guardarButton.addActionListener(e -> save());
            buttonPanel.add(guardarButton);

            JButton borrarButton = new JButton("Borrar", icon("/animaltrack/icons/32/delete.png"));
            borrarButton.addActionListener(e -> deleteSelected());
            buttonPanel.add(borrarButton);

            JButton recargarButton = new JButton("Recargar", icon("/animaltrack/icons/32/refresh.png"));
            recargarButton.addActionListener(e -> reload());
            buttonPanel.add(recargarButton);

            filterTF = new JXSearchField("Filtrar usuarios");
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

            provinciaCombo.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(new ProvinciaServiceImpl(), false)));
            clearLookupSelection(provinciaCombo);
            provinciaCombo.addActionListener(e -> reloadMunicipios());
            tipoUsuarioCombo.addActionListener(e -> {
                clearForm();
                updateVisibleFields();
                reload();
            });

            updateVisibleFields();
            reloadMunicipios();
            reload();
        }

        private boolean isVeterinarioSelected() {
            return USER_TYPE_VETERINARIO.equals(tipoUsuarioCombo.getSelectedItem());
        }

        private void updateVisibleFields() {
            boolean showCodigo = isVeterinarioSelected();
            codigoLabel.setVisible(showCodigo);
            codigoTF.setVisible(showCodigo);
            revalidate();
            repaint();
        }

        private void reloadMunicipios() {
            try {
                Long provinciaId = selectedId(provinciaCombo);
                if (provinciaId == null) {
                    municipioCombo.setModel(new DefaultComboBoxModel<LookupItem>());
                    clearLookupSelection(municipioCombo);
                    return;
                }
                List<?> municipios = municipioService.findByProvinciaId(provinciaId);
                List<LookupItem> items = new ArrayList<LookupItem>();
                for (Object municipio : municipios) {
                    Object id = invoke(municipio, "getId");
                    if (id != null) {
                        String value = String.valueOf(id);
                        items.add(new LookupItem((Long) convert(value, Long.class), value, describe(municipio)));
                    }
                }
                municipioCombo.setModel(new DefaultComboBoxModel<LookupItem>(items.toArray(new LookupItem[items.size()])));
                clearLookupSelection(municipioCombo);
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void reload() {
            try {
                rows = isVeterinarioSelected()
                        ? new ArrayList<Object>(veterinarioService.findAll())
                        : new ArrayList<Object>(ganaderoService.findAll());
                updateTable();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void updateTable() {
            String filter = normalize(filterTF.getText());
            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "ID", "Tipo", "Codigo", "DNI", "Nombre", "Apellidos", "Telefono", "Email",
                            "Direccion", "CP", "Provincia", "Municipio" },
                    0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Object value : rows) {
                Object[] tableRow = buildTableRow(value);
                StringBuilder searchable = new StringBuilder();
                for (Object column : tableRow) {
                    searchable.append(column).append(' ');
                }
                if (filter == null || normalize(searchable.toString()).contains(filter)) {
                    model.addRow(tableRow);
                }
            }
            if (filter != null && model.getRowCount() == 0) {
                Object[] empty = new Object[12];
                empty[3] = NO_MATCHES_MESSAGE;
                model.addRow(empty);
            }
            table.setModel(model);
            table.getColumnExt("ID").setVisible(false);
            table.getColumnExt("Tipo").setVisible(false);
        }

        private Object[] buildTableRow(Object value) {
            return new Object[] {
                    invoke(value, "getId"),
                    value instanceof VeterinarioDTO ? USER_TYPE_VETERINARIO : USER_TYPE_GANADERO,
                    invoke(value, "getCodigo"),
                    invoke(value, "getDni"),
                    invoke(value, "getNombre"),
                    invoke(value, "getApellidos"),
                    invoke(value, "getTelefono"),
                    invoke(value, "getEmail"),
                    invoke(value, "getDireccion"),
                    invoke(value, "getCodigoPostal"),
                    invoke(value, "getProvinciaNombre"),
                    invoke(value, "getMunicipioNombre")
            };
        }

        private void save() {
            try {
                Long id = parseLong(idTF.getText());
                String password = trimToNull(new String(passwordPF.getPassword()));
                if (id == null && password == null) {
                    JOptionPane.showMessageDialog(this, "Introduce una contraseña para la cuenta de acceso.",
                            "Usuarios", JOptionPane.WARNING_MESSAGE);
                    passwordPF.requestFocusInWindow();
                    return;
                }
                if (password != null && trimToNull(emailTF.getText()) == null) {
                    JOptionPane.showMessageDialog(this, "Introduce un email para crear la cuenta de acceso.",
                            "Usuarios", JOptionPane.WARNING_MESSAGE);
                    emailTF.requestFocusInWindow();
                    return;
                }

                if (isVeterinarioSelected()) {
                    Veterinario veterinario = buildVeterinario();
                    VeterinarioDTO saved;
                    if (id == null) {
                        saved = veterinarioService.create(veterinario);
                    } else {
                        veterinarioService.update(veterinario);
                        saved = veterinarioService.findById(id);
                    }
                    saveLogin(saved == null ? id : saved.getId(), null, password);
                } else {
                    Ganadero ganadero = buildGanadero();
                    GanaderoDTO saved;
                    if (id == null) {
                        saved = ganaderoService.create(ganadero);
                    } else {
                        ganaderoService.update(ganadero);
                        saved = ganaderoService.findById(id);
                    }
                    saveLogin(null, saved == null ? id : saved.getId(), password);
                }
                JOptionPane.showMessageDialog(this, "Usuario guardado correctamente.", "Usuarios",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void saveLogin(Long veterinarioId, Long ganaderoId, String password) throws Exception {
            if (password == null) {
                return;
            }
            String email = trimToNull(emailTF.getText());
            UsuarioLoginDTO usuario = usuarioLoginService.findByEmail(email);
            if (usuario == null) {
                usuario = new UsuarioLoginDTO();
            }
            usuario.setEmail(email);
            usuario.setPasswordHash(password);
            usuario.setRol(veterinarioId == null ? "GANADERO" : "VETERINARIO");
            usuario.setGanaderoId(ganaderoId);
            usuario.setVeterinarioId(veterinarioId);
            usuario.setActivo(Boolean.TRUE);

            if (usuario.getId() == null) {
                usuarioLoginService.create(usuario);
            } else {
                usuarioLoginService.update(usuario);
            }
        }

        private Veterinario buildVeterinario() {
            Veterinario veterinario = new Veterinario();
            veterinario.setId(parseLong(idTF.getText()));
            veterinario.setCodigo(trimToNull(codigoTF.getText()));
            veterinario.setDni(trimToNull(dniTF.getText()));
            veterinario.setNombre(trimToNull(nombreTF.getText()));
            veterinario.setApellidos(trimToNull(apellidosTF.getText()));
            veterinario.setTelefono(trimToNull(telefonoTF.getText()));
            veterinario.setEmail(trimToNull(emailTF.getText()));
            veterinario.setDireccion(trimToNull(direccionTF.getText()));
            veterinario.setCodigoPostal(trimToNull(codigoPostalTF.getText()));
            veterinario.setMunicipioId(selectedId(municipioCombo));
            return veterinario;
        }

        private Ganadero buildGanadero() {
            Ganadero ganadero = new Ganadero();
            ganadero.setId(parseLong(idTF.getText()));
            ganadero.setDni(trimToNull(dniTF.getText()));
            ganadero.setNombre(trimToNull(nombreTF.getText()));
            ganadero.setApellidos(trimToNull(apellidosTF.getText()));
            ganadero.setTelefono(trimToNull(telefonoTF.getText()));
            ganadero.setEmail(trimToNull(emailTF.getText()));
            ganadero.setDireccion(trimToNull(direccionTF.getText()));
            ganadero.setCodigoPostal(trimToNull(codigoPostalTF.getText()));
            ganadero.setMunicipioId(selectedId(municipioCombo));
            return ganadero;
        }

        private void loadSelectedIntoForm() {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Long id = parseLong(String.valueOf(table.getModel().getValueAt(modelRow, 0)));
            String tipo = String.valueOf(table.getModel().getValueAt(modelRow, 1));
            if (id == null) {
                return;
            }
            try {
                if (USER_TYPE_VETERINARIO.equals(tipo)) {
                    tipoUsuarioCombo.setSelectedItem(USER_TYPE_VETERINARIO);
                    loadVeterinario(veterinarioService.findById(id));
                } else {
                    tipoUsuarioCombo.setSelectedItem(USER_TYPE_GANADERO);
                    loadGanadero(ganaderoService.findById(id));
                }
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void loadVeterinario(VeterinarioDTO veterinario) {
            if (veterinario == null) {
                return;
            }
            idTF.setText(String.valueOf(veterinario.getId()));
            codigoTF.setText(defaultString(veterinario.getCodigo()));
            fillCommonFields(veterinario.getDni(), veterinario.getNombre(), veterinario.getApellidos(),
                    veterinario.getTelefono(), veterinario.getEmail(), veterinario.getDireccion(),
                    veterinario.getCodigoPostal(), veterinario.getProvinciaId(), veterinario.getMunicipioId());
        }

        private void loadGanadero(GanaderoDTO ganadero) {
            if (ganadero == null) {
                return;
            }
            idTF.setText(String.valueOf(ganadero.getId()));
            codigoTF.setText("");
            fillCommonFields(ganadero.getDni(), ganadero.getNombre(), ganadero.getApellidos(), ganadero.getTelefono(),
                    ganadero.getEmail(), ganadero.getDireccion(), ganadero.getCodigoPostal(), ganadero.getProvinciaId(),
                    ganadero.getMunicipioId());
        }

        private void fillCommonFields(String dni, String nombre, String apellidos, String telefono, String email,
                String direccion, String codigoPostal, Long provinciaId, Long municipioId) {
            dniTF.setText(defaultString(dni));
            nombreTF.setText(defaultString(nombre));
            apellidosTF.setText(defaultString(apellidos));
            telefonoTF.setText(defaultString(telefono));
            emailTF.setText(defaultString(email));
            direccionTF.setText(defaultString(direccion));
            codigoPostalTF.setText(defaultString(codigoPostal));
            selectLookupItem(provinciaCombo, provinciaId == null ? null : String.valueOf(provinciaId));
            reloadMunicipios();
            selectLookupItem(municipioCombo, municipioId == null ? null : String.valueOf(municipioId));
            updateVisibleFields();
        }

        private void deleteSelected() {
            Long id = parseLong(idTF.getText());
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Carga primero un usuario con doble click.", "Usuarios",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String description = trimToNull(nombreTF.getText() + " " + apellidosTF.getText());
            if (!confirmDelete(this, description == null ? "ELIMINAR" : description)) {
                return;
            }
            try {
                if (isVeterinarioSelected()) {
                    veterinarioService.delete(id);
                } else {
                    ganaderoService.delete(id);
                }
                clearForm();
                reload();
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void clearForm() {
            idTF.setText("");
            codigoTF.setText("");
            dniTF.setText("");
            nombreTF.setText("");
            apellidosTF.setText("");
            telefonoTF.setText("");
            emailTF.setText("");
            passwordPF.setText("");
            direccionTF.setText("");
            codigoPostalTF.setText("");
            clearLookupSelection(provinciaCombo);
            reloadMunicipios();
            updateVisibleFields();
        }

        private String defaultString(String value) {
            return value == null ? "" : value;
        }
    }

    private static class RelationsPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        RelationsPanel() {
            setLayout(new BorderLayout(0, 0));
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Veterinario-Granja", new RelationEditorPanel("Veterinario", "Granja",
                    new VeterinarioGranjaServiceImpl(), new VeterinarioServiceImpl(), new GranjaServiceImpl(), true));
            tabs.addTab("Ganadero-Granja", new RelationEditorPanel("Ganadero", "Granja",
                    new GranjaServiceImpl(), new GanaderoServiceImpl(), new GranjaServiceImpl(),
                    RelationEditorPanel.GANADERO_GRANJA));
            tabs.addTab("Animal-Semilla", new RelationEditorPanel("Animal", "Semilla",
                    new AnimalSemillaServiceImpl(), new AnimalServiceImpl(), new SemillaServiceImpl(), false));
            add(tabs, BorderLayout.CENTER);
        }
    }

    private static class RelationEditorPanel extends JPanel {

        private static final long serialVersionUID = 1L;
        private static final int VETERINARIO_GRANJA = 1;
        private static final int ANIMAL_SEMILLA = 2;
        private static final int GANADERO_GRANJA = 3;

        private final JComboBox<LookupItem> firstCB;
        private final JComboBox<LookupItem> secondCB;
        private final Object service;
        private final int relationType;
        private final JXTable table;

        RelationEditorPanel(String firstLabel, String secondLabel, Object service, Object firstLookupService,
                Object secondLookupService, boolean veterinarioGranja) {
            this(firstLabel, secondLabel, service, firstLookupService, secondLookupService,
                    veterinarioGranja ? VETERINARIO_GRANJA : ANIMAL_SEMILLA);
        }

        RelationEditorPanel(String firstLabel, String secondLabel, Object service, Object firstLookupService,
                Object secondLookupService, int relationType) {
            this.service = service;
            this.relationType = relationType;
            setLayout(new BorderLayout(0, 0));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            add(top, BorderLayout.NORTH);
            firstCB = new JComboBox<LookupItem>();
            firstCB.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(firstLookupService, false)));
            FilterableComboBoxSupport.decorate(firstCB);
            secondCB = new JComboBox<LookupItem>();
            secondCB.setModel(new DefaultComboBoxModel<LookupItem>(lookupItems(secondLookupService, false)));
            FilterableComboBoxSupport.decorate(secondCB);
            clearLookupSelection(firstCB);
            clearLookupSelection(secondCB);
            top.add(new JLabel(firstLabel + ":"));
            top.add(firstCB);
            top.add(new JLabel(secondLabel + ":"));
            top.add(secondCB);

            JButton guardarButton = new JButton("Guardar", icon("/animaltrack/icons/32/save.png"));
            guardarButton.addActionListener(e -> save());
            top.add(guardarButton);
            JButton borrarButton = new JButton("Borrar", icon("/animaltrack/icons/32/delete.png"));
            borrarButton.addActionListener(e -> delete());
            top.add(borrarButton);
            JButton buscarButton = new JButton("Buscar por primero", icon("/animaltrack/icons/32/search.png"));
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
                if (first == null || second == null) {
                    JOptionPane.showMessageDialog(this, "Selecciona los dos valores de la relación.", "Relaciones",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (relationType == VETERINARIO_GRANJA) {
                    VeterinarioGranja value = new VeterinarioGranja();
                    value.setVeterinarioId(first);
                    value.setGranjaId(second);
                    ((VeterinarioGranjaService) service).create(value);
                } else if (relationType == ANIMAL_SEMILLA) {
                    AnimalSemilla value = new AnimalSemilla();
                    value.setAnimalId(first);
                    value.setSemillaId(second);
                    ((AnimalSemillaService) service).create(value);
                } else {
                    assignGanaderoToGranja(first, second);
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
                if (first == null || second == null || !confirmDelete(this, "ELIMINAR")) {
                    return;
                }
                if (relationType == VETERINARIO_GRANJA) {
                    ((VeterinarioGranjaService) service).delete(first, second);
                } else if (relationType == ANIMAL_SEMILLA) {
                    ((AnimalSemillaService) service).delete(first, second);
                } else {
                    assignGanaderoToGranja(null, second);
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
                List<?> rows = relationType == VETERINARIO_GRANJA
                        ? ((VeterinarioGranjaService) service).findByVeterinarioId(first)
                        : relationType == ANIMAL_SEMILLA
                                ? ((AnimalSemillaService) service).findByAnimalId(first)
                                : ((GranjaService) service).findByGanaderoId(first);
                updateTable(rows == null ? Collections.emptyList() : rows);
            } catch (Exception ex) {
                showError(ex);
            }
        }

        private void assignGanaderoToGranja(Long ganaderoId, Long granjaId) throws Exception {
            if (granjaId == null) {
                return;
            }
            GranjaDTO dto = ((GranjaService) service).findById(granjaId);
            if (dto == null) {
                return;
            }
            Granja granja = new Granja();
            granja.setId(dto.getId());
            granja.setRega(dto.getRega());
            granja.setNombre(dto.getNombre());
            granja.setDireccion(dto.getDireccion());
            granja.setCodigoPostal(dto.getCodigoPostal());
            granja.setMunicipioId(dto.getMunicipioId());
            granja.setGanaderoId(ganaderoId);
            ((GranjaService) service).update(granja);
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
                } else if (row instanceof GranjaDTO) {
                    GranjaDTO value = (GranjaDTO) row;
                    model.addRow(new Object[] { value.getGanaderoId(), displayComboValue(firstCB, value.getGanaderoId()),
                            value.getId(), displayComboValue(secondCB, value.getId()) });
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
                    new ComboItem("Tipos notificación", new TipoNotificacionServiceImpl()),
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
                ComboItem item = (ComboItem) FilterableComboBoxSupport.getSelectedItem(selector);
                if (item == null) {
                    return;
                }
                try {
                    List<?> rows = (List<?>) item.service.getClass().getMethod("findAll").invoke(item.service);
                    DefaultTableModel model = new DefaultTableModel(new Object[] { "ID", "Nombre", "Extra" }, 0);
                    for (Object row : rows) {
                        Object extra = invoke(row, "getCodigo");
                        if (extra == null) {
                            extra = invoke(row, "getProvinciaId");
                        }
                        model.addRow(new Object[] { invoke(row, "getId"), invoke(row, "getNombre"), extra });
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
        addField(panel, row, new JLabel(label), component);
    }

    private static void addField(JPanel panel, int row, Component labelComponent, Component component) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.weightx = 0;
        labelConstraints.insets = new Insets(4, 6, 4, 6);
        labelConstraints.anchor = GridBagConstraints.WEST;
        panel.add(labelComponent, labelConstraints);

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
        combo.setSelectedItem(item);
        if (item == null && combo.isEditable()) {
            combo.getEditor().setItem("");
        }
    }

    private static void clearLookupSelection(JComboBox<?> combo) {
        combo.setSelectedIndex(-1);
        if (combo.isEditable()) {
            combo.getEditor().setItem("");
        }
    }

    private static Long selectedId(JComboBox<LookupItem> combo) {
        Object selected = FilterableComboBoxSupport.getSelectedItem(combo);
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
        String rega = stringValue(invoke(row, "getRega"));
        if (rega != null && nombre != null) {
            return rega + " - " + nombre;
        }
        if (rega != null) {
            return rega;
        }
        if (nombre != null) {
            return nombre;
        }
        Object id = invoke(row, "getId");
        return id == null ? "" : String.valueOf(id);
    }

    private static boolean isEvento(Object row) {
        return row != null && row.getClass().getSimpleName().toLowerCase().contains("evento");
    }

    private static boolean confirmDelete(Component parent, String expectedText) {
        String expected = trimToNull(expectedText);
        if (expected == null) {
            expected = "ELIMINAR";
        }
        String typed = JOptionPane.showInputDialog(parent, "Para borrar escribe \"" + expected + "\":", "Confirmar borrado",
                JOptionPane.WARNING_MESSAGE);
        return expected.equals(typed);
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
        logger.error(rootMessage(ex), ex);
        JOptionPane.showMessageDialog(null, rootMessage(ex), "Administración", JOptionPane.ERROR_MESSAGE);
    }

    private static String rootMessage(Throwable ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? current.toString() : current.getMessage();
    }
}
