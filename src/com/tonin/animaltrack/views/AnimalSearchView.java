package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTable;
import com.tonin.animaltrack.dao.criteria.AnimalCriteria;
import com.tonin.animaltrack.model.Raza;
import com.tonin.animaltrack.model.Sexo;
import com.tonin.animaltrack.model.dto.AnimalDTO;
import com.tonin.animaltrack.service.AnimalService;
import com.tonin.animaltrack.service.RazaService;
import com.tonin.animaltrack.service.SexoService;
import com.tonin.animaltrack.service.impl.AnimalServiceImpl;
import com.tonin.animaltrack.service.impl.RazaServiceImpl;
import com.tonin.animaltrack.service.impl.SexoServiceImpl;
import com.tonin.animaltrack.ui.MainWindow;
import com.tonin.animaltrack.views.controler.AnimalSetEditableController;
import com.tonin.animaltrack.views.controler.AnimalSearchController;
import javax.swing.JComboBox;

import com.tonin.animaltrack.views.renderer.AnimalTableRenderer;
import com.tonin.animaltrack.views.tableModel.AnimalTableModel;

public class AnimalSearchView extends AbstractView implements FarmFilterAware {
    private static final long serialVersionUID = 1L;

    private AnimalService animalService;
    private SexoService sexoService;
    private RazaService razaService;
    private List<AnimalDTO> model;
    private JTextField nombreTF;
    private JTextField crotalTF;
    private JXTable resultsTable;
    private JLabel totalResultadosLabel;
    private JButton searchButton;
    private JComboBox<ComboItem<Sexo>> sexoCB;
    private JComboBox<ComboItem<Raza>> razaCB;
    private AnimalContainerView containerView;
    private AnimalSearchController searchController;

    public AnimalSearchView() {
        this(null);
    }

    public AnimalSearchView(AnimalContainerView containerView) {
        this.containerView = containerView;
        initialize();
        initServices();
        postInitialize();
        refreshForSelectedFarm();
    }

    private void initialize() {
        setName("Buscar Animal");
        setLayout(new BorderLayout(0, 0));
        
        JPanel contentPane = new JPanel();
        add(contentPane, BorderLayout.CENTER);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel searchPanel = new JPanel();
        contentPane.add(searchPanel, BorderLayout.NORTH);
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel lblNombre = new JLabel("Nombre:");
        searchPanel.add(lblNombre);
        
        nombreTF = new JXSearchField("Nombre");
        nombreTF.setColumns(20);
        searchPanel.add(nombreTF);
        
        JLabel lblCrotal = new JLabel("Crotal:");
        searchPanel.add(lblCrotal);
        
        crotalTF = new JXSearchField("Crotal");
        crotalTF.setColumns(12);
        searchPanel.add(crotalTF);

        JLabel lblSexo = new JLabel("Sexo:");
        searchPanel.add(lblSexo);

        sexoCB = new JComboBox<ComboItem<Sexo>>();
        searchPanel.add(sexoCB);

        JLabel lblRaza = new JLabel("Raza:");
        searchPanel.add(lblRaza);

        razaCB = new JComboBox<ComboItem<Raza>>();
        searchPanel.add(razaCB);
        
        searchButton = new JButton("Buscar");
        searchPanel.add(searchButton);
        
        
        JPanel resultsPanel = new JPanel();
        contentPane.add(resultsPanel, BorderLayout.CENTER);
        resultsPanel.setLayout(new BorderLayout(0, 0));
        
        resultsTable = new JXTable();
        resultsTable.setColumnControlVisible(true);
        resultsTable.setSortable(true);
        resultsPanel.add(resultsTable, BorderLayout.CENTER);
        
        JPanel paginationPanel = new JPanel();
        FlowLayout fl_paginationPanel = (FlowLayout) paginationPanel.getLayout();
        fl_paginationPanel.setAlignment(FlowLayout.LEFT);
        contentPane.add(paginationPanel, BorderLayout.SOUTH);
        
        totalResultadosLabel = new JLabel("");
        paginationPanel.add(totalResultadosLabel);
    }

    private void initServices() {
        animalService = new AnimalServiceImpl();
        sexoService = new SexoServiceImpl();
        razaService = new RazaServiceImpl();
        loadSexoCombo();
        loadRazaCombo();
    }

