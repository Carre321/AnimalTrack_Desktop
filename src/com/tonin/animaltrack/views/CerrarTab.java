package com.tonin.animaltrack.views;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class CerrarTab extends JPanel {

    private static final long serialVersionUID = 1L;

    public CerrarTab(JTabbedPane tabbedPane, String title) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JLabel titleLabel = new JLabel(title + " ");
        add(titleLabel);

        JButton closeButton = new JButton("x");
        closeButton.setFocusable(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfTabComponent(CerrarTab.this);
            if (index != -1) {
                tabbedPane.remove(index);
            }
        });
        add(closeButton);
    }
}
 