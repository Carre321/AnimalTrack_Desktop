package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toedter.calendar.JDateChooser;
import com.tonin.animaltrack.dao.Results;
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
    private static Logger logger = LogManager.getLogger(EventoSearchView.class.getName());

    private static final String NO_MATCHES_MESSAGE = "No hay coincidencias con el filtro de búsqueda.";
    private static final int PAGE_SIZE = 20;

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
    private JButton anteriorButton;
    private JButton siguienteButton;
    private JLabel paginaLabel;
    private JLabel totalResultadosLabel;
    private EventoSearchController searchController;
    private EventoContainerView containerView;
    private int paginaActual = 1;

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
        animalNombreTF.setColumns(11);

        JLabel lblCrotal = new JLabel("Crotal:");
        buscarPanel.add(lblCrotal);

        animalIdTF = new JXSearchField("Crotal");
        buscarPanel.add(animalIdTF);
        animalIdTF.setColumns(8);

        JLabel lblTipoEvento = new JLabel("Tipo Evento:");
        buscarPanel.add(lblTipoEvento);

        tipoEventoCombo = new JComboBox<ComboItem<TipoEvento>>();
        setFixedComboWidth(tipoEventoCombo, 180);
        buscarPanel.add(tipoEventoCombo);

        JLabel lblVeterinario = new JLabel("Veterinario:");
        buscarPanel.add(lblVeterinario);

        veterinarioCombo = new JComboBox<ComboItem<VeterinarioDTO>>();
        setFixedComboWidth(veterinarioCombo, 240);
        buscarPanel.add(veterinarioCombo);

        JLabel fechaDesdeLabel = new JLabel("Fecha Desde:");
        buscarPanel.add(fechaDesdeLabel);

        fechaDesdeDateChooser = new JDateChooser();
        fechaDesdeDateChooser.setPreferredSize(new Dimension(100, fechaDesdeDateChooser.getPreferredSize().height));
        buscarPanel.add(fechaDesdeDateChooser);

        JLabel fechaHastaLabel = new JLabel("Hasta:");
        buscarPanel.add(fechaHastaLabel);

        fechaHastaDateChooser = new JDateChooser();
        fechaHastaDateChooser.setPreferredSize(new Dimension(100, fechaHastaDateChooser.getPreferredSize().height));
        buscarPanel.add(fechaHastaDateChooser);

        buscarButton = new JButton("Buscar");
        buscarButton.setIcon(new ImageIcon(EventoSearchView.class.getResource("/animaltrack/icons/32/search.png")));
        buscarPanel.add(buscarButton);

        resultadosTable = new JXTable();
        resultadosTable.setColumnControlVisible(true);
        resultadosTable.setSortable(true);
        add(new JScrollPane(resultadosTable), BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel();
        FlowLayout paginationLayout = (FlowLayout) paginationPanel.getLayout();
        paginationLayout.setAlignment(FlowLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        anteriorButton = new JButton("<<");
        paginaLabel = new JLabel("Página 1 de 1");
        siguienteButton = new JButton(">>");
        totalResultadosLabel = new JLabel("Total: 0 resultados");

        paginationPanel.add(anteriorButton);
        paginationPanel.add(paginaLabel);
        paginationPanel.add(siguienteButton);
        paginationPanel.add(totalResultadosLabel);

        anteriorButton.setVisible(false);
        paginaLabel.setVisible(false);
        siguienteButton.setVisible(false);
    }

    private void initServices() {
        eventoService = new EventoServiceImpl();
        tipoEventoService = new TipoEventoServiceImpl();
        veterinarioService = new VeterinarioServiceImpl();
        loadTipoEventoCombo();
        loadVeterinarioCombo();
    }

    private void setFixedComboWidth(JComboBox<?> combo, int width) {
        Dimension preferred = combo.getPreferredSize();
        combo.setPreferredSize(new Dimension(width, preferred.height));
        combo.setMinimumSize(new Dimension(width, preferred.height));
        combo.setMaximumSize(new Dimension(width, preferred.height));
    }

    private void postInitialize() {
        FilterableComboBoxSupport.decorate(tipoEventoCombo);
        FilterableComboBoxSupport.decorate(veterinarioCombo);

        searchController = new EventoSearchController(this);
        buscarButton.setAction(searchController);
        anteriorButton.addActionListener(e -> searchController.buscarPagina(paginaActual - 1));
        siguienteButton.addActionListener(e -> searchController.buscarPagina(paginaActual + 1));
        animalNombreTF.addKeyListener(searchController);
        animalIdTF.addKeyListener(searchController);
        tipoEventoCombo.addItemListener(searchController);
        veterinarioCombo.addItemListener(searchController);
        fechaDesdeDateChooser.addPropertyChangeListener("date", e -> searchController.buscarPagina(1));
        fechaHastaDateChooser.addPropertyChangeListener("date", e -> searchController.buscarPagina(1));
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
        Long selectedGranjaId = MainWindow.getInstance().getSelectedGranjaId();
        criteria.setGranjaId(selectedGranjaId);
        if (!MainWindow.getInstance().getPermissions().isAdmin() && selectedGranjaId == null) {
            criteria.setGranjaId(-1L);
        }

        return criteria;
    }

    @Override
    public void refreshForSelectedFarm() {
        if (searchController != null) {
            searchController.buscarPagina(1);
        }
    }

    public void setModel(List<EventoDTO> model) {
        this.model = model == null ? Collections.emptyList() : model;
        updateView();
    }

    public int getPageSize() {
        return PAGE_SIZE;
    }

    public void setResults(Results<EventoDTO> results, int pagina) {
        this.model = results == null ? Collections.<EventoDTO>emptyList() : results.getPageResults();
        updateView(results == null ? 0 : results.getTotal(), pagina);
    }

    public void updateView() {
        updateView(model == null ? 0 : model.size(), 1);
    }

    public void updateView(int total, int pagina) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Animal", "Crotal", "Tipo Evento", "Resultado", "Veterinario", "Fecha", "Precio" }, 0) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        if (this.model.isEmpty()) {
            model.addRow(new Object[] { NO_MATCHES_MESSAGE, null, null, null, null, null, null });
        } else {
            for (EventoDTO dto : this.model) {
                model.addRow(new Object[] {
                        dto.getAnimalNombre(),
                        dto.getAnimalCrotal(),
                        dto.getTipoEventoNombre(),
                        dto.getResultado(),
                        dto.getVeterinarioNombreCompleto(),
                        dto.getFechaHora(),
                        dto.getPrecioEvento() });
            }
        }

        resultadosTable.setModel(model);

        paginaActual = pagina < 1 ? 1 : pagina;
        int totalPaginas = (int) Math.ceil((double) total / PAGE_SIZE);
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }

        boolean mostrarPaginacion = total > PAGE_SIZE;
        anteriorButton.setVisible(mostrarPaginacion);
        paginaLabel.setVisible(mostrarPaginacion);
        siguienteButton.setVisible(mostrarPaginacion);
        anteriorButton.setEnabled(paginaActual > 1);
        siguienteButton.setEnabled(paginaActual < totalPaginas);
        paginaLabel.setText("Página " + paginaActual + " de " + totalPaginas);
        totalResultadosLabel.setText("Total: " + total + " resultados");
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

        EventoDTO evento = null;
        try {
            evento = eventoService.findById(this.model.get(modelRow).getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
        if (evento == null) {
            return;
        }

        EventoCreateView eventoView = new EventoCreateView();
        eventoView.setModel(evento);
        eventoView.setEditable(false);
        if (MainWindow.getInstance().getPermissions().canEditEvento()) {
            eventoView.setAgreeController(new EventoSetEditableController(eventoView));
        }
        containerView.addClosableTab(buildViewTitle(evento), eventoView);
    }

    private String buildViewTitle(EventoDTO evento) {
        String tipo = evento.getTipoEventoNombre() == null ? "Evento" : evento.getTipoEventoNombre();
        String animal = evento.getAnimalCrotal() == null ? "" : " " + evento.getAnimalCrotal();
        return tipo + animal;
    }

    private void loadTipoEventoCombo() {
        DefaultComboBoxModel<ComboItem<TipoEvento>> comboModel = new DefaultComboBoxModel<ComboItem<TipoEvento>>();
        try {
            List<TipoEvento> tipos = tipoEventoService.findAll();
            if (tipos != null) {
                for (TipoEvento tipo : tipos) {
                    comboModel.addElement(new ComboItem<TipoEvento>(tipo, tipo.getNombre()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        tipoEventoCombo.setModel(comboModel);
        clearComboSelection(tipoEventoCombo);
    }

    private void loadVeterinarioCombo() {
        DefaultComboBoxModel<ComboItem<VeterinarioDTO>> comboModel = new DefaultComboBoxModel<ComboItem<VeterinarioDTO>>();
        try {
            List<VeterinarioDTO> veterinarios = veterinarioService.findAll();
            if (veterinarios != null) {
                for (VeterinarioDTO veterinario : veterinarios) {
                    comboModel.addElement(new ComboItem<VeterinarioDTO>(veterinario, veterinario.getNombreCompleto()));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        veterinarioCombo.setModel(comboModel);
        clearComboSelection(veterinarioCombo);
    }

    private <T> void clearComboSelection(JComboBox<ComboItem<T>> combo) {
        combo.setSelectedIndex(-1);
        combo.getEditor().setItem("");
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = FilterableComboBoxSupport.getSelectedItem(combo);
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