    private void postInitialize() {
    	
    	FilterableComboBoxSupport.decorate(sexoCB);
    	FilterableComboBoxSupport.decorate(razaCB);
    	
        searchController = new AnimalSearchController(this);
        searchButton.setAction(searchController);
        crotalTF.addKeyListener(searchController);
        sexoCB.addItemListener(searchController);
        razaCB.addItemListener(searchController);
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedAnimalDetail();
                }
            }
        });
    }
    
    
    
    public AnimalCriteria getCriteria() {
    	
    	AnimalCriteria criteria = new AnimalCriteria();
        //String nombre = nombreTF.getText() == null ? null : nombreTF.getText().trim();
        //String crotal = crotalTF.getText() == null ? null : crotalTF.getText().trim();
		String nombre = StringUtils.trimToNull(nombreTF.getText());
		String crotal = StringUtils.trimToNull(crotalTF.getText());

        criteria.setNombreLike(nombre);
        criteria.setCrotalLike(crotal);
        ComboItem<Sexo> sexoItem = getSelectedItem(sexoCB);
        criteria.setSexoId(sexoItem == null || sexoItem.getValue() == null ? null : sexoItem.getValue().getId());
        ComboItem<Raza> razaItem = getSelectedItem(razaCB);
        criteria.setRazaId(razaItem == null || razaItem.getValue() == null ? null : razaItem.getValue().getId());
        criteria.setGranjaId(MainWindow.getInstance().getSelectedGranjaId());

        return criteria;

	}

    @Override
    public void refreshForSelectedFarm() {
        if (searchController != null) {
            searchController.doAction();
        }
    }
    
    public void setModel(List<AnimalDTO> model) {
        this.model = model;
        updateView();
    }

    public void updateView() {
        List<AnimalDTO> rows = model == null ? Collections.<AnimalDTO>emptyList() : model;
        resultsTable.setModel(new AnimalTableModel(rows));
        resultsTable.setDefaultRenderer(AnimalDTO.class, new AnimalTableRenderer());
        totalResultadosLabel.setText(rows.size() + " Resultados Encontrados");
    }

    private void loadSexoCombo() {
        DefaultComboBoxModel<ComboItem<Sexo>> comboModel = new DefaultComboBoxModel<ComboItem<Sexo>>();
        comboModel.addElement(new ComboItem<Sexo>(null, "Todos"));
        List<Sexo> sexos = sexoService.findAll();
        if (sexos != null) {
            for (Sexo sexo : sexos) {
                comboModel.addElement(new ComboItem<Sexo>(sexo, sexo.getNombre()));
            }
        }
        sexoCB.setModel(comboModel);
        sexoCB.setSelectedIndex(0);
    }

    private void loadRazaCombo() {
        DefaultComboBoxModel<ComboItem<Raza>> comboModel = new DefaultComboBoxModel<ComboItem<Raza>>();
        comboModel.addElement(new ComboItem<Raza>(null, "Todas"));
        List<Raza> razas = razaService.findAll();
        if (razas != null) {
            for (Raza raza : razas) {
                comboModel.addElement(new ComboItem<Raza>(raza, raza.getNombre()));
            }
        }
        razaCB.setModel(comboModel);
        razaCB.setSelectedIndex(0);
    }

    @SuppressWarnings("unchecked")
    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo) {
        Object selectedItem = combo.getSelectedItem();
        if (!(selectedItem instanceof ComboItem)) {
            return null;
        }
        return (ComboItem<T>) selectedItem;
    }

    private void openSelectedAnimalDetail() {
        if (containerView == null) {
            return;
        }
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = resultsTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= (model == null ? 0 : model.size())) {
            return;
        }

        AnimalDTO animal = animalService.findById(model.get(modelRow).getId());
        if (animal == null) {
            return;
        }

        AnimalCreateView animalView = new AnimalCreateView();
        animalView.setModel(animal);
        animalView.setEditable(false);
        animalView.setAgreeController(new AnimalSetEditableController(animalView));
        containerView.addClosableTab(buildViewTitle(animal), animalView);
    }

    private String buildViewTitle(AnimalDTO animal) {
        if (animal.getNombre() != null && !animal.getNombre().trim().isEmpty()) {
            return animal.getNombre();
        }
        if (animal.getCrotal() != null && !animal.getCrotal().trim().isEmpty()) {
            return animal.getCrotal();
        }
        return "Detalle Animal";
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
