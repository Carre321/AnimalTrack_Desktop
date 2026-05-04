package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class VeterinarioContainerView extends View implements FarmFilterAware {

    private JTabbedPane contentTabbedPanel;

    public VeterinarioContainerView() {
        initialize();
        VeterinarioSearchView buscarPanel = new VeterinarioSearchView(this);
        addClosableTab(buscarPanel.getName(), buscarPanel);
    }

    private void initialize() {
        setLayout(new BorderLayout(0, 0));

        JPanel mainPanel = new JPanel();
        add(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        JButton nuevoVeterinarioButton = new JButton("Nuevo Veterinario");
        nuevoVeterinarioButton.setIcon(new ImageIcon(
                VeterinarioContainerView.class.getResource("/nuvola/32x32/1727_add_add.png")));
        nuevoVeterinarioButton.addActionListener(e -> {
            VeterinarioCreateView createView = new VeterinarioCreateView();
            addClosableTab(createView.getName(), createView);
        });
        buttonPanel.add(nuevoVeterinarioButton);

        JButton buscarVeterinarioButton = new JButton("Buscar Veterinario");
        buscarVeterinarioButton.addActionListener(e -> {
            VeterinarioSearchView searchView = new VeterinarioSearchView(this);
            addClosableTab(searchView.getName(), searchView);
        });
        buscarVeterinarioButton.setIcon(new ImageIcon(
                VeterinarioContainerView.class.getResource("/nuvola/32x32/1746_find_find.png")));
        buttonPanel.add(buscarVeterinarioButton);

        contentTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.add(contentTabbedPanel, BorderLayout.CENTER);
    }

    public void addClosableTab(String title, View view) {
        contentTabbedPanel.addTab(title, view);
        int index = contentTabbedPanel.indexOfComponent(view);
        if (index != -1) {
            contentTabbedPanel.setTabComponentAt(index, new CerrarTab(contentTabbedPanel, title));
        }
        contentTabbedPanel.setSelectedComponent(view);
        contentTabbedPanel.revalidate();
        contentTabbedPanel.repaint();
    }

    @Override
    public void refreshForSelectedFarm() {
        for (int i = 0; i < contentTabbedPanel.getTabCount(); i++) {
            java.awt.Component component = contentTabbedPanel.getComponentAt(i);
            if (component instanceof FarmFilterAware) {
                ((FarmFilterAware) component).refreshForSelectedFarm();
            }
        }
    }
}

