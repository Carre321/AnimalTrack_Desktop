package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXSearchField;

import com.toedter.calendar.JDateChooser;
import com.tonin.animaltrack.dao.criteria.EventoCriteria;
import com.tonin.animaltrack.model.TipoEvento;
import com.tonin.animaltrack.model.dto.EventoDTO;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.EventoService;
import com.tonin.animaltrack.service.TipoEventoService;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.EventoServiceImpl;
import com.tonin.animaltrack.service.impl.TipoEventoServiceImpl;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.controler.EventoSetEditableController;
import com.tonin.animaltrack.views.controler.EventoSearchController;

public class EventoSearchView extends AbstractView implements FarmFilterAware {
    private static final String NO_MATCHES_MESSAGE = "No hay coincidencias con el filtro de busqueda.";

    private JTextField animalIdTF;
    private JXTable resultadosTable;
    private JTextField animalNombreTF;
    private JComboBox<ComboItem<TipoEvento>> tipoEventoCombo;
    private JComboBox<ComboItem<VeterinarioDTO>> veterinarioCombo;
    private JDateChooser fechaDesdeDateChooser;
    private JDateChooser fechaHastaDateChooser;
    private EventoService eventoService;
    private TipoEventoService tipoEventoService;
    private VeterinarioService veterinarioService;
    private List<EventoDTO> model;
    private JButton buscarButton;
    private EventoSearchController searchController;
    private EventoContainerView containerView;

    public EventoSearchView() {
        this(null);
    }

    public EventoSearchView(EventoContainerView containerView) {
        this.containerView = containerView;
        initialize();
        initServices();
        postInitialize();
        refreshForSelectedFarm();
    }

    private void initialize() {
        setName("Buscar Evento");
        setLayout(new BorderLayout(0, 0));

        JPanel buscarPanel = new JPanel();
        add(buscarPanel, BorderLayout.NORTH);
        buscarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel lblAnimal = new JLabel("Animal:");
        buscarPanel.add(lblAnimal);

        animalNombreTF = new JXSearchField("Animal");
        buscarPanel.add(animalNombreTF);
        animalNombreTF.setColumns(14);

        JLabel lblCrotal = new JLabel("Crotal:");
        buscarPanel.add(lblCrotal);

        animalIdTF = new JXSearchField("Crotal");
        buscarPanel.add(animalIdTF);
        animalIdTF.setColumns(10);

        JLabel lblTipoEvento = new JLabel("Tipo Evento:");
        buscarPanel.add(lblTipoEvento);

        tipoEventoCombo = new JComboBox<ComboItem<TipoEvento>>();
        FilterableComboBoxSupport.decorate(tipoEventoCombo);
        buscarPanel.add(tipoEventoCombo);

        JLabel lblVeterinario = new JLabel("Veterinario:");
        buscarPanel.add(lblVeterinario);

        veterinarioCombo = new JComboBox<ComboItem<VeterinarioDTO>>();
        FilterableComboBoxSupport.decorate(veterinarioCombo);
        buscarPanel.add(veterinarioCombo);

        JLabel fechaDesdeLabel = new JLabel("Fecha Desde:");
        buscarPanel.add(fechaDesdeLabel);

        fechaDesdeDateChooser = new JDateChooser();
        buscarPanel.add(fechaDesdeDateChooser);

        JLabel fechaHastaLabel = new JLabel("Hasta:");
        buscarPanel.add(fechaHastaLabel);

        fechaHastaDateChooser = new JDateChooser();
        buscarPanel.add(fechaHastaDateChooser);

        buscarButton = new JButton("Buscar");
        buscarButton.setIcon(new ImageIcon(EventoSearchView.class.getResource("/animaltrack/icons/32/search.png")));
        buscarPanel.add(buscarButton);

        resultadosTable = new JXTable();
        resultadosTable.setColumnControlVisible(true);
        resultadosTable.setSortable(true);
        add(new JScrollPane(resultadosTable), BorderLayout.CENTER);
    }

    private void initServices() {
        eventoService = new EventoServiceImpl();
        tipoEventoService = new TipoEventoServiceImpl();
        veterinarioService = new VeterinarioServiceImpl();
        loadTipoEventoCombo();
        loadVeterinarioCombo();
    }

