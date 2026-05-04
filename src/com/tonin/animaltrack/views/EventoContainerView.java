package com.tonin.animaltrack.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class EventoContainerView extends View implements FarmFilterAware {

    private JTabbedPane contentTabbedPanel;

    public EventoContainerView() {
        initialize();
        EventoSearchView buscarPanel = new EventoSearchView(this);
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

        JButton nuevoEventoButton = new JButton("Nuevo Evento");
        nuevoEventoButton.setIcon(new ImageIcon(
                EventoContainerView.class.getResource("/nuvola/32x32/1727_add_add.png")));
        nuevoEventoButton.addActionListener(e -> {
            EventoCreateView createView = new EventoCreateView();
            addClosableTab(createView.getName(), createView);
        });
        buttonPanel.add(nuevoEventoButton);

        JButton buscarEventoButton = new JButton("Buscar Evento");
        buscarEventoButton.addActionListener(e -> {
            EventoSearchView searchView = new EventoSearchView(this);
            addClosableTab(searchView.getName(), searchView);
        });
        buscarEventoButton
                .setIcon(new ImageIcon(EventoContainerView.class.getResource("/nuvola/32x32/1746_find_find.png")));
        buttonPanel.add(buscarEventoButton);

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
