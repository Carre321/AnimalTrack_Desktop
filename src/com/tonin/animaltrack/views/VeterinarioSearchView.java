package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXSearchField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tonin.animaltrack.dao.criteria.VeterinarioCriteria;
import com.tonin.animaltrack.model.dto.VeterinarioDTO;
import com.tonin.animaltrack.service.VeterinarioService;
import com.tonin.animaltrack.service.impl.VeterinarioServiceImpl;
import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.controler.VeterinarioSetEditableController;
import com.tonin.animaltrack.views.controler.VeterinarioSearchController;

public class VeterinarioSearchView extends AbstractView implements FarmFilterAware {
    private static Logger logger = LogManager.getLogger(VeterinarioSearchView.class.getName());

    private static final String NO_MATCHES_MESSAGE = "No hay coincidencias con el filtro de búsqueda.";

    private JTextField codigoTF;
    private JTextField nombreTF;
    private JTextField apellidosTF;
    private JTextField dniTF;
    private JXTable resultadosTable;
    private VeterinarioService veterinarioService;
    private List<VeterinarioDTO> model;
    private JButton buscarButton;
    private VeterinarioSearchController searchController;
    private VeterinarioContainerView containerView;

    public VeterinarioSearchView() {
        this(null);
    }

    public VeterinarioSearchView(VeterinarioContainerView containerView) {
        this.containerView = containerView;
        initialize();
        initServices();
        postInitialize();
        refreshForSelectedFarm();
    }

    private void initialize() {
        setName("Buscar Veterinario");
        setLayout(new BorderLayout(0, 0));

        JPanel buscarPanel = new JPanel();
        add(buscarPanel, BorderLayout.NORTH);
        buscarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JLabel codigoJlabel = new JLabel("Código:");
        buscarPanel.add(codigoJlabel);

        codigoTF = new JXSearchField("Código");
        codigoTF.setColumns(10);
        buscarPanel.add(codigoTF);

        JLabel nombreJlabel = new JLabel("Nombre:");
        buscarPanel.add(nombreJlabel);

        nombreTF = new JXSearchField("Nombre");
        nombreTF.setColumns(15);
        buscarPanel.add(nombreTF);

        JLabel apellidosJlabel = new JLabel("Apellidos:");
        buscarPanel.add(apellidosJlabel);

        apellidosTF = new JXSearchField("Apellidos");
        apellidosTF.setColumns(18);
        buscarPanel.add(apellidosTF);

        JLabel dniJlabel = new JLabel("DNI:");
        buscarPanel.add(dniJlabel);

        dniTF = new JXSearchField("DNI");
        dniTF.setColumns(10);
        buscarPanel.add(dniTF);

        buscarButton = new JButton("Buscar");
        buscarButton
                .setIcon(new ImageIcon(VeterinarioSearchView.class.getResource("/animaltrack/icons/32/search.png")));
        buscarPanel.add(buscarButton);

        resultadosTable = new JXTable();
        resultadosTable.setColumnControlVisible(true);
        resultadosTable.setSortable(true);
        add(new JScrollPane(resultadosTable), BorderLayout.CENTER);
    }

    private void initServices() {
        veterinarioService = new VeterinarioServiceImpl();
    }

    private void postInitialize() {
        searchController = new VeterinarioSearchController(this);
        buscarButton.setAction(searchController);
        codigoTF.addKeyListener(searchController);
        nombreTF.addKeyListener(searchController);
        apellidosTF.addKeyListener(searchController);
        dniTF.addKeyListener(searchController);
        resultadosTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedVeterinarioDetail();
                }
            }
        });
    }

    public VeterinarioCriteria getCriteria() {
        VeterinarioCriteria criteria = new VeterinarioCriteria();
        String codigo = codigoTF.getText() == null ? null : codigoTF.getText().trim();
        String nombre = nombreTF.getText() == null ? null : nombreTF.getText().trim();
        String apellidos = apellidosTF.getText() == null ? null : apellidosTF.getText().trim();
        String dni = dniTF.getText() == null ? null : dniTF.getText().trim();

        if (codigo != null && !codigo.isEmpty()) {
            criteria.setCodigoLike(codigo);
        }
        if (nombre != null && !nombre.isEmpty()) {
            criteria.setNombreLike(nombre);
        }
        if (apellidos != null && !apellidos.isEmpty()) {
            criteria.setApellidosLike(apellidos);
        }
        if (dni != null && !dni.isEmpty()) {
            criteria.setDniLike(dni);
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

    public void setModel(List<VeterinarioDTO> model) {
        this.model = model == null ? Collections.emptyList() : model;
        updateView();
    }

    public void updateView() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[] { "Código", "DNI", "Nombre", "Apellidos", "Teléfono", "Email", "Dirección", "CP", "Municipio", "Provincia" },
                0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (this.model.isEmpty()) {
            model.addRow(new Object[] { NO_MATCHES_MESSAGE, null, null, null, null, null, null, null, null, null });
        } else {
            for (VeterinarioDTO dto : this.model) {
                model.addRow(new Object[] {
                        dto.getCodigo(),
                        dto.getDni(),
                        dto.getNombre(),
                        dto.getApellidos(),
                        dto.getTelefono(),
                        dto.getEmail(),
                        dto.getDireccion(),
                        dto.getCodigoPostal(),
                        dto.getMunicipioNombre(),
                        dto.getProvinciaNombre() });
            }
        }

        resultadosTable.setModel(model);
    }

    private void openSelectedVeterinarioDetail() {
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

        VeterinarioDTO veterinario = null;
        try {
            veterinario = veterinarioService.findById(this.model.get(modelRow).getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }
        if (veterinario == null) {
            return;
        }

        VeterinarioCreateView veterinarioView = new VeterinarioCreateView();
        veterinarioView.setModel(veterinario);
        veterinarioView.setEditable(false);
        veterinarioView.setAgreeController(new VeterinarioSetEditableController(veterinarioView));
        containerView.addClosableTab(buildViewTitle(veterinario), veterinarioView);
    }

    private String buildViewTitle(VeterinarioDTO veterinario) {
        if (veterinario.getCodigo() != null && !veterinario.getCodigo().trim().isEmpty()) {
            return veterinario.getCodigo();
        }
        return veterinario.getNombreCompleto();
    }
}