    private void postInitialize() {
        searchController = new EventoSearchController(this);
        buscarButton.setAction(searchController);
        animalNombreTF.addKeyListener(searchController);
        animalIdTF.addKeyListener(searchController);
        tipoEventoCombo.addActionListener(e -> searchController.doAction());
        veterinarioCombo.addActionListener(e -> searchController.doAction());
        fechaDesdeDateChooser.addPropertyChangeListener("date", e -> searchController.doAction());
        fechaHastaDateChooser.addPropertyChangeListener("date", e -> searchController.doAction());
        resultadosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedEventoDetail();
                }
            }
        });
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return new Timestamp(date.getTime()).toLocalDateTime();
    }

    public EventoCriteria getCriteria() {
        EventoCriteria criteria = new EventoCriteria();

        Date fechaDesde = fechaDesdeDateChooser.getDate();
        if (fechaDesde != null) {
            criteria.setFechaDesde(toLocalDateTime(fechaDesde));
        }

        Date fechaHasta = fechaHastaDateChooser.getDate();
        if (fechaHasta != null) {
            criteria.setFechaHasta(toLocalDateTime(fechaHasta));
        }

        String animalNombre = animalNombreTF.getText() == null ? null : animalNombreTF.getText().trim();
        String crotal = animalIdTF.getText() == null ? null : animalIdTF.getText().trim();
        ComboItem<TipoEvento> tipoEventoItem = getSelectedItem(tipoEventoCombo);
        ComboItem<VeterinarioDTO> veterinarioItem = getSelectedItem(veterinarioCombo);

        if (animalNombre != null && !animalNombre.isEmpty()) {
            criteria.setAnimalNombreLike(animalNombre);
        }
        if (crotal != null && !crotal.isEmpty()) {
            criteria.setAnimalCrotalLike(crotal);
        }
        if (tipoEventoItem != null && tipoEventoItem.getValue() != null) {
            criteria.setTipoEventoId(tipoEventoItem.getValue().getId());
        }
        if (veterinarioItem != null && veterinarioItem.getValue() != null) {
            criteria.setVeterinarioId(veterinarioItem.getValue().getId());
        }
        criteria.setGranjaId(MainWindow.getInstance().getSelectedGranjaId());

        return criteria;
    }

    @Override
    public void refreshForSelectedFarm() {
        if (searchController != null) {
            searchController.doAction();
        }
    }

    public void setModel(List<EventoDTO> model) {
        this.model = model == null ? Collections.emptyList() : model;
        updateView();
    }

    public void updateView() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Animal", "Crotal", "Tipo Evento", "Veterinario", "Fecha", "Precio" }, 0) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        if (this.model.isEmpty()) {
            model.addRow(new Object[] { NO_MATCHES_MESSAGE, null, null, null, null, null });
        } else {
            for (EventoDTO dto : this.model) {
                model.addRow(new Object[] {
                        dto.getAnimalNombre(),
                        dto.getAnimalCrotal(),
                        dto.getTipoEventoNombre(),
                        dto.getVeterinarioNombreCompleto(),
                        dto.getFechaHora(),
                        dto.getPrecioEvento() });
            }
        }

        resultadosTable.setModel(model);
    }

    private void openSelectedEventoDetail() {
        if (containerView == null) {
            return;
        }
        int selectedRow = resultadosTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = resultadosTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= this.model.size()) {
            return;
        }

        EventoDTO evento = eventoService.findById(this.model.get(modelRow).getId());
        if (evento == null) {
            return;
        }

        EventoCreateView eventoView = new EventoCreateView();
        eventoView.setModel(evento);
        eventoView.setEditable(false);
        eventoView.setAgreeController(new EventoSetEditableController(eventoView));
        containerView.addClosableTab(buildViewTitle(evento), eventoView);
    }

    private String buildViewTitle(EventoDTO evento) {
        String tipo = evento.getTipoEventoNombre() == null ? "Evento" : evento.getTipoEventoNombre();
        String animal = evento.getAnimalCrotal() == null ? "" : " " + evento.getAnimalCrotal();
        return tipo + animal;
    }

    private void loadTipoEventoCombo() {
        DefaultComboBoxModel<ComboItem<TipoEvento>> comboModel = new DefaultComboBoxModel<ComboItem<TipoEvento>>();
        comboModel.addElement(new ComboItem<TipoEvento>(null, "Todos"));
        List<TipoEvento> tipos = tipoEventoService.findAll();
        if (tipos != null) {
            for (TipoEvento tipo : tipos) {
                comboModel.addElement(new ComboItem<TipoEvento>(tipo, tipo.getNombre()));
            }
        }
        tipoEventoCombo.setModel(comboModel);
        tipoEventoCombo.setSelectedIndex(0);
    }

    private void loadVeterinarioCombo() {
        DefaultComboBoxModel<ComboItem<VeterinarioDTO>> comboModel = new DefaultComboBoxModel<ComboItem<VeterinarioDTO>>();
        comboModel.addElement(new ComboItem<VeterinarioDTO>(null, "Todos"));
        List<VeterinarioDTO> veterinarios = veterinarioService.findAll();
        if (veterinarios != null) {
            for (VeterinarioDTO veterinario : veterinarios) {
                comboModel.addElement(new ComboItem<VeterinarioDTO>(veterinario, veterinario.getNombreCompleto()));
            }
        }
        veterinarioCombo.setModel(comboModel);
        veterinarioCombo.setSelectedIndex(0);
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = combo.getSelectedItem();
        if (!(selectedItem instanceof ComboItem)) {
            return null;
        }
        return (ComboItem<T>) selectedItem;
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

