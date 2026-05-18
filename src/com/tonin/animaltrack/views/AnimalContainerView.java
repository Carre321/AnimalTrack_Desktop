package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.tonin.animaltrack.ui.MainWindow;

public class AnimalContainerView extends AbstractView implements FarmFilterAware {

    private JTabbedPane contentTabbedPanel;

    public AnimalContainerView() {
        initialize();
        AnimalSearchView buscarPanel = new AnimalSearchView(this);
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

        JButton nuevoAnimalButton = new JButton("Nuevo Animal");
        nuevoAnimalButton.setEnabled(MainWindow.getInstance().getPermissions().canCreateAnimal());
        nuevoAnimalButton.setIcon(
                new ImageIcon(AnimalContainerView.class.getResource("/animaltrack/icons/32/add-new.png")));
        nuevoAnimalButton.addActionListener(e -> {
            AnimalCreateView createView = new AnimalCreateView();
            addClosableTab(createView.getName(), createView);
        });
        buttonPanel.add(nuevoAnimalButton);

        JButton buscarAnimalButton = new JButton("Buscar Animal");
        buscarAnimalButton.addActionListener(e -> {
            AnimalSearchView searchView = new AnimalSearchView(this);
            addClosableTab(searchView.getName(), searchView);
        });
        buscarAnimalButton
                .setIcon(new ImageIcon(AnimalContainerView.class.getResource("/animaltrack/icons/32/search.png")));
        buttonPanel.add(buscarAnimalButton);

        contentTabbedPanel = new JTabbedPane(JTabbedPane.TOP);
        mainPanel.add(contentTabbedPanel, BorderLayout.CENTER);
    }

    public void addClosableTab(String title, AbstractView view) {
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

